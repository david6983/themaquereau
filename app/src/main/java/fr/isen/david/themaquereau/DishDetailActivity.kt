package fr.isen.david.themaquereau

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.squareup.picasso.Picasso
import fr.isen.david.themaquereau.adapters.ItemAdapter
import fr.isen.david.themaquereau.databinding.ActivityDishDetailBinding
import fr.isen.david.themaquereau.model.domain.Item
import fr.isen.david.themaquereau.util.displayToast
import java.lang.NumberFormatException

//TODO redirection to previous activity not correct
class DishDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDishDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDishDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // get the selected item id
        intent.extras?.getSerializable(ItemAdapter.ITEM)?.let { serializedItem ->
            val item = serializedItem as Item
            binding.dishDetailName.text = item.name_fr
            // Ingredients
            binding.dishDetailIngredients.text = item.ingredients.joinToString(", ") { it.name_fr }
            // Get quantity 2
            try {
                val quantity2 = Integer.parseInt(binding.quantity2.text.toString())
                // Price
                if (item.prices.isNotEmpty()) {
                    // convert the price to int
                    val realPrice: Double = quantity2 * item.prices[0].price
                    binding.dishDetailPrice.text = realPrice.toString()
                }
            } catch (e: NumberFormatException) {
                displayToast("Cannot parse default value", applicationContext)
            }
            // Image
            val picasso = Picasso.get()
            if (item.images.first().isNotEmpty()) {
                picasso
                    .load(item.images.first())
                    .into(binding.dishDetailImage)
            } else {
                picasso
                    .load(R.drawable.maquereau_not_found)
                    .into(binding.dishDetailImage)
            }

            // Number Input Listener
            binding.quantity2.addTextChangedListener { number ->
                try {
                    val newQuantity = Integer.parseInt(number.toString())
                    val realPrice: Double = newQuantity * item.prices[0].price
                    binding.dishDetailPrice.text = realPrice.toString()
                } catch (e: NumberFormatException) {
                    displayToast("no number", applicationContext)
                }
            }
        }
    }

    companion object {
        val TAG: String = DishDetailActivity::class.java.simpleName
    }
}