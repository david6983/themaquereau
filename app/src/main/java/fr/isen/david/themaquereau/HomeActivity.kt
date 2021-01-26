package fr.isen.david.themaquereau

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import fr.isen.david.themaquereau.adapters.ItemAdapter
import fr.isen.david.themaquereau.databinding.ActivityHomeBinding
import fr.isen.david.themaquereau.model.domain.Data
import org.json.JSONObject

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding // Best practise instead of findViewById
    private var data: Array<Data> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        loadData()

        val intent = Intent(this, DishesListActivity::class.java)
        binding.homeEntreeButton.setOnClickListener {
            Log.i(TAG, "data: ${data[0]}")
            intent.putExtra(DATA, data[0])
            startActivity(intent)
        }
        binding.homePlatsButton.setOnClickListener {
            Log.i(TAG, "data: ${data[1]}")
            intent.putExtra(DATA, data[1])
            startActivity(intent)
        }
        binding.homeDesertsButton.setOnClickListener {
            Log.i(TAG, "data: ${data[2]}")
            intent.putExtra(DATA, data[2])
            startActivity(intent)
        }
    }

    private fun loadData() {
        // initialise gson
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

        val params = JSONObject()
        params.put("id_shop", "1")

        val queue = Volley.newRequestQueue(this)

        // Request a string response from the provided URL.
        val req = JsonObjectRequest(
            Request.Method.POST, API_URL, params,
            Response.Listener<JSONObject> { response ->
                Log.d(DishesListActivity.TAG, "Response: $response")
                data = gson.fromJson(response["data"].toString(), Array<Data>::class.java)
            },
            Response.ErrorListener { error ->
                Log.e(DishesListActivity.TAG, "Error: ${error.message}")
            })

        // Add the request to the RequestQueue.
        queue.add(req)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, " destroyed") // log the destroy cycle
    }

    companion object {
        val TAG: String = HomeActivity::class.java.simpleName
        const val API_URL = "http://test.api.catering.bluecodegames.com/menu"
        const val DATA = "data"
    }
}