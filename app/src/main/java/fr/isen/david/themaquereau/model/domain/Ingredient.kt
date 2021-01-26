package fr.isen.david.themaquereau.model.domain

import java.io.Serializable
import java.util.*

data class Ingredient(
    val id: Long,
    val id_shop: Long,
    val name_fr: String,
    val create_date: Date,
    val update_date: Date,
    val id_pizza: Long
) : Serializable