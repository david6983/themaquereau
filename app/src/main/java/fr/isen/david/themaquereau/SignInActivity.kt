package fr.isen.david.themaquereau

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.david.themaquereau.databinding.ActivitySignInBinding
import fr.isen.david.themaquereau.model.domain.RegisterResponse
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

        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        // Set checkbox if not the first time
        if (sharedPref.contains(FIRST_TIME_SIGN_IN)) {
            sharedPref.getBoolean(FIRST_TIME_SIGN_IN, true).let {
                if (!it) {
                    binding.remindMeCheckBox.isChecked = true
                }
            }
        }

        binding.submitSignIn.setOnClickListener {
            val user = User(
                "",
                "",
                binding.emailInput.text.toString(),
                "",
                binding.passwordInput.text.toString()
            )
            val queue = Volley.newRequestQueue(this)
            val req = signIn(user, "1")
            queue.add(req)

            if (binding.remindMeCheckBox.isChecked) {
                with(sharedPref.edit()) {
                    putBoolean(FIRST_TIME_SIGN_IN, false)
                    apply()
                }
            } else {
                with(sharedPref.edit()) {
                    putBoolean(FIRST_TIME_SIGN_IN, true)
                    apply()
                }
            }

            displayToast("Sign in successfully", applicationContext)
            redirectToParent()
        }

        binding.noAccountLink.setOnClickListener {
            intent.extras?.getSerializable(ITEM)?.let {
                val intent = Intent(this, SignUpActivity::class.java)
                intent.putExtra(ITEM, it)
                startActivity(intent)
            }
        }
    }

    private fun redirectToParent() {
        // redirect to dish list
        intent.extras?.getSerializable(ITEM)?.let {
            val parent = Intent(this, DishDetailsActivity::class.java)
            // to return to the right activity, the basket activity need the category
            parent.putExtra(ITEM, it)
            startActivity(parent)
        } ?: run {
            // by default
            val parent = Intent(this, HomeActivity::class.java)
            startActivity(parent)
        }
    }

    private fun signIn(user: User, idShop: String): JsonObjectRequest {
        // params
        val params = JSONObject()
        params.put("id_shop", idShop)
        user.toSignInParams(params)
        return JsonObjectRequest(
            Request.Method.POST, API_LOGIN_URL, params,
            Response.Listener { response ->
                Log.d(TAG, "Sign In Response: $response")
                Gson().fromJson(response["data"].toString(), RegisterResponse::class.java).let {
                    val sharedPref = this.getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putInt(ID_CLIENT, it.id)
                        apply()
                    }
                }
            },
            Response.ErrorListener { error ->
                Log.e(DishesListActivity.TAG, "Error: ${error.message}")
            })
    }

    companion object {
        val TAG = SignInActivity::class.java.simpleName
    }
}