package fr.isen.david.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // listen click on every text in the main menu
        findViewById<TextView>(R.id.homeEntreeButton).setOnClickListener {
            val intent = Intent(this, DisplayMenuItemsActivity::class.java)
            intent.putExtra("category", "Nos Entrées")
            startActivity(intent)
        }
        findViewById<TextView>(R.id.homePlatsButton).setOnClickListener {
            val intent = Intent(this, DisplayMenuItemsActivity::class.java)
            intent.putExtra("category", "Nos Plats")
            startActivity(intent)
        }
        findViewById<TextView>(R.id.homeDesertsButton).setOnClickListener {
            val intent = Intent(this, DisplayMenuItemsActivity::class.java)
            intent.putExtra("category", "Nos Deserts")
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, " destroyed") // log the destroy cycle
    }

    companion object {
        const val TAG = "HomeActivity"
    }
}