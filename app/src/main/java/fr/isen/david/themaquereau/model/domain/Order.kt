package fr.isen.david.themaquereau.model.domain

import java.io.Serializable

data class Order(
    var id: Int,
    val item: Item,
    var quantity: Int,
    var realPrice: Double
) : Serializable