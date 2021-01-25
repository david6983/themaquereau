package fr.isen.david.androiderestaurant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class DisplayDishesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrees)

        intent.extras?.getInt("category")?.let {
            findViewById<TextView>(R.id.categoryText).setText(it)
        }
    }

    companion object {
        const val TAG = "DisplayDishesActivity"
    }
}