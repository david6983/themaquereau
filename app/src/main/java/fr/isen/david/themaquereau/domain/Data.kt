package fr.isen.david.themaquereau.domain

data class Data(
    val name_fr: String,
    val name_en: String,
    val items: List<Item>
)