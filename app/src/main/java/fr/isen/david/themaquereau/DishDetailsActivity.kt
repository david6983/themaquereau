package fr.isen.david.themaquereau

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import fr.isen.david.themaquereau.adapters.ItemAdapter
import fr.isen.david.themaquereau.databinding.ActivityDishDetailsBinding
import fr.isen.david.themaquereau.fragments.DishImagesPagerFragment
import fr.isen.david.themaquereau.model.domain.Item
import fr.isen.david.themaquereau.model.domain.Order
import fr.isen.david.themaquereau.util.displayToast
import org.json.JSONObject
import java.io.File
import java.lang.NumberFormatException

class DishDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDishDetailsBinding
    private lateinit var order: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDishDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // get the selected item id
        intent.extras?.getSerializable(ItemAdapter.ITEM)?.let { serializedItem ->
            val item = serializedItem as Item
            binding.dishDetailName.text = item.name_fr
            // Create an order
            order = Order(0, item, 1 , 0.0)
            // Ingredients
            binding.dishDetailIngredients.text = item.ingredients.joinToString(", ") { it.name_fr }
            // Get quantity
            try {
                val quantity = Integer.parseInt(binding.quantity.text.toString())
                // Price
                if (item.prices.isNotEmpty()) {
                    // convert the price to int
                    order.realPrice = quantity * item.prices[0].price
                    binding.dishDetailPrice.text = order.realPrice.toString()
                }
            } catch (e: NumberFormatException) {
                displayToast("Cannot parse default value", applicationContext)
            }

            // Images Pager
            val pagerFragment = DishImagesPagerFragment()
            pagerFragment.arguments = Bundle().apply {
                // Give the item to the pager
                putSerializable(DishImagesPagerFragment.ARG_OBJECT, item)
            }
            // Replace the fragment by the new one
            supportFragmentManager.beginTransaction()
                .replace(R.id.dishPagerFragment, pagerFragment)
                .addToBackStack("DishImagesPagerFragment")
                .commit()

            // Number Input Listener
            binding.quantity.addTextChangedListener { txt ->
                try {
                    order.quantity = Integer.parseInt(txt.toString())
                    order.realPrice = order.quantity * item.prices[0].price
                    binding.dishDetailPrice.text = order.realPrice.toString()
                } catch (e: NumberFormatException) {
                    displayToast("no number", applicationContext)
                }
            }

            binding.fishImageButton.setOnClickListener { v ->
                val jsonOrder = Gson().toJson(order)
                // save json order to file
                applicationContext.openFileOutput(ORDER_FILE, Context.MODE_PRIVATE).use {
                    it.write(jsonOrder.toByteArray())
                }
                // Alert the user with a snack bar
                val snack = Snackbar.make(v, R.string.order_saved, Snackbar.LENGTH_SHORT)
                snack.show()
                Log.i(TAG, "order saved: $jsonOrder")
            }
        }
    }

    companion object {
        val TAG: String = DishDetailsActivity::class.java.simpleName
        const val ORDER_FILE: String = "basket"
    }
}