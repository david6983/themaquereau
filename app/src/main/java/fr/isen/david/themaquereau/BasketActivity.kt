package fr.isen.david.themaquereau

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.room.withTransaction
import com.google.gson.Gson
import fr.isen.david.themaquereau.adapters.OrderAdapter
import fr.isen.david.themaquereau.databinding.ActivityBasketBinding
import fr.isen.david.themaquereau.helpers.SwipeToDeleteCallback
import fr.isen.david.themaquereau.model.database.AppDatabase
import fr.isen.david.themaquereau.model.domain.Order
import fr.isen.david.themaquereau.util.displayToast
import kotlinx.coroutines.launch
import java.io.FileNotFoundException

class BasketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBasketBinding
    private lateinit var orders: MutableList<Order>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasketBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val gson = Gson()

        // Retrieve the orders from file if exist
        try {
            applicationContext.openFileInput(ORDER_FILE).use { inputStream ->
                inputStream.bufferedReader().use {
                    orders = gson.fromJson(it.readText(), Array<Order>::class.java).toMutableList()
                    // Render the orders in the recycle view
                    renderOrders()
                }
            }
        } catch(e: FileNotFoundException) {
            // Alert the user that there are no orders yet
            displayToast("no orders found", applicationContext)
            // redirect to the parent activity
            val intent = getParentActivityIntentImpl()
            startActivity(intent)
        }

        // Listener finalise order
        binding.finalOrderButton.setOnClickListener {
            finalOrderCallback(gson)
        }
    }

    private fun renderOrders() {
        // Render the orders in the recycle view
        val rvOrders = binding.orderList
        val adapter = OrderAdapter(orders, applicationContext)
        rvOrders.adapter = adapter
        rvOrders.layoutManager = LinearLayoutManager(this)
        // Add our touch helper
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(rvOrders)
    }

    private fun finalOrderCallback(gson: Gson) {
        // Convert to JsonArray the orders
        val finalOrder = gson.toJson(orders)
        Log.i(TAG, "The final order is $finalOrder")
        // Save the order in a database
        lifecycleScope.launch {
            whenStarted {
                persistFinalOrderToDb(orders)
            }
        }
    }

    private suspend fun persistFinalOrderToDb(finalOrder: List<Order>) {
        //TODO extract the db with koin library
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, AppDatabase.DB_NAME
        ).build().let {
            it.withTransaction {
                val orderDao = it.orderDao()
                orderDao.insertAll(finalOrder)
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
        val TAG = BasketActivity::class.java.simpleName
    }
}