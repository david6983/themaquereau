package fr.isen.david.themaquereau.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.david.themaquereau.DishDetailsActivity
import fr.isen.david.themaquereau.HomeActivity
import fr.isen.david.themaquereau.R
import fr.isen.david.themaquereau.databinding.LayoutDishCardBinding
import fr.isen.david.themaquereau.model.domain.Item

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
class ItemAdapter(
    private var items: List<Item>,
    private val category: Int,
    private val context: Context
) : RecyclerView.Adapter<ItemAdapter.ItemHolder>() {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ItemHolder(binding: LayoutDishCardBinding) : RecyclerView.ViewHolder(binding.root) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val dishNameView: TextView = binding.dishName
        val dishImage: ImageView = binding.dishImage
        val dishPrice: TextView = binding.dishPrice
        val layout = binding.root
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ItemHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = LayoutDishCardBinding.inflate(inflater, parent, false)
        // Return a new holder instance
        return ItemHolder(contactView)
    }

    // Involves populating data into the item through holder
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
        intent.putExtra(HomeActivity.CATEGORY, category)
        context.startActivity(intent)
    }

    private fun renderImage(item: Item, holder: ItemHolder) {
        val picasso = Picasso.get()
        if (item.images.first().isNotEmpty()) {
            picasso
                .load(item.images.first())
                .resize(400, 400)
                .into(holder.dishImage)
        } else {
            picasso
                .load(R.drawable.maquereau_not_found)
                .resize(400, 400)
                .into(holder.dishImage)
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return items.size
    }

    companion object {
        const val ITEM = "item"
    }
}