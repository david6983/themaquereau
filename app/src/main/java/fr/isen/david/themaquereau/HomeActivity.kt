package fr.isen.david.themaquereau

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.gson.Gson
import fr.isen.david.themaquereau.databinding.ActivityHomeBinding
import fr.isen.david.themaquereau.model.domain.Order
import fr.isen.david.themaquereau.util.displayToast


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding // Best practise instead of findViewById
    private lateinit var sharedPref: SharedPreferences
    private lateinit var toolbarMenu: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        manageMainMenu()
        retrieveQuantity()
        setFirstTimeSignIn(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, " destroyed") // log the destroy cycle
    }

    private fun setFirstTimeSignIn(value: Boolean) {
        if (!sharedPref.contains(FIRST_TIME_SIGN_IN)) {
            with(sharedPref.edit()) {
                putBoolean(FIRST_TIME_SIGN_IN, value)
                apply()
            }
        }
    }

    private fun retrieveQuantity() {
        sharedPref.getInt(ID_CLIENT, -1).let { userId ->
            if (userId != -1) {
                applicationContext.openFileInput("$ORDER_FILE$userId$ORDER_FILE_SUFFIX").use { inputStream ->
                    inputStream.bufferedReader().use { reader ->
                        val orders =
                            Gson().fromJson(reader.readText(), Array<Order>::class.java).toMutableList()
                        Log.i(TAG, "$orders")
                        with(sharedPref.edit()) {
                            putInt(QUANTITY_KEY, orders.sumBy { it.quantity })
                            apply()
                        }
                    }
                }
            } else {
                //recreate()
            }
        }
    }

    private fun manageMainMenu() {
        val intent = Intent(this, DishesListActivity::class.java)
        binding.homeEntreeButton.setOnClickListener {
            intent.putExtra(CATEGORY, 0)
            startActivity(intent)
        }
        binding.homePlatsButton.setOnClickListener {
            intent.putExtra(CATEGORY, 1)
            startActivity(intent)
        }
        binding.homeDesertsButton.setOnClickListener {
            intent.putExtra(CATEGORY, 2)
            startActivity(intent)
        }
    }

    // Inflate the menu to the toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.basket_toolbar, menu)

        menu?.findItem(R.id.showBasket)?.let {
            // keep a reference
            toolbarMenu = it
            // Setup the badge with the quantity
            retrieveQuantity()
            setupBadge(it)
            // Add a click listener
            it.actionView.setOnClickListener {
                val menuItemIntent = Intent(this, BasketActivity::class.java)
                startActivity(menuItemIntent)
            }
        }

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.actionLogOut)?.let {
            if (!sharedPref.contains(ID_CLIENT)) {
                it.setTitle(R.string.action_log_in)
            } else {
                it.setTitle(R.string.action_log_out)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.actionLogOut -> {
                if (sharedPref.contains(ID_CLIENT)) {
                    with(sharedPref.edit()) {
                        remove(ID_CLIENT)
                        apply()
                    }
                    // reset quantity badge
                    resetQuantity()
                    displayToast("Log Out successfully", applicationContext)
                } else {
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resetQuantity() {
        if (sharedPref.contains(QUANTITY_KEY)) {
            with(sharedPref.edit()) {
                remove(QUANTITY_KEY)
                apply()
            }
        }
        setupBadge(toolbarMenu)
    }

    private fun setupBadge(menuItem: MenuItem) {
        val textView = menuItem.actionView.findViewById<TextView>(R.id.nbItems)
        if (sharedPref.contains(QUANTITY_KEY)) {
            val quantity = sharedPref.getInt(QUANTITY_KEY, 0)
            if (quantity == 0) {
                textView.isVisible = false
            } else {
                textView.text = quantity.toString()
                textView.isVisible = true
            }
        } else {
            textView.isVisible = false
        }
    }

    companion object {
        val TAG: String = HomeActivity::class.java.simpleName
    }
}