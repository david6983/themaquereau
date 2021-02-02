package fr.isen.david.themaquereau

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.*
import com.android.volley.toolbox.*
import com.google.gson.GsonBuilder
import fr.isen.david.themaquereau.adapters.ItemAdapter
import fr.isen.david.themaquereau.databinding.ActivityDishListBinding
import fr.isen.david.themaquereau.model.domain.Data
import fr.isen.david.themaquereau.model.domain.Item
import fr.isen.david.themaquereau.util.displayToast
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


class DishesListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDishListBinding
    private var items: List<Item> = listOf()
    private lateinit var toolbarMenu: MenuItem
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
    private lateinit var sharedPref: SharedPreferences
    private val params = JSONObject()
    private var category = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDishListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        params.put("id_shop", "1")

        // The list is not visible until the content is loaded
        binding.itemRecyclerView.isVisible = false

        // Get the category number
        intent.extras?.getInt(CATEGORY)?.let {
            category = it
        }

        // Recycler view adapter
        // Retrieve the recycler view
        val rvItems = binding.itemRecyclerView
        val adapter = ItemAdapter(items, category, applicationContext)
        rvItems.adapter = adapter
        rvItems.layoutManager = LinearLayoutManager(this)

        // Setting cache
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())

        val req = loadData()

        // Add the request to the RequestQueue.
        val queue = RequestQueue(cache, network).apply {
            start()
        }
        queue.add(req)

        // Swipe container
        val swipeContainer = binding.swipeContainer
        swipeContainer.setOnRefreshListener {
            // invalidate the cache
            queue.cache.clear()
            items = listOf()
            // The list is not visible until the content is loaded
            binding.itemRecyclerView.isVisible = false
            // Add the request to the RequestQueue.
            queue.add(req)
            // stop the refresh
            swipeContainer.isRefreshing = false
        }
    }

    private fun loadData(): JsonObjectRequest {
        // Or perform the request if no data found
        // Request a string response from the provided URL.
        return object : JsonObjectRequest(
            Method.POST, API_URL, params,
            Response.Listener { response ->
                Log.d(TAG, "Response: $response")
                val dataList = gson.fromJson(response["data"].toString(), Array<Data>::class.java)
                val data = dataList[category]
                binding.categoryText.text = data.name_fr

                // items
                val rvItems = binding.itemRecyclerView
                val adapter = ItemAdapter(data.items, category, applicationContext)
                rvItems.adapter = adapter

                binding.itemRecyclerView.isVisible = true
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Error: ${error.message}")
                displayToast("Cannot Load dishes", applicationContext)
                //TODO display no dishes found + display a message if no data
        }) {
            override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
                response?.let { res ->
                    try {
                        // Verify if there is some cache
                        var cacheEntry = HttpHeaderParser.parseCacheHeaders(response)
                        if (cacheEntry == null) {
                            // if not, create a cache entry
                            cacheEntry = Cache.Entry()
                        }
                        // in 3 minutes cache will be hit, but also refreshed on background
                        val cacheHitButRefreshed =
                            3 * 60 * 1000.toLong()
                        // in 24 hours this cache entry expires completely
                        val cacheExpired =
                            24 * 60 * 60 * 1000.toLong()
                        // current time
                        val now = System.currentTimeMillis()
                        // expiration time
                        val softExpire = now + cacheHitButRefreshed
                        val ttl = now + cacheExpired
                        // save raw data
                        cacheEntry.data = res.data
                        cacheEntry.softTtl = softExpire
                        cacheEntry.ttl = ttl
                        // handle cache header date
                        var headerValue: String? = res.headers["Date"]
                        if (headerValue != null) {
                            cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue)
                        }
                        // handle cache header Last-Modified
                        headerValue = response.headers["Last-Modified"]
                        if (headerValue != null) {
                            cacheEntry.lastModified =
                                HttpHeaderParser.parseDateAsEpoch(headerValue)
                        }
                        // Write response header
                        cacheEntry.responseHeaders = response.headers
                        val jsonString = String(
                            response.data,
                            Charset.forName("UTF-8")
                        )
                        Log.i(TAG, "from cache: $jsonString")
                        return Response.success(JSONObject(jsonString), cacheEntry)
                    } catch (e: UnsupportedEncodingException) {
                        Log.e(TAG, "Not supported encoding")
                        return Response.error(ParseError(e))
                    } catch (e: JSONException) {
                        Log.e(TAG, "Json error")
                        return Response.error(ParseError(e))
                    }
                }
                return super.parseNetworkResponse(response)
            }
        }
    }

    // Inflate the menu to the toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.basket_toolbar, menu)

        menu?.findItem(R.id.showBasket)?.let {
            toolbarMenu = it
            // Setup the badge with the quantity
            setupBadge(it)

            // Add a click listener
            it.actionView.setOnClickListener {
                val menuItemIntent = Intent(this, BasketActivity::class.java)
                // to return to the right activity, the basket activity need the category
                menuItemIntent.putExtra(CATEGORY, category)
                startActivity(menuItemIntent)
            }
        }

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.actionLogOut)?.let {
            if (!sharedPref.contains(ID_CLIENT)) {
                it.setTitle(R.string.action_log_in)
            } else {
                it.setTitle(R.string.action_log_out)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun setupBadge(menuItem: MenuItem) {
        val textView = menuItem.actionView.findViewById<TextView>(R.id.nbItems)
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        if (sharedPref.contains("quantity")) {
            val quantity = sharedPref.getInt("quantity", 0)
            if (quantity == 0) {
                textView.isVisible = false
            } else {
                textView.text = quantity.toString()
                textView.isVisible = true
            }
        } else {
            textView.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.showBasket -> {
            val menuItemIntent = Intent(this, BasketActivity::class.java)
            // to return to the right activity, the basket activity need the category
            menuItemIntent.putExtra(CATEGORY, category)
            startActivity(menuItemIntent)
            true
        }
        R.id.actionLogOut -> {
            if (sharedPref.contains(ID_CLIENT)) {
                with(sharedPref.edit()) {
                    remove(ID_CLIENT)
                    apply()
                }
                // reset quantity badge
                resetQuantity()
                displayToast("Log Out successfully", applicationContext)
            } else {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun resetQuantity() {
        if (sharedPref.contains(QUANTITY_KEY)) {
            with(sharedPref.edit()) {
                remove(fr.isen.david.themaquereau.QUANTITY_KEY)
                apply()
            }
        }
        setupBadge(toolbarMenu)
    }

    companion object {
        val TAG: String = DishesListActivity::class.java.simpleName
    }
}