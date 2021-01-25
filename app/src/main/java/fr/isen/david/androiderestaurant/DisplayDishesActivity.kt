package fr.isen.david.androiderestaurant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.isen.david.androiderestaurant.adapters.ItemAdapter
import fr.isen.david.androiderestaurant.databinding.ActivityEntreesBinding
import fr.isen.david.androiderestaurant.databinding.ActivityHomeBinding
import fr.isen.david.androiderestaurant.domain.Item

class DisplayDishesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntreesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntreesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // get the category and display the corresponding text
        intent.extras?.getInt(HomeActivity.CATEGORY)?.let {
            binding.categoryText.setText(it)
        }

        // Retrieve the recycler view
        val rvItems = findViewById<RecyclerView>(R.id.itemRecyclerView)

        // Create some contact
        val items = listOf(
            Item(
                0,
                "soupe de poisson",
                "",
                0,
                "",
                "",
                listOf(),
                listOf(),
                listOf()
            ),
            Item(
                    1,
                    "cordon bleu a la truffe",
                    "",
                    0,
                    "",
                    "",
                    listOf(),
                    listOf(),
                    listOf()
            )
        )

        val adapter = ItemAdapter(items)
        rvItems.adapter = adapter
        rvItems.layoutManager = LinearLayoutManager(this)
    }

    companion object {
        const val TAG = "DisplayDishesActivity"
    }
}