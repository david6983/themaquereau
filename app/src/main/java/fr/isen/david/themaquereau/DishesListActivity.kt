package fr.isen.david.themaquereau

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class DishesListActivity : BaseActivity() {
    private lateinit var binding: ActivityDishListBinding
    private var items: List<Item> = listOf()
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
    private val params = JSONObject()
    private var category = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDishListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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

    override fun setBasketListener() {
        val menuItemIntent = Intent(this, BasketActivity::class.java)
        menuItemIntent.putExtra(CATEGORY, category)
        startActivity(menuItemIntent)
    }

    companion object {
        val TAG: String = DishesListActivity::class.java.simpleName
    }
}