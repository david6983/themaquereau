package fr.isen.david.themaquereau

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import fr.isen.david.themaquereau.databinding.ActivitySignInBinding
import fr.isen.david.themaquereau.model.domain.User
import fr.isen.david.themaquereau.util.displayToast
import org.json.JSONObject

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.submitSignIn.setOnClickListener {
            displayToast("Sign in", applicationContext)
        }

        binding.noAccountLink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            //intent.putExtra(ItemAdapter.ITEM, item)
            startActivity(intent)
        }
    }

    private fun signIn(user: User, id_shop: String): JsonObjectRequest {
        // params
        val params = JSONObject()
        params.put("id_shop", id_shop)
        user.toSignInParams(params)
        return JsonObjectRequest(
            Request.Method.POST, API_LOGIN_URL, params,
            Response.Listener { response ->
                Log.d(SignUpActivity.TAG, "Response: $response")
            },
            Response.ErrorListener { error ->
                Log.e(DishesListActivity.TAG, "Error: ${error.message}")
            })
    }
}