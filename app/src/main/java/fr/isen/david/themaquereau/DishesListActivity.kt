package fr.isen.david.themaquereau

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.*
import com.google.gson.GsonBuilder
import fr.isen.david.themaquereau.adapters.ItemAdapter
import fr.isen.david.themaquereau.databinding.ActivityDishListBinding
import fr.isen.david.themaquereau.model.domain.Data
import fr.isen.david.themaquereau.model.domain.Item
import fr.isen.david.themaquereau.util.displayToast
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


class DishesListActivity : AppCompatActivity() {
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
        intent.extras?.getInt(HomeActivity.CATEGORY)?.let {
            category = it
        }

        // Recycler view adapter
        // Retrieve the recycler view
        val rvItems = binding.itemRecyclerView
        val adapter = ItemAdapter(items, applicationContext)
        rvItems.adapter = adapter
        rvItems.layoutManager = LinearLayoutManager(this)

        loadData()

        // Swipe container
        val swipeContainer = binding.swipeContainer
        swipeContainer.setOnRefreshListener {
            // fetch the data again
            loadData()
            // stop the refresh
            swipeContainer.isRefreshing = false
        }
    }

    private fun loadData() {
        // Setting cache
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())

        // Or perform the request if no data found
        // Request a string response from the provided URL.
        val req = object : JsonObjectRequest(
            Method.POST, API_URL, params,
            Response.Listener<JSONObject> { response ->
                Log.d(TAG, "Response: $response")
                val dataList = gson.fromJson(response["data"].toString(), Array<Data>::class.java)
                val data = dataList[category]
                binding.categoryText.text = data.name_fr

                // items
                val rvItems = binding.itemRecyclerView
                val adapter = ItemAdapter(data.items, applicationContext)
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

        // Add the request to the RequestQueue.
        val queue = RequestQueue(cache, network).apply {
            start()
        }
        queue.add(req)
    }

    companion object {
        val TAG: String = DishesListActivity::class.java.simpleName
        const val API_URL = "http://test.api.catering.bluecodegames.com/menu"
    }
}