package fr.isen.david.themaquereau

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.withTransaction
import com.google.gson.Gson
import fr.isen.david.themaquereau.adapters.OrderAdapter
import fr.isen.david.themaquereau.databinding.ActivityBasketBinding
import fr.isen.david.themaquereau.helpers.ApiHelperImpl
import fr.isen.david.themaquereau.helpers.AppPreferencesHelperImpl
import fr.isen.david.themaquereau.helpers.PersistOrdersHelperImpl
import fr.isen.david.themaquereau.helpers.SwipeToDeleteCallback
import fr.isen.david.themaquereau.model.database.AppDatabase
import fr.isen.david.themaquereau.model.domain.Order
import fr.isen.david.themaquereau.util.displayToast
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class BasketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBasketBinding
    private var orders: MutableList<Order> = mutableListOf()
    private lateinit var rvOrders: RecyclerView
    private var userId: Int = -1

    private val preferencesImpl: AppPreferencesHelperImpl by inject()
    private val api: ApiHelperImpl by inject()
    private val persistence: PersistOrdersHelperImpl by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasketBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // progress bar not visible
        binding.orderProgress.isVisible = false
        // get the client id
        userId = preferencesImpl.getClientId()
        // Retrieve the orders from file if exist
        retrieveOrders()
        // Listener finalise order
        binding.finalOrderButton.setOnClickListener {
            finalOrderCallback()
        }
    }

    private fun retrieveOrders() {
        if (userId != -1) {
            persistence.readOrders(userId, renderOrders, noOrdersErrorCallback)
            Log.i(TAG, "read orders: $orders")
        } else {
            redirectToSignIn()
        }
    }

    private fun redirectToSignIn() {
        val signInIntent = Intent(this, SignInActivity::class.java)
        // Get the category number to display the right parent view
        intent.extras?.getInt(CATEGORY)?.let {
            signInIntent.putExtra(CATEGORY, it)
        } ?:run {
            intent.extras?.getSerializable(ITEM)?.let {
                signInIntent.putExtra(ITEM, it)
            }
        }
        startActivity(signInIntent)
    }

    private val noOrdersErrorCallback = {
        // Alert the user that there are no orders yet
        displayToast(getString(R.string.no_orders_found), applicationContext)
        // redirect to the parent activity
        val intent = getParentActivityIntentImpl()
        startActivity(intent)
    }

    private val renderOrders = { ordersList: MutableList<Order> ->
        orders = ordersList
        // Render the orders in the recycle view
        rvOrders = binding.orderList
        val adapter = OrderAdapter(
            ordersList, applicationContext, userId, preferencesImpl, persistence)
        rvOrders.adapter = adapter
        rvOrders.layoutManager = LinearLayoutManager(this)
        // Add our touch helper
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(rvOrders)
    }

    private fun finalOrderCallback() {
        // Convert to JsonArray the orders
        val finalOrder = Gson().toJson(orders)
        Log.i(TAG, "The final order is $finalOrder")
        // Save the order in a database
        lifecycleScope.launch {
            whenStarted {
                persistFinalOrderToDb()
            }
        }
        // Save the order to the api
        api.saveFinalOrder(finalOrder, userId, finalOrderSavedCallback, binding.orderProgress)
    }

    private val finalOrderSavedCallback = { receiver: String ->
        displayToast("$receiver ${getString(R.string.received_your_order)}", applicationContext)
        resetBasket()
    }

    private fun resetBasket() {
        // delete file
        applicationContext.deleteFile("$ORDER_FILE$userId$ORDER_FILE_SUFFIX")
        // reset quantity
        preferencesImpl.setQuantity(0)
        // redirect
        val intent = getParentActivityIntentImpl()
        startActivity(intent)
    }

    private suspend fun persistFinalOrderToDb() {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, AppDatabase.DB_NAME
        ).build().let {
            it.withTransaction {
                val orderDao = it.orderDao()
                orderDao.insertAll(orders)
                Log.i(DishDetailsActivity.TAG, "order saved to database")
            }
        }
    }

    override fun getSupportParentActivityIntent(): Intent? {
        return getParentActivityIntentImpl()
    }

    override fun getParentActivityIntent(): Intent? {
        return getParentActivityIntentImpl()
    }

    private fun getParentActivityIntentImpl(): Intent {
        val parentIntent = Intent(this, HomeActivity::class.java)
        // Get the category number to display the right parent view
        intent.extras?.getInt(CATEGORY)?.let {
            parentIntent.putExtra(CATEGORY, it)
            parentIntent.setClass(this, DishesListActivity::class.java)

        }
        intent.extras?.getSerializable(ITEM)?.let {
            parentIntent.putExtra(ITEM, it)
            parentIntent.setClass(this, DishDetailsActivity::class.java)
        }
        return parentIntent
    }

    companion object {
        val TAG: String = BasketActivity::class.java.simpleName
    }
}