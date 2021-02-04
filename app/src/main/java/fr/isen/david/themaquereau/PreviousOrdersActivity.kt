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
import fr.isen.david.themaquereau.helpers.ApiHelperImpl
import fr.isen.david.themaquereau.helpers.AppPreferencesHelperImpl
import fr.isen.david.themaquereau.model.domain.HistoryOrder
import org.json.JSONObject
import org.koin.android.ext.android.inject

class PreviousOrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreviousOrdersBinding
    private val preferencesImpl: AppPreferencesHelperImpl by inject()
    private val api: ApiHelperImpl by inject()
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviousOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = preferencesImpl.getClientId()
        api.listPreviousOrders(userId, onReceiveHistoryOrders)
    }

    private val onReceiveHistoryOrders = { data: Array<HistoryOrder> ->
        val rvHistory = binding.historyView
        val adapter = HistoryOrderAdapter(data.toList(), applicationContext)
        rvHistory.adapter = adapter
        rvHistory.layoutManager = LinearLayoutManager(this)
    }

    companion object {
        val TAG = PreviousOrdersActivity::class.java.simpleName
    }
}