package fr.isen.david.themaquereau

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import fr.isen.david.themaquereau.databinding.ActivitySignInBinding
import fr.isen.david.themaquereau.helpers.ApiHelperImpl
import fr.isen.david.themaquereau.helpers.AppPreferencesHelperImpl
import fr.isen.david.themaquereau.model.domain.User
import fr.isen.david.themaquereau.util.displayToast
import org.koin.android.ext.android.inject

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    private val preferencesImpl: AppPreferencesHelperImpl by inject()
    private val api: ApiHelperImpl by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Set checkbox if not the first time
        preferencesImpl.getFirstTimeSignIn().let {
            binding.remindMeCheckBox.isChecked = true
        }

        binding.submitSignIn.setOnClickListener {
            val user = User(
                "",
                "",
                binding.emailInput.text.toString(),
                "",
                binding.passwordInput.text.toString()
            )
            api.signIn(user, loginCallback)

            if (binding.remindMeCheckBox.isChecked) {
                Log.i(TAG, "checked")
                preferencesImpl.setFirstTimeSignIn(false)
            } else {
                Log.i(TAG, "noy checked")
                preferencesImpl.setFirstTimeSignIn(true)
            }

            displayToast(getString(R.string.log_in_success), applicationContext)
            val parent = getParentIntent()
            startActivity(parent)
        }

        binding.noAccountLink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.extras?.getSerializable(ITEM)?.let {
                intent.putExtra(ITEM, it)
            }
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun getSupportParentActivityIntent(): Intent? {
        return getParentActivityIntentImpl()
    }

    override fun getParentActivityIntent(): Intent? {
        return getParentActivityIntentImpl()
    }

    private fun getParentActivityIntentImpl(): Intent {
        return getParentIntent()
    }

    private fun getParentIntent(): Intent {
        // redirect to dish list
        intent.extras?.getSerializable(ITEM)?.let {
            val parent = Intent(this, DishDetailsActivity::class.java)
            // to return to the right activity, the basket activity need the category
            parent.putExtra(ITEM, it)
            return parent
        } ?: run {
            intent.extras?.getInt(CATEGORY)?.let {
                val parent = Intent(this, DishesListActivity::class.java)
                parent.putExtra(CATEGORY, it)
                return parent
            } ?:run {
                // by default
                return Intent(this, HomeActivity::class.java)
            }
        }
    }

    private val loginCallback = { userId: Int -> preferencesImpl.setClientId(userId) }

    companion object {
        val TAG: String = SignInActivity::class.java.simpleName
    }
}