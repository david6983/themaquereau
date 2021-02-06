package fr.isen.david.themaquereau

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import fr.isen.david.themaquereau.databinding.ActivityDishDetailsBinding
import fr.isen.david.themaquereau.fragments.DishImagesPagerFragment
import fr.isen.david.themaquereau.helpers.PersistOrdersHelperImpl
import fr.isen.david.themaquereau.model.domain.Item
import fr.isen.david.themaquereau.model.domain.Order
import fr.isen.david.themaquereau.util.displayToast
import org.koin.android.ext.android.inject


class DishDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityDishDetailsBinding
    private lateinit var order: Order
    private lateinit var item: Item

    private val persistence: PersistOrdersHelperImpl by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDishDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // get the selected item id
        intent.extras?.getSerializable(ITEM)?.let { serializedItem ->
            item = serializedItem as Item
        }

        binding.dishDetailName.text = item.name_fr
        // Create an order
        order = Order(0, item, 1 , 0.0)
        // Ingredients
        binding.dishDetailIngredients.text = item.ingredients
            .joinToString(", ") { it.name_fr }
        // Get quantity
        getQuantity()
        // Images Pager
        setImagePager()
        // Number Input Listener
        binding.quantity.addTextChangedListener { txt ->
            updateQuantityInput(item, txt)
        }
        // Order button Listener
        binding.orderButton.setOnClickListener { v ->
            orderCallback(v)
        }
    }

    private fun orderCallback(view: View) {
        val id = preferencesImpl.getClientId()
        if (preferencesImpl.getClientId() != -1) {
            // Save the order in a file
            saveOrder(order, id)
            // Alert the user with a snack bar
            alertUser(view)
        } else {
            if (preferencesImpl.isFirstTimeSignInDefined()) {
                preferencesImpl.getFirstTimeSignIn().let {
                    intent = if (it) {
                        Intent(this, SignUpActivity::class.java)
                    } else {
                        Intent(this, SignInActivity::class.java)
                    }
                }
            } else { // by default
                intent = Intent(this, SignUpActivity::class.java)
            }
            intent.putExtra(ITEM, item)
            startActivity(intent)
        }
    }

    private fun setImagePager() {
        val pagerFragment = DishImagesPagerFragment()
        pagerFragment.arguments = Bundle().apply {
            // Give the item to the pager
            putSerializable(ARG_OBJECT, item)
        }

        // Replace the fragment by the new one
        supportFragmentManager.beginTransaction()
            .replace(R.id.dishPagerFragment, pagerFragment)
            .addToBackStack("DishImagesPagerFragment")
            .commit()
    }

    private fun getQuantity() {
        try {
            val quantity = Integer.parseInt(binding.quantity.text.toString())
            // Price
            if (item.prices.isNotEmpty()) {
                // convert the price to int
                order.realPrice = quantity * item.prices[0].price
                binding.dishDetailPrice.text = order.realPrice.toString()
            }
        } catch (e: NumberFormatException) {
            Log.i(TAG, "Cannot parse quantity")
        }
    }

    private fun updateQuantityInput(item: Item, quantityValue: Editable?) {
        try {
            order.quantity = Integer.parseInt(quantityValue.toString())
            order.realPrice = order.quantity * item.prices[0].price
            binding.dishDetailPrice.text = order.realPrice.toString()
        } catch (e: NumberFormatException) {
            displayToast(getString(R.string.no_number), applicationContext)
        }
    }

    private fun saveOrder(order: Order, userId: Int) {
        persistence.saveOrder(userId, order, orderSavedCallback, errorSaveOrderCallback)
    }

    private val orderSavedCallback = { orders: MutableList<Order> ->
        updateQuantity(orders.sumBy { it.quantity })
    }

    private val errorSaveOrderCallback = { quantity: Int ->
        updateQuantity(quantity)
    }

    private fun alertUser(view: View) {
        val snack = Snackbar.make(view, R.string.order_saved, Snackbar.LENGTH_SHORT)
        snack.show()
    }

    private fun updateQuantity(newQuantity: Int) {
        preferencesImpl.setQuantity(newQuantity)
        setupBadge()
        Log.i(TAG, "added to pref: ${preferencesImpl.getQuantity()}")
    }

    override fun redirectToBasket() {
        val menuItemIntent = Intent(this, BasketActivity::class.java)
        menuItemIntent.putExtra(ITEM, this.item)
        startActivity(menuItemIntent)
    }

    override fun getSupportParentActivityIntent(): Intent? {
        return getParentActivityIntentImpl()
    }

    override fun getParentActivityIntent(): Intent? {
        return getParentActivityIntentImpl()
    }

    private fun getParentActivityIntentImpl(): Intent {
        val parentIntent = Intent(this, DishesListActivity::class.java)
        // Get the category number to display the right parent view
        intent.extras?.getInt(CATEGORY)?.let {
            parentIntent.putExtra(CATEGORY, it)

        }
        return parentIntent
    }

    override fun redirectSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.putExtra(ITEM, item)
        startActivity(intent)
    }

    companion object {
        val TAG: String = DishDetailsActivity::class.java.simpleName
    }
}