package fr.isen.david.themaquereau

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import fr.isen.david.themaquereau.adapters.HistoryOrderAdapter
import fr.isen.david.themaquereau.databinding.ActivityPreviousOrdersBinding
import fr.isen.david.themaquereau.helpers.AppPreferencesHelper
import fr.isen.david.themaquereau.model.domain.HistoryOrder
import org.json.JSONObject
import org.koin.android.ext.android.inject

class PreviousOrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreviousOrdersBinding
    private val preferences: AppPreferencesHelper by inject()
    private var userId: Int = -1
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviousOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = preferences.getClientId()
        listOrders()
    }

    private fun listOrders() {
        val queue = Volley.newRequestQueue(this)
        // params
        val params = JSONObject()
        params.put("id_shop", "1")
        params.put("id_user", userId)
        Log.i(TAG, "Sending params: $params")
        val req = JsonObjectRequest(
            Request.Method.POST, API_LIST_ORDER_URL, params,
            Response.Listener { response ->
                Log.d(SignInActivity.TAG, "List Order Response: $response")
                gson.fromJson(response["data"].toString(), Array<HistoryOrder>::class.java).let {
                    val rvHistory = binding.historyView
                    val adapter = HistoryOrderAdapter(it.toList(), applicationContext)
                    rvHistory.adapter = adapter
                    rvHistory.layoutManager = LinearLayoutManager(this)
                }
            },
            Response.ErrorListener { error ->
                Log.e(DishesListActivity.TAG, "Error: ${error.message}")
            })
        queue.add(req)
    }

    companion object {
        val TAG = PreviousOrdersActivity::class.java.simpleName
    }
}