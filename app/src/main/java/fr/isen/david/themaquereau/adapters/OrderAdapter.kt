package fr.isen.david.themaquereau.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import fr.isen.david.themaquereau.*
import fr.isen.david.themaquereau.databinding.LayoutOrderBasketBinding
import fr.isen.david.themaquereau.model.domain.Order
import fr.isen.david.themaquereau.util.displayToast
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException

class OrderAdapter(
    private var orders: MutableList<Order>,
    val context: Context,
    private val userId: Int
) : RecyclerView.Adapter<OrderAdapter.OrderHolder>() {
    private var recentlyDeletedOrderPosition: Int = -1
    private lateinit var recentlyDeletedOrder: Order
    private lateinit var binding: LayoutOrderBasketBinding
    private lateinit var view: View

    inner class OrderHolder(binding: LayoutOrderBasketBinding) : RecyclerView.ViewHolder(binding.root) {
        val dishName = binding.dishName
        val quantity = binding.quantityBasketOrder
        val realPrice = binding.realPrice
        val dishImage = binding.dishImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.OrderHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        binding = LayoutOrderBasketBinding.inflate(inflater, parent, false)
        view = binding.root
        return OrderHolder(binding)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderAdapter.OrderHolder, position: Int) {
        // Get the data model based on the position
        val order: Order = orders[position]
        // Set the name of the dish
        val textView = holder.dishName
        textView.text = order.item.name_fr
        // Image
        renderImage(order, holder)
        // Price
        holder.realPrice.text = order.realPrice.toString()
        // Quantity
        holder.quantity.text = order.quantity.toString()

    }

    private fun renderImage(order: Order, holder: OrderHolder) {
        val picasso = Picasso.get()
        if (order.item.images.first().isNotEmpty()) {
            picasso
                .load(order.item.images.first())
                .resize(IMAGE_WIDTH, IMAGE_WIDTH)
                .into(holder.dishImage)
        } else {
            picasso
                .load(R.drawable.maquereau_not_found)
                .resize(IMAGE_WIDTH, IMAGE_WIDTH)
                .into(holder.dishImage)
        }
    }

    fun deleteItem(position: Int) {
        recentlyDeletedOrder = orders[position]
        recentlyDeletedOrderPosition = position
        orders.removeAt(position)
        // remove it in the file
        deleteOrder(position)
        // notify the recycler view
        notifyItemRemoved(position)
        showUndoSnackbar()
    }

    private fun deleteOrder(position: Int) {
        try {
            context.openFileInput("$ORDER_FILE$userId$ORDER_FILE_SUFFIX").use { inputStream ->
                inputStream.bufferedReader().use { reader ->
                    val gson = Gson()
                    val ordersFromFile = retrieveOrders(reader, gson)
                    // delete the order
                    ordersFromFile.removeAt(position)
                    val ordersToFile = gson.toJson(ordersFromFile)
                    // save the file again
                    context.openFileOutput("$ORDER_FILE$userId$ORDER_FILE_SUFFIX", Context.MODE_PRIVATE).use { outputStream ->
                        outputStream.write(ordersToFile.toString().toByteArray())
                    }
                    Log.i(DishDetailsActivity.TAG, "deleted order from basket: $ordersToFile")

                    // Update shared preferences
                    val sharedPref = context.getSharedPreferences(
                        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    // add the quantity to the previous quantity
                    with(sharedPref.edit()) {
                        putInt(QUANTITY_KEY, ordersFromFile.sumBy { it.quantity })
                        apply()
                    }
                }
            }
        } catch(e: FileNotFoundException) {
            // Alert the user that there are no orders yet
            displayToast("cannot retrieve orders", view.context)
        }
    }

    private fun retrieveOrders(reader: BufferedReader, gson: Gson): MutableList<Order> {
        return gson.fromJson(reader.readText(), Array<Order>::class.java).toMutableList()
    }

    private fun showUndoSnackbar() {
        val snackbar = Snackbar.make(view, R.string.deleted_order, Snackbar.LENGTH_LONG)
        snackbar.setAction(R.string.deleted_order) {
            undoDelete()
        }
        snackbar.show()
    }

    private fun undoDelete() {
        orders.add(recentlyDeletedOrderPosition, recentlyDeletedOrder)
        notifyItemInserted(recentlyDeletedOrderPosition)
    }
}