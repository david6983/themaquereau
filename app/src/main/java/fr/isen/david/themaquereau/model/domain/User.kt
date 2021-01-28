package fr.isen.david.themaquereau.model.domain

import java.io.Serializable

//TODO add salt for password
data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
) : Serializable