package fr.isen.david.androiderestaurant.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.isen.david.androiderestaurant.R
import fr.isen.david.androiderestaurant.domain.Item

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
class ItemAdapter(private val items: List<Item>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

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

        Log.i(TAG, items.size.toString())
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return items.size
    }

    companion object {
        const val TAG = "ItemAdapter"
    }
}