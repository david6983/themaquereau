package fr.isen.david.themaquereau.model.domain

import java.io.Serializable

data class Data(
    val name_fr: String,
    val name_en: String,
    var items: List<Item>
) : Serializable