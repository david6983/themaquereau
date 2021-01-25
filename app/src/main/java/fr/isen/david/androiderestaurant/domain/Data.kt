package fr.isen.david.androiderestaurant.domain

data class Data(
    val name_fr: String,
    val name_en: String,
    val items: ArrayList<Item>
)