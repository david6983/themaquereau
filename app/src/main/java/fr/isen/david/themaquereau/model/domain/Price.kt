package fr.isen.david.themaquereau.model.domain

import java.io.Serializable
import java.util.*

data class Price(
    val id: Long,
    val id_pizza: Long,
    val id_size: Long,
    val price: Double,
    val create_date: Date,
    val update_date: Date,
    val size: String
) : Serializable