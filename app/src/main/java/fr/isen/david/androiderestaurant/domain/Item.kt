package fr.isen.david.androiderestaurant.domain

data class Item(
    val id: Long,
    val name_fr: String,
    val name_en: String,
    val id_category: Long,
    val categ_name_fr: String,
    val categ_name_en: String,
    val images: ArrayList<String>,
    val ingredients: ArrayList<Ingredient>,
    val prices: ArrayList<Price>
)