package fr.isen.david.androiderestaurant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity_layout)

        // temporary display function to display a toast given a text string
        val displayToast = { text: CharSequence ->
            val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT)
            toast.show()
        }

        // listen click on every text in the main menu
        findViewById<TextView>(R.id.homeEntreeButton).setOnClickListener {
            displayToast("Entrées")
        }
        findViewById<TextView>(R.id.homePlatsButton).setOnClickListener {
            displayToast("Plats")
        }
        findViewById<TextView>(R.id.homeDesertsButton).setOnClickListener {
            displayToast("Deserts")
        }
    }
}