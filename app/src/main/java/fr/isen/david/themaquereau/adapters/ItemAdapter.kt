package fr.isen.david.themaquereau.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.isen.david.themaquereau.DishDetailActivity
import fr.isen.david.themaquereau.R
import fr.isen.david.themaquereau.model.domain.Item

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
class ItemAdapter(
    private val items: List<Item>,
    private val context: Context
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val nameTextView: TextView = itemView.findViewById(R.id.dishName)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.item_dish, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ItemAdapter.ViewHolder, position: Int) {
        // Get the data model based on position
        val item: Item = items[position]
        // Set item views based on your views and data model
        val textView = viewHolder.nameTextView
        textView.text = item.name_fr
        // listener on the item
        textView.setOnClickListener {
            // intent with external context
            val intent = Intent(context, DishDetailActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            // give the id of the item to the next activity to retrieve it from the API
            intent.putExtra(ITEM_ID, item.id)
            context.startActivity(intent)
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return items.size
    }

    companion object {
        const val TAG = "ItemAdapter"
        const val ITEM_ID = "item_id"
    }
}