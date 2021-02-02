package fr.isen.david.themaquereau.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.david.themaquereau.*
import fr.isen.david.themaquereau.databinding.LayoutDishCardBinding
import fr.isen.david.themaquereau.model.domain.Item

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
class ItemAdapter(
    private var items: List<Item>,
    private val category: Int,
    private val context: Context
) : RecyclerView.Adapter<ItemAdapter.ItemHolder>() {

    inner class ItemHolder(binding: LayoutDishCardBinding) : RecyclerView.ViewHolder(binding.root) {
        val dishNameView: TextView = binding.dishName
        val dishImage: ImageView = binding.dishImage
        val dishPrice: TextView = binding.dishPrice
        val layout = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ItemHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView = LayoutDishCardBinding.inflate(inflater, parent, false)
        return ItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemAdapter.ItemHolder, position: Int) {
        // Get the data model based on position
        val item: Item = items[position]
        // Set item views based on your views and data model
        val textView = holder.dishNameView
        textView.text = item.name_fr
        // Image
        renderImage(item, holder)
        // Price
        item.prices.isNotEmpty().let {
            // Extract the first price only ('from ...')
            holder.dishPrice.text = item.prices[0].price.toString()
        }
        // listener on the item
        holder.layout.setOnClickListener {
            itemClickCallback(item)
        }
    }

    private fun itemClickCallback(item: Item) {
        // intent with external context
        val intent = Intent(context, DishDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        // give the id of the item to the next activity to retrieve it from the API
        intent.putExtra(ITEM, item)
        intent.putExtra(CATEGORY, category)
        context.startActivity(intent)
    }

    private fun renderImage(item: Item, holder: ItemHolder) {
        val picasso = Picasso.get()
        if (item.images.first().isNotEmpty()) {
            picasso
                .load(item.images.first())
                .resize(IMAGE_WIDTH, IMAGE_WIDTH)
                .into(holder.dishImage)
        } else {
            picasso
                .load(R.drawable.maquereau_not_found)
                .resize(IMAGE_WIDTH, IMAGE_WIDTH)
                .into(holder.dishImage)
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return items.size
    }
}