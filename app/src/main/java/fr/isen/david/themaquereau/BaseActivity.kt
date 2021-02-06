package fr.isen.david.themaquereau

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import fr.isen.david.themaquereau.helpers.AppPreferencesHelperImpl
import fr.isen.david.themaquereau.util.displayToast
import org.koin.android.ext.android.inject

open class BaseActivity : AppCompatActivity() {
    open val preferencesImpl: AppPreferencesHelperImpl by inject()
    open lateinit var basketMenu: MenuItem
    private lateinit var badgeTextView: TextView

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.accountAction)?.let {
            if (!preferencesImpl.isClientIdDefined()) {
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
        basketMenu.actionView.setOnClickListener {
            redirectToBasket()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.accountAction -> {
            if (preferencesImpl.isClientIdDefined()) {
                preferencesImpl.removeClientId()
                hideBadge()
                displayToast(getString(R.string.log_out_success), applicationContext)
            } else {
                redirectSignIn()
            }
            true
        }
        R.id.myPreviousOrdersAction -> {
            val intent = Intent(this, PreviousOrdersActivity::class.java)
            startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    open fun redirectSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    private fun setMyAccountItem(menu: Menu?) {
        menu?.findItem(R.id.myPreviousOrdersAction)?.isVisible = !preferencesImpl.isClientIdDefined()
    }

    open fun setupBadge() {
        badgeTextView = basketMenu.actionView.findViewById<TextView>(R.id.nbItems)
        if (preferencesImpl.isQuantityDefined() && !preferencesImpl.getFirstTimeSignIn()) {
            val quantity = preferencesImpl.getQuantity()
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

    open fun redirectToBasket() {
        val menuItemIntent = Intent(this, BasketActivity::class.java)
        startActivity(menuItemIntent)
    }
}