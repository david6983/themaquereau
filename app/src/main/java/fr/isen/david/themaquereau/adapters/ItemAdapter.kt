package fr.isen.david.themaquereau.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.david.themaquereau.DishDetailActivity
import fr.isen.david.themaquereau.R
import fr.isen.david.themaquereau.model.domain.Item

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
class ItemAdapter(
    private val items: List<Item>,
    private val context: Context
) : RecyclerView.Adapter<ItemAdapter.ItemHolder>() {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ItemHolder(val layout: View) : RecyclerView.ViewHolder(layout) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        //TODO replace findViewById by binding
        val dishNameView: TextView = itemView.findViewById(R.id.dishName)
        val dishImage: ImageView = itemView.findViewById(R.id.dishImage)
        val dishPrice: TextView = itemView.findViewById(R.id.dishPrice)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ItemHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.layout_dish_card, parent, false)
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
        val picasso = Picasso.get()
        if (item.images.size > 1) {
            picasso
                .load(item.images[0])
                .resize(400, 400)
                .into(holder.dishImage)
        } else {
            picasso
                .load(R.drawable.maquereau_not_found)
                .resize(400, 400)
                .into(holder.dishImage)
        }
        // Price
        if (item.prices.isNotEmpty()) {
            // Extract the first price only ('from ...')
            holder.dishPrice.text = item.prices[0].price.toString()
        }

        // listener on the item
        //TODO extract this function in the activity
        holder.layout.setOnClickListener {
            // intent with external context
            val intent = Intent(context, DishDetailActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            // give the id of the item to the next activity to retrieve it from the API
            //TODO send the entire information
            intent.putExtra(ITEM, item.id)
            context.startActivity(intent)
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return items.size
    }

    companion object {
        val TAG = ItemAdapter::class.java.simpleName
        const val ITEM = "item"
    }
}