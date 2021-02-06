package fr.isen.david.themaquereau

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.wajahatkarim3.easyvalidation.core.Validator
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import fr.isen.david.themaquereau.databinding.ActivitySignUpBinding
import fr.isen.david.themaquereau.helpers.ApiHelperImpl
import fr.isen.david.themaquereau.helpers.AppPreferencesHelperImpl
import fr.isen.david.themaquereau.model.domain.User
import fr.isen.david.themaquereau.util.displayToast
import org.koin.android.ext.android.inject


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var inputName: EditText
    private lateinit var inputLastName: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputAddress: EditText
    private lateinit var inputPassword: EditText

    private val preferencesImpl: AppPreferencesHelperImpl by inject()
    private val api: ApiHelperImpl by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        inputName = binding.inputName
        inputLastName = binding.inputLastname
        inputEmail = binding.inputEmail
        inputAddress = binding.inputAddress
        inputPassword = binding.inputPassword

        binding.submitSignUp.setOnClickListener {
            val emailValid = emailValidator().check()
            val passwordValid = passwordValidator().check()
            val nameValid = textValidator(inputName).check()
            val lastnameValid = textValidator(inputLastName).check()
            val addressValid = textValidator(inputAddress).check()

            if (
                emailValid &&
                passwordValid &&
                nameValid &&
                lastnameValid &&
                addressValid
            ) {
                val user = User(
                   inputName.text.toString(),
                   inputLastName.text.toString(),
                   inputEmail.text.toString(),
                   inputAddress.text.toString(),
                   inputPassword.text.toString()
                )
                Log.d(TAG, "new sign up : $user")
                api.signUp(user, onSignUpCallback, onSignUpErrorCallback)
            }
        }

        binding.alreadyHaveAccountLink.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            //intent.putExtra(ItemAdapter.ITEM, item)
            startActivity(intent)
        }
    }

    override fun getSupportParentActivityIntent(): Intent? {
        return getParentActivityIntentImpl()
    }

    override fun getParentActivityIntent(): Intent? {
        return getParentActivityIntentImpl()
    }

    private fun getParentActivityIntentImpl(): Intent {
        var parentIntent = Intent(this, HomeActivity::class.java)
        // give back the item to the parent
        intent.extras?.getSerializable(ITEM)?.let {
            parentIntent = Intent(this, DishDetailsActivity::class.java)
            parentIntent.putExtra(ITEM, it)
        }
        return parentIntent
    }

    private fun emailValidator(): Validator {
        return inputEmail.validator()
            .nonEmpty()
            .validEmail()
            .addErrorCallback {
                inputEmail.error = when(it) {
                    ERROR_EMPTY -> getString(R.string.error_empty)
                    else -> getString(R.string.error_email_non_valid)
                }
                inputEmail.setTextColor(getColor(R.color.invalid))
            }
            .addSuccessCallback {
                inputEmail.setTextColor(getColor(R.color.valid))
            }
    }

    private fun passwordValidator(): Validator {
        return inputPassword.validator()
            .nonEmpty()
            .minLength(PASSWORD_LENGTH) // Based on OWASP recommendation
            .atleastOneNumber()
            .atleastOneUpperCase()
            .atleastOneSpecialCharacters()
            .addErrorCallback {
                inputPassword.error = it
                inputPassword.setTextColor(getColor(R.color.invalid))
            }
            .addSuccessCallback {
                inputPassword.setTextColor(getColor(R.color.valid))
            }
    }

    private fun textValidator(input: EditText): Validator {
        return input.validator()
            .nonEmpty()
            .addErrorCallback {
                input.error  = getString(R.string.error_empty)
                input.setTextColor(getColor(R.color.invalid))
            }
            .addSuccessCallback {
                input.setTextColor(getColor(R.color.valid))
            }
    }

    private val onSignUpCallback = { userId: Int ->
        preferencesImpl.setClientId(userId)
        displayToast(getString(R.string.sign_up_success), applicationContext)
        redirectToParent()
    }

    private val onSignUpErrorCallback = {
        invalidateInput(inputEmail)
        displayToast(getString(R.string.sign_up_error), applicationContext)
    }

    private fun invalidateInput(input: EditText) {
        input.setTextColor(getColor(R.color.invalid))
        input.error = getString(R.string.error_exist)
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

    companion object {
        val TAG = SignUpActivity::class.java.simpleName
    }
}