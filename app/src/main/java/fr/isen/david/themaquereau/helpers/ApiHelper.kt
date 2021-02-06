package fr.isen.david.themaquereau.helpers

import android.content.Context
import android.util.Log
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.android.volley.*
import com.android.volley.toolbox.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import fr.isen.david.themaquereau.*
import fr.isen.david.themaquereau.model.domain.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

interface ApiHelper {
    fun loadDishList(
        category: Int,
        receivedDataCallback: (data: Data) -> (Unit),
        errorCallback: () -> (Unit),
        cacheDir: File
    ): RequestQueue
    fun listPreviousOrders(userId: Int, receivedDataCallback: (data: Array<HistoryOrder>) -> Unit)
    fun saveFinalOrder(
        jsonOrder: String,
        userId: Int,
        onSavedCallback: (receiver: String) -> Unit,
        progressBar: ProgressBar
    )
    fun signIn(user: User, loginCallback: (userId: Int) -> (Unit))
    fun signUp(user: User, registerCallback: (userId: Int) -> (Unit), errorCallback: () -> (Unit))
    fun getQueue(): RequestQueue
    fun getCacheQueue(cacheDir: File): RequestQueue
    fun getJsonRequestLoadData(
        category: Int,
        callback: (data: Data) -> (Unit),
        errorCallback: () -> (Unit)
    ): JsonObjectRequest
}

class ApiHelperImpl(
    private val context: Context
) : ApiHelper {
    private val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

    override fun loadDishList(
        category: Int,
        receivedDataCallback: (data: Data) -> (Unit),
        errorCallback: () -> (Unit),
        cacheDir: File
    ): RequestQueue {
        val queue = getCacheQueue(cacheDir)
        val req = getJsonRequestLoadData(category, receivedDataCallback, errorCallback)
        queue.add(req)
        return queue
    }

    override fun getJsonRequestLoadData(
        category: Int,
        callback: (data: Data) -> (Unit),
        errorCallback: () -> (Unit)
    ): JsonObjectRequest {
        val params = getParams()

        return object : JsonObjectRequest(
            Method.POST, API_URL, params,
            Response.Listener { response ->
                Log.d(DishesListActivity.TAG, "Response: $response")
                val dataList = gson.fromJson(response["data"].toString(), Array<Data>::class.java)
                callback(dataList[category])
            },
            Response.ErrorListener { error ->
                Log.e(DishesListActivity.TAG, "Error: ${error.message}")
                errorCallback()
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
                return Response.error(VolleyError("Cannot parse network response"))
            }
        }
    }

    override fun listPreviousOrders(
        userId: Int,
        receivedDataCallback: (data: Array<HistoryOrder>) -> Unit
    ) {
        val queue = getQueue()
        // params
        val params = getParams()
        params.put("id_user", userId)
        Log.i(PreviousOrdersActivity.TAG, "Sending params: $params")
        val req = JsonObjectRequest(
            Request.Method.POST, API_LIST_ORDER_URL, params,
            Response.Listener { response ->
                Log.d(SignInActivity.TAG, "List Order Response: $response")
                receivedDataCallback(
                    gson.fromJson(
                        response["data"].toString(),
                        Array<HistoryOrder>::class.java)
                )
            },
            Response.ErrorListener { error ->
                Log.e(DishesListActivity.TAG, "Error: ${error.message}")
            })
        queue.add(req)
    }

    override fun saveFinalOrder(
        jsonOrder: String,
        userId: Int,
        onSavedCallback: (receiver: String) -> Unit,
        progressBar: ProgressBar
    ) {
        val queue = getQueue()
        // params
        val params = getParams()
        params.put("id_user", userId)
        params.put("msg", jsonOrder)

        val req = JsonObjectRequest(
            Request.Method.POST, API_ORDER_URL, params,
            Response.Listener { response ->
                Log.d(SignInActivity.TAG, "Sent Order Response: $response")
                // alert the user
                onSavedCallback(
                    gson.fromJson(
                        response["data"].toString(),
                        Array<FinalOrderResponse>::class.java)[0].receiver
                )
            },
            Response.ErrorListener { error ->
                Log.e(DishesListActivity.TAG, "Error: ${error.message}")
            })
        queue.add(req)
        progressBar.isVisible = true
        queue.addRequestFinishedListener<JsonObjectRequest> {
            // dismiss progress bar
            progressBar.isVisible = false
        }
    }

    override fun signIn(user: User, loginCallback: (userId: Int) -> (Unit)) {
        val queue = getQueue()
        // params
        val params = getParams()
        user.toSignInParams(params)
        val req = JsonObjectRequest(
            Request.Method.POST, API_LOGIN_URL, params,
            Response.Listener { response ->
                Log.d(SignInActivity.TAG, "Sign In Response: $response")
                loginCallback(
                    gson.fromJson(
                        response["data"].toString(),
                        RegisterResponse::class.java).id
                )
            },
            Response.ErrorListener { error ->
                Log.e(DishesListActivity.TAG, "Error: ${error.message}")
            })

        queue.add(req)
    }

    override fun signUp(
        user: User,
        registerCallback: (userId: Int) -> (Unit),
        errorCallback: () -> (Unit)
    ) {
        val queue = getQueue()
        // params
        val params = getParams()
        user.toSignUpParams(params)
        Log.i(SignUpActivity.TAG, "with params $params")
        val req = JsonObjectRequest(
            Request.Method.POST, API_REGISTER_URL, params,
            Response.Listener { response ->
                Log.d(SignUpActivity.TAG, "Sign Up Response: $response")
                registerCallback(
                    gson.fromJson(
                        response["data"].toString(),
                        RegisterResponse::class.java).id
                )
            },
            Response.ErrorListener { error ->
                errorCallback()
                Log.e(SignUpActivity.TAG, "Error: $error")
            }
        )
        queue.add(req)
    }

    override fun getQueue(): RequestQueue {
        return Volley.newRequestQueue(context)
    }

    override fun getCacheQueue(cacheDir: File): RequestQueue {
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        return RequestQueue(cache, network).apply {
            start()
        }
    }

    private fun getParams(): JSONObject {
        val params = JSONObject()
        params.put("id_shop", "1")
        return params
    }

    companion object {
        val TAG: String = ApiHelperImpl::class.java.simpleName
    }
}