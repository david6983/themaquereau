package fr.isen.david.themaquereau

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import fr.isen.david.themaquereau.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding // Best practise instead of findViewById

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //setSupportActionBar(binding.basketToolbar)
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

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, " destroyed") // log the destroy cycle
    }

    // Inflate the menu to the toolbar
    //TODO move to a AppCompactActivity that will be heritate
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

    private fun setupBadge(menuItem: MenuItem) {
        val textView = menuItem.actionView.findViewById<TextView>(R.id.nbItems)
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
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
        const val CATEGORY = "category"
    }
}