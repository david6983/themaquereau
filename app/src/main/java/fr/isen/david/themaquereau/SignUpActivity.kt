package fr.isen.david.themaquereau

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.wajahatkarim3.easyvalidation.core.Validator
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import fr.isen.david.themaquereau.adapters.ItemAdapter
import fr.isen.david.themaquereau.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var inputName: EditText
    private lateinit var inputLastName: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputAddress: EditText
    private lateinit var inputPassword: EditText

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
            emailValidator().check()
            passwordValidator().check()
            textValidator(inputName).check()
            textValidator(inputLastName).check()
            textValidator(inputAddress).check()
            Log.i(TAG, "name: ${inputName.text}; last name: ${inputLastName.text}; email: ${inputEmail.text}; address: ${inputAddress.text}; password: ${inputPassword.text}")

        }
    }

    override fun getSupportParentActivityIntent(): Intent? {
        return getParentActivityIntentImpl()
    }

    override fun getParentActivityIntent(): Intent? {
        return getParentActivityIntentImpl()
    }

    private fun getParentActivityIntentImpl(): Intent {
        val parentIntent = Intent(this, DishDetailsActivity::class.java)
        // give back the item to the parent
        intent.extras?.getSerializable(ItemAdapter.ITEM)?.let {
            parentIntent.putExtra(ItemAdapter.ITEM, it)

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

    companion object {
        val TAG = SignUpActivity::class.java.simpleName
        const val PASSWORD_LENGTH: Int = 12
        const val ERROR_EMPTY = "Can't be empty!"
        const val ERROR_LENGTH = "Length should be greater than"
        const val ERROR_NO_NUMBER = "At least one letter should be a number!"
        const val ERROR_NO_UPPERCASE = "At least one letter should be in uppercase!"
        const val ERROR_NO_SPECIAL = "Should contain at least 1 special characters!"
    }
}