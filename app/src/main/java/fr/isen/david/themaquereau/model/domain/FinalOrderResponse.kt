package fr.isen.david.themaquereau.model.domain

import java.io.Serializable

data class FinalOrderResponse(
    val id_receiver: Int,
    val receiver: String
) : Serializable