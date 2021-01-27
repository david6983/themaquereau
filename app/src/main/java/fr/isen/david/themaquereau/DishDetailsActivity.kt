package fr.isen.david.themaquereau

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import fr.isen.david.themaquereau.adapters.ItemAdapter
import fr.isen.david.themaquereau.databinding.ActivityDishDetailsBinding
import fr.isen.david.themaquereau.fragments.DishImagesPagerFragment
import fr.isen.david.themaquereau.model.domain.Item
import fr.isen.david.themaquereau.util.displayToast
import java.lang.NumberFormatException

class DishDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDishDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDishDetailsBinding.inflate(layoutInflater)
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
                val quantity2 = Integer.parseInt(binding.quantity.text.toString())
                // Price
                if (item.prices.isNotEmpty()) {
                    // convert the price to int
                    val realPrice: Double = quantity2 * item.prices[0].price
                    binding.dishDetailPrice.text = realPrice.toString()
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
                    val newQuantity = Integer.parseInt(txt.toString())
                    val realPrice: Double = newQuantity * item.prices[0].price
                    binding.dishDetailPrice.text = realPrice.toString()
                } catch (e: NumberFormatException) {
                    displayToast("no number", applicationContext)
                }
            }

            binding.fishImageButton.setOnClickListener {
                Log.i(TAG, "submit maquereau")
            }
        }
    }

    companion object {
        val TAG: String = DishDetailsActivity::class.java.simpleName
    }
}