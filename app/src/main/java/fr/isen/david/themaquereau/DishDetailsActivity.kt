package fr.isen.david.themaquereau

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Menu
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

class DishDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDishDetailsBinding
    private lateinit var order: Order
    private lateinit var item: Item
    private lateinit var basketMenu: MenuItem
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDishDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

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
            updateQuantity(item, txt)
        }

        binding.fishImageButton.setOnClickListener { v ->
            orderCallback(v)
        }
    }

    private fun orderCallback(view: View) {
        if (sharedPref.contains(ID_CLIENT)) {
            // Save the order in a file
            saveOrder(order)
            // Save the quantity
            updateQuantity(order.quantity)
            // Alert the user with a snack bar
            alertUser(view)
        } else {
            if (sharedPref.contains(FIRST_TIME_SIGN_IN)) {
                sharedPref.getBoolean(FIRST_TIME_SIGN_IN, false).let {
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

    private fun updateQuantity(item: Item, quantityValue: Editable?) {
        try {
            order.quantity = Integer.parseInt(quantityValue.toString())
            order.realPrice = order.quantity * item.prices[0].price
            binding.dishDetailPrice.text = order.realPrice.toString()
        } catch (e: NumberFormatException) {
            displayToast("no number", applicationContext)
        }
    }

    private fun saveOrder(order: Order) {
        //TODO add id to the basket to separate baskets
        try {
            applicationContext.openFileInput(ORDER_FILE).use { inputStream ->
                inputStream.bufferedReader().use {
                    val previousOrders = Gson().fromJson(it.readText(), Array<Order>::class.java).toMutableList()
                    // update id
                    order.order_id += 1
                    // verify if the order already exist by name
                    previousOrders.find { ord -> ord.item.name_fr == order.item.name_fr }.let { foundOrder ->
                        if (foundOrder !== null) {
                            val position = previousOrders.indexOf(foundOrder);
                            foundOrder.quantity += order.quantity
                            foundOrder.realPrice += order.realPrice
                            previousOrders.set(position, foundOrder)
                        } else {
                            previousOrders.add(order)
                        }
                    }
                    val newJsonOrders = Gson().toJson(previousOrders)
                    applicationContext.openFileOutput(ORDER_FILE, Context.MODE_PRIVATE).use { outputStream ->
                        outputStream.write(newJsonOrders.toString().toByteArray())
                        Log.i(TAG, "updated orders: $newJsonOrders")
                    }
                }
            }
        } catch(e: FileNotFoundException) {
            val orders = JsonArray()
            val jsonOrder = Gson().toJsonTree(order)
            orders.add(Gson().toJsonTree(jsonOrder))
            applicationContext.openFileOutput(ORDER_FILE, Context.MODE_PRIVATE).use { outputStream ->
                outputStream.write(orders.toString().toByteArray())
                Log.i(TAG, "order saved: $jsonOrder")
            }
        }
    }

    private fun alertUser(view: View) {
        val snack = Snackbar.make(view, R.string.order_saved, Snackbar.LENGTH_SHORT)
        snack.show()
    }

    private fun updateQuantity(newQuantity: Int) {
        // Save the quantity
        val currentQuantity = sharedPref.getInt(QUANTITY_KEY, 0)
        // add the quantity to the previous quantity
        with(sharedPref.edit()) {
            putInt(QUANTITY_KEY, currentQuantity + newQuantity)
            apply()
        }
        // Setup the badge with the quantity
        setupBadge(basketMenu)
        Log.i(TAG, "added to pref: ${sharedPref.getInt(QUANTITY_KEY, 0)}")
    }

    // Inflate the menu to the toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.basket_toolbar, menu)

        basketMenu = menu?.findItem(R.id.showBasket)!!
        setupBadge(basketMenu)
        // Add a click listener
        basketMenu.actionView.setOnClickListener {
            val menuItemIntent = Intent(this, BasketActivity::class.java)
            menuItemIntent.putExtra(ITEM, this.item)
            startActivity(menuItemIntent)
        }

        return true
    }

    private fun setupBadge(menuItem: MenuItem) {
        val textView = menuItem.actionView.findViewById<TextView>(R.id.nbItems)
        val quantity = sharedPref.getInt(QUANTITY_KEY, 0)
        if (quantity == 0) {
            textView.isVisible = false
        } else {
            textView.text = quantity.toString()
            textView.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.showBasket -> {
            val menuItemIntent = Intent(this, BasketActivity::class.java)
            menuItemIntent.putExtra(ITEM, this.item)
            startActivity(menuItemIntent)
            true
        }
        R.id.actionLogOut -> {
            val sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                remove(ID_CLIENT)
                apply()
            }
            displayToast("Log Out successfully", applicationContext)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
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