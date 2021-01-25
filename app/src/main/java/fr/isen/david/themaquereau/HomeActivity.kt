package fr.isen.david.themaquereau

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import fr.isen.david.themaquereau.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding // Best practise instead of findViewById

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.homeEntreeButton.setOnClickListener {
            val intent = Intent(this, DishesListActivity::class.java)
            intent.putExtra(CATEGORY, R.string.entree_title)
            startActivity(intent)
        }
        binding.homePlatsButton.setOnClickListener {
            val intent = Intent(this, DishesListActivity::class.java)
            intent.putExtra(CATEGORY, R.string.plats_title)
            startActivity(intent)
        }
        binding.homeDesertsButton.setOnClickListener {
            val intent = Intent(this, DishesListActivity::class.java)
            intent.putExtra(CATEGORY, R.string.deserts_title)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, " destroyed") // log the destroy cycle
    }

    companion object {
        val TAG: String = HomeActivity::class.java.simpleName
        const val CATEGORY = "category"
    }
}