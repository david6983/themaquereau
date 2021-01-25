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
import fr.isen.david.themaquereau.adapters.ItemAdapter
import fr.isen.david.themaquereau.databinding.ActivityEntreesBinding
import fr.isen.david.themaquereau.model.domain.Data
import fr.isen.david.themaquereau.model.domain.Item
import org.json.JSONObject

class DishesListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntreesBinding

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
        val rvItems = findViewById<RecyclerView>(R.id.itemRecyclerView)

        // initialise gson
        val gson = Gson()

        val items = listOf(
            Item(
                0,
                "soupe de poisson",
                "",
                0,
                "",
                "",
                listOf(),
                listOf(),
                listOf()
            ),
            Item(
                1,
                "cordon bleu a la truffe",
                "",
                0,
                "",
                "",
                listOf(),
                listOf(),
                listOf()
            )
        )

        val params = JSONObject()
        params.put("id_shop", "1")

        val queue = Volley.newRequestQueue(this)

        // Request a string response from the provided URL.
        val req = JsonObjectRequest(
            Request.Method.POST, API_URL, params,
            Response.Listener<JSONObject> { response ->
                Log.d(TAG, "Response: $response")
                val data = gson.fromJson(response["data"].toString(), List::class.java)
                Log.d(TAG, "Gson response: $data")
                val entrees = data[0].toString()
                Log.d(TAG, "Entrees: $entrees")
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Error: ${error.message}")
            })

        // Add the request to the RequestQueue.
        queue.add(req)

        val adapter = ItemAdapter(items, applicationContext)
        rvItems.adapter = adapter
        rvItems.layoutManager = LinearLayoutManager(this)
    }

    companion object {
        val TAG: String = DishesListActivity::class.java.simpleName
        const val API_URL = "http://test.api.catering.bluecodegames.com/menu"
    }
}