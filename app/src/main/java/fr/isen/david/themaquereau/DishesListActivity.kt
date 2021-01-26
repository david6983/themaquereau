package fr.isen.david.themaquereau

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.google.gson.GsonBuilder
import fr.isen.david.themaquereau.adapters.ItemAdapter
import fr.isen.david.themaquereau.databinding.ActivityDishListBinding
import fr.isen.david.themaquereau.model.domain.Data
import fr.isen.david.themaquereau.model.domain.Item
import org.json.JSONObject

class DishesListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDishListBinding
    private lateinit var rvItems: RecyclerView
    private lateinit var data: Data
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
    private val params = JSONObject()
    private var category = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDishListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        params.put("id_shop", "1")

        // The list is not visible until the content is loaded
        binding.itemRecyclerView.isVisible = false

        // Get the category number
        intent.extras?.getInt(HomeActivity.CATEGORY)?.let {
            category = it
        }

        loadData()
    }

    private fun loadData() {
        // Setting cache
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val queue = RequestQueue(cache, network).apply {
            start()
        }
        // Or perform the request if no data found
        // Request a string response from the provided URL.
        val req = JsonObjectRequest(
            Request.Method.POST, API_URL, params,
            Response.Listener<JSONObject> { response ->
                Log.d(TAG, "Response: $response")
                val dataList = gson.fromJson(response["data"].toString(), Array<Data>::class.java)
                data = dataList[category]
                binding.categoryText.text = data.name_fr

                // Retrieve the recycler view
                this.rvItems = binding.itemRecyclerView

                // Recycler view adapter
                val adapter = ItemAdapter(data.items, applicationContext)
                rvItems.adapter = adapter
                rvItems.layoutManager = LinearLayoutManager(this)

                binding.itemRecyclerView.isVisible = true
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Error: ${error.message}")
            })

        // Reset the cache
        queue.cache.clear()

        // Add the request to the RequestQueue.
        queue.add(req)
    }

    companion object {
        val TAG: String = DishesListActivity::class.java.simpleName
        const val API_URL = "http://test.api.catering.bluecodegames.com/menu"
    }
}