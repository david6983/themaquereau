package fr.isen.david.themaquereau

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.room.withTransaction
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.david.themaquereau.adapters.OrderAdapter
import fr.isen.david.themaquereau.databinding.ActivityBasketBinding
import fr.isen.david.themaquereau.helpers.SwipeToDeleteCallback
import fr.isen.david.themaquereau.model.database.AppDatabase
import fr.isen.david.themaquereau.model.domain.FinalOrderResponse
import fr.isen.david.themaquereau.model.domain.Order
import fr.isen.david.themaquereau.util.displayToast
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.FileNotFoundException

class BasketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBasketBinding
    private lateinit var orders: MutableList<Order>
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasketBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val gson = Gson()

        // progress bar not visible
        binding.orderProgress.isVisible = false

        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        userId = sharedPref.getInt(ID_CLIENT, -1)
        // Retrieve the orders from file if exist
        if (userId != -1) {
            try {
                applicationContext.openFileInput("$ORDER_FILE$userId$ORDER_FILE_SUFFIX").use { inputStream ->
                    inputStream.bufferedReader().use {
                        orders =
                            gson.fromJson(it.readText(), Array<Order>::class.java).toMutableList()
                        // Render the orders in the recycle view
                        renderOrders()
                    }
                }
            } catch (e: FileNotFoundException) {
                // Alert the user that there are no orders yet
                displayToast("no orders found", applicationContext)
                // redirect to the parent activity
                val intent = getParentActivityIntentImpl()
                startActivity(intent)
            }
        } else {
            // redirect to the login page
            val intent = Intent(this, SignInActivity::class.java)
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
        val adapter = OrderAdapter(orders, applicationContext, userId)
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
                persistFinalOrderToDb()
            }
        }
        // Save the order to the api
        saveToApi(1, finalOrder)
    }

    private fun saveToApi(idShop: Int, message: String) {
        val queue = Volley.newRequestQueue(this)
        // params
        val params = JSONObject()
        params.put("id_shop", idShop)
        params.put("id_user", userId)
        params.put("msg", message)
        //params.put()
        val req = JsonObjectRequest(
            Request.Method.POST, API_ORDER_URL, params,
            Response.Listener { response ->
                Log.d(SignInActivity.TAG, "Sent Order Response: $response")
                // reset basket

                // alert the user
                Gson().fromJson(response["data"].toString(), Array<FinalOrderResponse>::class.java).let {
                    displayToast("${it[0].receiver} a reçu votre commande", applicationContext)
                    // redirect to Home

                }
            },
            Response.ErrorListener { error ->
                Log.e(DishesListActivity.TAG, "Error: ${error.message}")
            })
        queue.add(req)
        binding.orderProgress.isVisible = true
        queue.addRequestFinishedListener<JsonObjectRequest> {
            // dismiss progress bar
            binding.orderProgress.isVisible = false
        }
    }

    private suspend fun persistFinalOrderToDb() {
        //TODO extract the db with koin library
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
        val TAG = BasketActivity::class.java.simpleName
    }
}