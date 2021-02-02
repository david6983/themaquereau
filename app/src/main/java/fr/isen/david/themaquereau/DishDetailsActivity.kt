package fr.isen.david.themaquereau

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonArray
import fr.isen.david.themaquereau.databinding.ActivityDishDetailsBinding
import fr.isen.david.themaquereau.fragments.DishImagesPagerFragment
import fr.isen.david.themaquereau.model.domain.Item
import fr.isen.david.themaquereau.model.domain.Order
import fr.isen.david.themaquereau.util.displayToast
import java.io.FileNotFoundException
import java.lang.NumberFormatException

class DishDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityDishDetailsBinding
    private lateinit var order: Order
    private lateinit var item: Item

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
        binding.dishDetailIngredients.text = item.ingredients.joinToString(", ") { it.name_fr }
        // Get quantity
        getQuantity()

        // Images Pager
        setImagePager()

        // Number Input Listener
        binding.quantity.addTextChangedListener { txt ->
            updateQuantityInput(item, txt)
        }

        binding.fishImageButton.setOnClickListener { v ->
            orderCallback(v)
        }
    }

    private fun orderCallback(view: View) {
        val id = preferences.getClientId()
        if (preferences.getClientId() != -1) {
            // Save the order in a file
            saveOrder(order, id)
            // Alert the user with a snack bar
            alertUser(view)
        } else {
            if (preferences.isFirstTimeSignInDefined()) {
                preferences.getFirstTimeSignIn().let {
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
            displayToast("Cannot parse default value", applicationContext)
        }
    }

    private fun updateQuantityInput(item: Item, quantityValue: Editable?) {
        try {
            order.quantity = Integer.parseInt(quantityValue.toString())
            order.realPrice = order.quantity * item.prices[0].price
            binding.dishDetailPrice.text = order.realPrice.toString()
        } catch (e: NumberFormatException) {
            displayToast("no number", applicationContext)
        }
    }

    private fun saveOrder(order: Order, userId: Int) {
        try {
            applicationContext.openFileInput("$ORDER_FILE$userId$ORDER_FILE_SUFFIX").use { inputStream ->
                inputStream.bufferedReader().use { reader ->
                    val orders = Gson().fromJson(reader.readText(), Array<Order>::class.java).toMutableList()
                    // update id
                    order.order_id += 1
                    // verify if the order already exist by name
                    orders.find { ord -> ord.item.name_fr == order.item.name_fr }.let { foundOrder ->
                        if (foundOrder !== null) {
                            val position = orders.indexOf(foundOrder);
                            foundOrder.quantity += order.quantity
                            foundOrder.realPrice += order.realPrice
                            orders.set(position, foundOrder)
                        } else {
                            orders.add(order)
                        }
                    }
                    // update quantity
                    updateQuantity(orders.sumBy { it.quantity })
                    val newJsonOrders = Gson().toJson(orders)
                    applicationContext.openFileOutput("$ORDER_FILE$userId$ORDER_FILE_SUFFIX", Context.MODE_PRIVATE).use { outputStream ->
                        outputStream.write(newJsonOrders.toString().toByteArray())
                        Log.i(TAG, "updated orders: $newJsonOrders")
                    }
                }
            }
        } catch(e: FileNotFoundException) {
            val orders = JsonArray()
            val jsonOrder = Gson().toJsonTree(order)
            orders.add(Gson().toJsonTree(jsonOrder))
            applicationContext.openFileOutput("$ORDER_FILE$userId$ORDER_FILE_SUFFIX", Context.MODE_PRIVATE).use { outputStream ->
                outputStream.write(orders.toString().toByteArray())
                updateQuantity(order.quantity)
                Log.i(TAG, "order saved: $jsonOrder")
            }
        }
    }

    private fun alertUser(view: View) {
        val snack = Snackbar.make(view, R.string.order_saved, Snackbar.LENGTH_SHORT)
        snack.show()
    }

    private fun updateQuantity(newQuantity: Int) {
        preferences.setQuantity(newQuantity)
        setupBadge()
        Log.i(TAG, "added to pref: ${preferences.getQuantity()}")
    }

    override fun setBasketListener() {
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

    companion object {
        val TAG: String = DishDetailsActivity::class.java.simpleName
    }
}