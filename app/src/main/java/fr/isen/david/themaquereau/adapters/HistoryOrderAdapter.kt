package fr.isen.david.themaquereau.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import fr.isen.david.themaquereau.databinding.LayoutHistoryOrderCardBinding
import fr.isen.david.themaquereau.model.domain.HistoryOrder
import fr.isen.david.themaquereau.model.domain.Order
import fr.isen.david.themaquereau.util.displayToast

class HistoryOrderAdapter(
    private var orders: List<HistoryOrder>,
    private val context: Context
) : RecyclerView.Adapter<HistoryOrderAdapter.HistoryOrderHolder>() {
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

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
        //TODO add a view pager 2 with titles
        val order: HistoryOrder = orders[position]
        //val messageOrder = parseMessage(order.message)[0]
        holder.receiver.text = order.receiver
        holder.price.text = "13"
        holder.date.text = order.create_date.toString() //TODO format date
        // listener on the item
        holder.layout.setOnClickListener {
            clickCallback(order)
        }
    }

    private fun parseMessage(message: String): List<Order> {
        return gson.fromJson(message, Array<Order>::class.java).toList()
    }

    private fun clickCallback(order: HistoryOrder) {
        displayToast(order.toString(), context)
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}