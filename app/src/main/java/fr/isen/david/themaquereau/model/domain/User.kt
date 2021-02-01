package fr.isen.david.themaquereau.model.domain

import org.json.JSONObject
import java.io.Serializable

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val address: String,
    val password: String
) : Serializable {
    fun toSignUpParams(params: JSONObject) {
        params.put("firstname", firstName)
        params.put("lastname", lastName)
        params.put("email", email)
        params.put("address", address)
        params.put("password", password)
    }

    fun toSignInParams(params: JSONObject) {
        params.put("email", email)
        params.put("password", password)
    }
}