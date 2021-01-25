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

        // temporary display function to display a toast given a text string
        val displayToast = { text: CharSequence ->
            val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT)
            toast.show()
        }

        // listen click on every text in the main menu
        findViewById<TextView>(R.id.homeEntreeButton).setOnClickListener {
            //displayToast("Entrées")
            val intent = Intent(this, EntreesActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.homePlatsButton).setOnClickListener {
            displayToast("Plats")
        }
        findViewById<TextView>(R.id.homeDesertsButton).setOnClickListener {
            displayToast("Deserts")
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