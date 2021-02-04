package fr.isen.david.themaquereau

import android.content.Intent
import android.os.Bundle
import android.util.Log
import fr.isen.david.themaquereau.databinding.ActivityHomeBinding


class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // callbacks
        manageMainMenu()
        manageFindUsButton()
        if (!preferencesImpl.isFirstTimeSignInDefined()) {
            preferencesImpl.setFirstTimeSignIn(true)
        }
    }

    private fun manageFindUsButton() {
        binding.findUsLink.setOnClickListener {
            startActivity(Intent(applicationContext, ContactActivity::class.java))
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

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, " destroyed") // log the destroy cycle
    }

    override fun setBasketListener() {
        val menuItemIntent = Intent(this, BasketActivity::class.java)
        startActivity(menuItemIntent)
    }

    companion object {
        val TAG: String = HomeActivity::class.java.simpleName
    }
}