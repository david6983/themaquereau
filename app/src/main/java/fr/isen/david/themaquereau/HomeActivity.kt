package fr.isen.david.themaquereau

import android.animation.ObjectAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.*
import androidx.core.animation.addListener
import fr.isen.david.themaquereau.databinding.ActivityHomeBinding
import fr.isen.david.themaquereau.util.fadeIn
import fr.isen.david.themaquereau.util.fadeOut
import fr.isen.david.themaquereau.util.translation


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
            preferencesImpl.setQuantity(0)
        }
        // Animations
        binding.homeEntreeButton.animation = fadeIn
        binding.homePlatsButton.animation = fadeIn
        binding.homeDesertsButton.animation = fadeIn
        translation(binding.homeDivider1, -1000.0f, 0.0f, 300)
        translation(binding.homeDivider2, +1000.0f, 0.0f, 300)
        translation(binding.homeDivider3, -1000.0f, 0.0f, 300)

        rotateMichelin()
    }

    private fun rotateMichelin() {
        ObjectAnimator.ofFloat(binding.michelinLogo,
            View.ROTATION_Y, 0f, 370f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = INFINITE
            repeatMode = REVERSE
            start()
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
            it.animation = fadeOut
            intent.putExtra(CATEGORY, 0)
            startActivity(intent)
        }
        binding.homePlatsButton.setOnClickListener {
            it.animation = fadeOut
            intent.putExtra(CATEGORY, 1)
            startActivity(intent)
        }
        binding.homeDesertsButton.setOnClickListener {
            it.animation = fadeOut
            intent.putExtra(CATEGORY, 2)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, " destroyed") // log the destroy cycle
    }

    override fun redirectToBasket() {
        val menuItemIntent = Intent(this, BasketActivity::class.java)
        startActivity(menuItemIntent)
    }

    companion object {
        val TAG: String = HomeActivity::class.java.simpleName
    }
}