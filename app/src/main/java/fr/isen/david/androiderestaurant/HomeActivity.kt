package fr.isen.david.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // listen click on every text in the main menu
        findViewById<TextView>(R.id.homeEntreeButton).setOnClickListener {
            val intent = Intent(this, DisplayDishesActivity::class.java)
            intent.putExtra("category", R.string.entree_title)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.homePlatsButton).setOnClickListener {
            val intent = Intent(this, DisplayDishesActivity::class.java)
            intent.putExtra("category", R.string.plats_title)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.homeDesertsButton).setOnClickListener {
            val intent = Intent(this, DisplayDishesActivity::class.java)
            intent.putExtra("category", R.string.deserts_title)
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