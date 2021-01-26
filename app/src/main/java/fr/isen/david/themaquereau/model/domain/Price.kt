package fr.isen.david.themaquereau.model.domain

import java.util.*

data class Price(
    val id: Long,
    val id_pizza: Long,
    val id_size: Long,
    val price: Double,
    val create_date: Date,
    val update_date: Date,
    val size: String
) {
    constructor() : this(0, 0, 0, 0.0, Date(), Date(), "")
}