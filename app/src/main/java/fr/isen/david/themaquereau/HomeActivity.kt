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
import fr.isen.david.themaquereau.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding // Best practise instead of findViewById
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        manageMainMenu()
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
            // Setup the badge with the quantity
            setupBadge(it)

            // Add a click listener
            it.actionView.setOnClickListener {
                val menuItemIntent = Intent(this, BasketActivity::class.java)
                startActivity(menuItemIntent)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.actionLogIn -> {
                val menuItemIntent = Intent(this, SignUpActivity::class.java)
                startActivity(menuItemIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupBadge(menuItem: MenuItem) {
        val textView = menuItem.actionView.findViewById<TextView>(R.id.nbItems)
        if (sharedPref.contains("quantity")) {
            val quantity = sharedPref.getInt("quantity", 0)
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