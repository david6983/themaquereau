package fr.isen.david.themaquereau

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import fr.isen.david.themaquereau.adapters.ItemAdapter
import fr.isen.david.themaquereau.databinding.ActivityDishListBinding
import fr.isen.david.themaquereau.helpers.ApiHelperImpl
import fr.isen.david.themaquereau.model.domain.Data
import fr.isen.david.themaquereau.model.domain.Item
import org.koin.android.ext.android.inject

class DishesListActivity : BaseActivity() {
    private lateinit var binding: ActivityDishListBinding
    private var items: List<Item> = listOf()
    private var category = 0

    private val api: ApiHelperImpl by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDishListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // The list is not visible until the content is loaded
        binding.itemRecyclerView.isVisible = false

        // Get the category number
        intent.extras?.getInt(CATEGORY)?.let {
            category = it
        }

        // get the list of dish from the api
        val queue = api.loadDishList(category, onDataReceived, cacheDir)

        // Swipe container
        setSwipeToRefresh(queue)
    }

    private fun setSwipeToRefresh(queue: RequestQueue) {
        val swipeContainer = binding.swipeContainer
        swipeContainer.setOnRefreshListener {
            // invalidate the cache
            queue.cache.clear()
            items = listOf()
            // The list is not visible until the content is loaded
            binding.itemRecyclerView.isVisible = false
            // Add the request to the RequestQueue.
            queue.add(api.getJsonRequestLoadData(category, onDataReceived))
            // stop the refresh
            swipeContainer.isRefreshing = false
        }
    }

    private val onDataReceived = { data: Data ->
        binding.categoryText.text = data.name_fr

        // items
        val rvItems = binding.itemRecyclerView
        val adapter = ItemAdapter(data.items, category, applicationContext)
        rvItems.adapter = adapter
        rvItems.layoutManager = LinearLayoutManager(this)

        binding.itemRecyclerView.isVisible = true
    }

    override fun setBasketListener() {
        val menuItemIntent = Intent(this, BasketActivity::class.java)
        menuItemIntent.putExtra(CATEGORY, category)
        startActivity(menuItemIntent)
    }

    companion object {
        val TAG: String = DishesListActivity::class.java.simpleName
    }
}