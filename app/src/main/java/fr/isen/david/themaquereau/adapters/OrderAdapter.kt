package fr.isen.david.themaquereau.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import fr.isen.david.themaquereau.*
import fr.isen.david.themaquereau.databinding.LayoutOrderBasketBinding
import fr.isen.david.themaquereau.helpers.AppPreferencesHelperImpl
import fr.isen.david.themaquereau.helpers.PersistOrdersHelperImpl
import fr.isen.david.themaquereau.model.domain.Order
import fr.isen.david.themaquereau.util.displayToast

class OrderAdapter(
    private var orders: MutableList<Order>,
    val context: Context,
    private val userId: Int,
    private val preferencesImpl: AppPreferencesHelperImpl,
    private val persistence: PersistOrdersHelperImpl
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
        persistence.deleteOrder(userId, position, updateSharedPrefCallback, errorDeleteCallBack)
        // notify the recycler view
        notifyItemRemoved(position)
        showUndoSnackbar()
    }

    private val updateSharedPrefCallback = { ordersFromFile: MutableList<Order> ->
        // Update shared preferences
        preferencesImpl.setQuantity(ordersFromFile.sumBy { it.quantity })
    }

    private val errorDeleteCallBack = {
        // Alert the user that there are no orders yet
        displayToast("cannot retrieve orders", view.context)
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