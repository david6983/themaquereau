package fr.isen.david.themaquereau

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.isen.david.themaquereau.adapters.ItemAdapter
import fr.isen.david.themaquereau.databinding.ActivityDishDetailBinding

class DishDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDishDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDishDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // get the selected item id
        intent.extras?.getLong(ItemAdapter.ITEM_ID)?.let {
            binding.textView.text = it.toString()
        }
    }
}