package fr.isen.david.themaquereau

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import fr.isen.david.themaquereau.adapters.ItemAdapter
import fr.isen.david.themaquereau.adapters.OrderAdapter
import fr.isen.david.themaquereau.databinding.ActivityBasketBinding
import fr.isen.david.themaquereau.helpers.SwipeToDeleteCallback
import fr.isen.david.themaquereau.model.domain.Order
import fr.isen.david.themaquereau.util.displayToast
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
            applicationContext.openFileInput(DishDetailsActivity.ORDER_FILE).use { inputStream ->
                inputStream.bufferedReader().use {
                    val ordersJsonString = it.readText()
                    orders = gson.fromJson(ordersJsonString, Array<Order>::class.java).toMutableList()

                    // Render the orders in the recycle view
                    val rvOrders = binding.orderList
                    val adapter = OrderAdapter(orders, applicationContext)
                    rvOrders.adapter = adapter
                    rvOrders.layoutManager = LinearLayoutManager(this)

                    // Add our touch helper
                    val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
                    itemTouchHelper.attachToRecyclerView(rvOrders)
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
            // Convert to JsonArray the orders
            val finalOrder = gson.toJson(orders)
            Log.i(TAG, "The final order is $finalOrder")
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
        intent.extras?.getInt(HomeActivity.CATEGORY)?.let {
            parentIntent.putExtra(HomeActivity.CATEGORY, it)
            parentIntent.setClass(this, DishesListActivity::class.java)

        }
        intent.extras?.getSerializable(ItemAdapter.ITEM)?.let {
            parentIntent.putExtra(ItemAdapter.ITEM, it)
            parentIntent.setClass(this, DishDetailsActivity::class.java)
        }
        return parentIntent
    }

    companion object {
        val TAG = BasketActivity::class.java.simpleName
    }
}