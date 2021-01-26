package fr.isen.david.themaquereau

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.isen.david.themaquereau.adapters.ItemAdapter
import fr.isen.david.themaquereau.databinding.ActivityDishListBinding
import fr.isen.david.themaquereau.model.domain.Data
import fr.isen.david.themaquereau.model.domain.Item

class DishesListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDishListBinding
    private var items: List<Item> = listOf()
    private lateinit var rvItems: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDishListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.itemRecyclerView.isVisible = false

        intent.extras?.getSerializable(HomeActivity.DATA)?.let {
            val data: Data = it as Data
            Log.i(TAG, "recevied data: $data")
            binding.categoryText.text = data.name_fr
            items = data.items
        }

        // Retrieve the recycler view
        this.rvItems = binding.itemRecyclerView

        // Recycler view adapter
        val adapter = ItemAdapter(this.items, applicationContext)
        rvItems.adapter = adapter
        rvItems.layoutManager = LinearLayoutManager(this)
        binding.itemRecyclerView.isVisible = true
    }

    companion object {
        val TAG: String = DishesListActivity::class.java.simpleName
    }
}