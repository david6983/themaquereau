package fr.isen.david.themaquereau.model.domain

import java.io.Serializable
import java.util.*

data class HistoryOrder(
    val id_sender: String,
    val id_receiver: String,
    val sender: String,
    val receiver: String,
    val code: String,
    val message: String,
    val create_date: Date
) : Serializable