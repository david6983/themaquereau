package fr.isen.david.themaquereau

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.david.themaquereau.adapters.HistoryOrderAdapter
import fr.isen.david.themaquereau.databinding.ActivityPreviousOrdersBinding
import fr.isen.david.themaquereau.helpers.ApiHelperImpl
import fr.isen.david.themaquereau.helpers.AppPreferencesHelperImpl
import fr.isen.david.themaquereau.model.domain.HistoryOrder
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
        binding.historyView.isVisible = false
        api.listPreviousOrders(userId, onReceiveHistoryOrders)
    }

    private val onReceiveHistoryOrders = { data: Array<HistoryOrder> ->
        if (data.isNotEmpty()) {
            val rvHistory = binding.historyView
            val adapter = HistoryOrderAdapter(data.toList())
            rvHistory.adapter = adapter
            rvHistory.layoutManager = LinearLayoutManager(this)
            rvHistory.isVisible = true
            binding.noOrdersText.isVisible = false
        }
    }

    companion object {
        val TAG: String = PreviousOrdersActivity::class.java.simpleName
    }
}