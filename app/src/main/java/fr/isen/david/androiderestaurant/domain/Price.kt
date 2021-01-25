package fr.isen.david.androiderestaurant.domain

import java.util.*

data class Price(
    val id: Long,
    val id_pizza: Long,
    val id_size: Long,
    val price: Int,
    val create_date: Date,
    val update_date: Date,
    val size: String
)