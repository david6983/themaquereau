package fr.isen.david.themaquereau

import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import fr.isen.david.themaquereau.helpers.AppPreferencesHelper
import fr.isen.david.themaquereau.util.displayToast
import org.koin.android.ext.android.inject

open class BaseActivity : AppCompatActivity() {
    open val preferences: AppPreferencesHelper by inject()
    open lateinit var basketMenu: MenuItem
    private lateinit var badgeTextView: TextView

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.accountAction)?.let {
            if (!preferences.isClientIdDefined()) {
                it.setTitle(R.string.action_log_in)
            } else {
                it.setTitle(R.string.action_log_out)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.basket_toolbar, menu)
        basketMenu = menu?.findItem(R.id.showBasket)!!
        setupBadge()
        Log.i("BaseActivity", "setuup badge")
        basketMenu.actionView.setOnClickListener {
            setBasketListener()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.accountAction -> {
            if (preferences.isClientIdDefined()) {
                preferences.removeClientId()
                hideBadge()
                displayToast("Log Out successfully", applicationContext)
            } else {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    open fun setupBadge() {
        badgeTextView = basketMenu.actionView.findViewById<TextView>(R.id.nbItems)
        if (preferences.isQuantityDefined() && !preferences.getFirstTimeSignIn()) {
            val quantity = preferences.getQuantity()
            if (quantity == 0) {
                badgeTextView.isVisible = false
            } else {
                badgeTextView.text = quantity.toString()
                badgeTextView.isVisible = true
            }
        } else {
            badgeTextView.isVisible = false
        }
    }

    open fun hideBadge() {
        badgeTextView.isVisible = false
    }

    open fun showBadge() {
        badgeTextView.isVisible = true
    }

    open fun setBasketListener() {
        val menuItemIntent = Intent(this, BasketActivity::class.java)
        startActivity(menuItemIntent)
    }
}