package fr.isen.david.themaquereau.model.domain

import java.util.*

data class Ingredient(
    val id: Long,
    val id_shop: Long,
    val name_fr: String,
    val create_date: Date,
    val update_date: Date,
    val id_pizza: Long
) {
    constructor() : this(0, 0, "", Date(), Date() , 0)
}