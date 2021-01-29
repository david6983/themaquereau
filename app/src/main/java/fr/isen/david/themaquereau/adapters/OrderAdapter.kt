package fr.isen.david.themaquereau.adapters

import android.content.Context
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.set
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import fr.isen.david.themaquereau.DishDetailsActivity
import fr.isen.david.themaquereau.R
import fr.isen.david.themaquereau.databinding.LayoutOrderBasketBinding
import fr.isen.david.themaquereau.model.domain.Order
import fr.isen.david.themaquereau.util.displayToast
import java.io.FileNotFoundException
import java.lang.NumberFormatException

class OrderAdapter(
    private var orders: MutableList<Order>,
    val context: Context
) : RecyclerView.Adapter<OrderAdapter.OrderHolder>() {
    private lateinit var recentlyDeletedOrder: Order
    private var previousQuantity: Int = 0
    private var recentlyDeletedOrderPosition: Int = -1
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
        val picasso = Picasso.get()
        if (order.item.images.first().isNotEmpty()) {
            picasso
                .load(order.item.images.first())
                .resize(400, 400)
                .into(holder.dishImage)
        } else {
            picasso
                .load(R.drawable.maquereau_not_found)
                .resize(400, 400)
                .into(holder.dishImage)
        }
        // Price
        holder.realPrice.text = order.realPrice.toString()
        // Quantity
        holder.quantity.text = order.quantity.toString()

    }

    private fun updateQuantity(position: Int, quantityValue: Editable?) {
        try {
            val inputQuantity = Integer.parseInt(quantityValue.toString())
            if (inputQuantity > 0) {
                // save the previous quantity
                previousQuantity = orders[position].quantity
                // update the new quantity
                orders[position].quantity = inputQuantity
                orders[position].realPrice =
                    orders[position].quantity * orders[position].item.prices[0].price
                binding.realPrice.text = orders[position].realPrice.toString()
                // save changes in file
                updateOrder(position)
            } else {
                displayToast("La quantité doit etre superieur a 0", context)
            }
        } catch (e: NumberFormatException) {
            displayToast("no number", context)
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

    private fun updateOrder(position: Int) {
        try {
            view.context.openFileInput(DishDetailsActivity.ORDER_FILE).use { inputStream ->
                inputStream.bufferedReader().use {
                    val gson = Gson()
                    val ordersJsonString = it.readText()
                    val ordersFromFile = gson.fromJson(ordersJsonString, Array<Order>::class.java).toMutableList()
                    // update the order
                    ordersFromFile[position] = orders[position]
                    val ordersToFile = gson.toJson(ordersFromFile)
                    // save the file again
                    view.context.applicationContext.openFileOutput(DishDetailsActivity.ORDER_FILE, Context.MODE_PRIVATE).use { outputStream ->
                        outputStream.write(ordersToFile.toString().toByteArray())
                        Log.i(DishDetailsActivity.TAG, "deleted order from basket: $ordersToFile")
                    }
                    // Update shared preferences
                    val sharedPref = context.getSharedPreferences(
                        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    val currentQuantity = sharedPref.getInt(DishDetailsActivity.QUANTITY_KEY, 0)
                    // add the quantity to the previous quantity
                    with(sharedPref.edit()) {
                        val orderNewQuantity = orders[position].quantity
                        var newQuantity = 0
                        // the user can decrease or increase the quantity
                        if (previousQuantity < orderNewQuantity) {
                            // if the user increase the number
                            newQuantity = orders[position].quantity - previousQuantity
                            putInt(DishDetailsActivity.QUANTITY_KEY, currentQuantity + newQuantity)
                            Log.d("OrderAdapter", "offset quantity: $newQuantity")
                        } else if (previousQuantity > orderNewQuantity) {
                            // if the user decrease the number
                            newQuantity = previousQuantity - orders[position].quantity
                            putInt(DishDetailsActivity.QUANTITY_KEY, currentQuantity - newQuantity)
                            Log.d("OrderAdapter", "offset quantity: $newQuantity")
                        }
                        apply()
                    }
                    notifyItemChanged(position)
                }
            }
        } catch(e: FileNotFoundException) {
            // Alert the user that there are no orders yet
            displayToast("cannot retrieve orders", view.context)
        }
    }

    private fun deleteOrder(position: Int) {
        try {
            view.context.openFileInput(DishDetailsActivity.ORDER_FILE).use { inputStream ->
                inputStream.bufferedReader().use {
                    val gson = Gson()
                    val ordersJsonString = it.readText()
                    val ordersFromFile = gson.fromJson(ordersJsonString, Array<Order>::class.java).toMutableList()
                    // delete the order
                    ordersFromFile.removeAt(position)
                    val ordersToFile = gson.toJson(ordersFromFile)
                    // save the file again
                    view.context.applicationContext.openFileOutput(DishDetailsActivity.ORDER_FILE, Context.MODE_PRIVATE).use { outputStream ->
                        outputStream.write(ordersToFile.toString().toByteArray())
                        Log.i(DishDetailsActivity.TAG, "deleted order from basket: $ordersToFile")
                    }
                    // Update shared preferences
                    val sharedPref = context.getSharedPreferences(
                        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    val currentQuantity = sharedPref.getInt(DishDetailsActivity.QUANTITY_KEY, 0)
                    // add the quantity to the previous quantity
                    with(sharedPref.edit()) {
                        putInt(DishDetailsActivity.QUANTITY_KEY, currentQuantity - recentlyDeletedOrder.quantity)
                        apply()
                    }
                }
            }
        } catch(e: FileNotFoundException) {
            // Alert the user that there are no orders yet
            displayToast("cannot retrieve orders", view.context)
        }
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