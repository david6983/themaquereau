package fr.isen.david.themaquereau

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import fr.isen.david.themaquereau.adapters.ItemAdapter
import fr.isen.david.themaquereau.databinding.ActivityEntreesBinding
import fr.isen.david.themaquereau.model.domain.Data
import fr.isen.david.themaquereau.model.domain.Item
import org.json.JSONObject

class DishesListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntreesBinding
    private var items: List<Item> = listOf()
    private lateinit var rvItems: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntreesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // get the category and display the corresponding text
        intent.extras?.getInt(HomeActivity.CATEGORY)?.let {
            binding.categoryText.setText(it)
        }

        // Retrieve the recycler view
        this.rvItems = binding.itemRecyclerView

        // initialise gson
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

        val params = JSONObject()
        params.put("id_shop", "1")

        val queue = Volley.newRequestQueue(this)

        // Request a string response from the provided URL.
        val req = JsonObjectRequest(
            Request.Method.POST, API_URL, params,
            Response.Listener<JSONObject> { response ->
                Log.d(TAG, "Response: $response")
                val data: Array<Data> = gson.fromJson(response["data"].toString(), Array<Data>::class.java)
                this.items = data[0].items
                Log.d(TAG, "entrees: $items")

                // Recycler view adapter
                val adapter = ItemAdapter(this.items, applicationContext)
                rvItems.adapter = adapter
                rvItems.layoutManager = LinearLayoutManager(this)
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Error: ${error.message}")
            })

        // Add the request to the RequestQueue.
        queue.add(req)
    }

    companion object {
        val TAG: String = DishesListActivity::class.java.simpleName
        const val API_URL = "http://test.api.catering.bluecodegames.com/menu"
    }
}