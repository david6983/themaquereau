package fr.isen.david.themaquereau.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import fr.isen.david.themaquereau.databinding.LayoutHistoryOrderCardBinding
import fr.isen.david.themaquereau.model.domain.HistoryOrder
import fr.isen.david.themaquereau.model.domain.Order
import java.text.SimpleDateFormat
import java.util.*

class HistoryOrderAdapter(
    private var orders: List<HistoryOrder>
) : RecyclerView.Adapter<HistoryOrderAdapter.HistoryOrderHolder>() {
    inner class HistoryOrderHolder(binding: LayoutHistoryOrderCardBinding) : RecyclerView.ViewHolder(binding.root) {
        val date: TextView = binding.orderDate
        val price: TextView = binding.orderPrice
        val receiver: TextView = binding.receiver
        val layout = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryOrderAdapter.HistoryOrderHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val orderView = LayoutHistoryOrderCardBinding.inflate(inflater, parent, false)
        return HistoryOrderHolder(orderView)
    }

    override fun onBindViewHolder(holder: HistoryOrderHolder, position: Int) {
        //TODO handle no previous orders
        val order: HistoryOrder = orders[position]
        holder.receiver.text = order.receiver
        val subOrders = parseMessage(order.message)
        val total = subOrders.sumByDouble { it.realPrice }
        holder.price.text = total.toString()
        val formatter = SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRENCH);
        holder.date.text = formatter.format(order.create_date)
    }

    private fun parseMessage(message: String): Array<Order> {
        return Gson().fromJson(message, Array<Order>::class.java)
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}