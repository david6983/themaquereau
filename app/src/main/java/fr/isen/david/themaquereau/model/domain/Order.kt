package fr.isen.david.themaquereau.model.domain

import java.io.Serializable

data class Order(
    val id: Int,
    val item: Item,
    var quantity: Int,
    var realPrice: Double
) : Serializable