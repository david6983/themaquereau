package fr.isen.david.themaquereau.model.domain

import java.io.Serializable

data class Item(
        val id: Long,
        val name_fr: String,
        val name_en: String,
        val id_category: Long,
        val categ_name_fr: String,
        val categ_name_en: String,
        val images: List<String>,
        val ingredients: List<Ingredient>,
        val prices: List<Price>
) : Serializable {
        constructor() : this(
                0,
                "",
                "",
                0,
                "",
                "",
                listOf(),
                listOf(),
                listOf()
        )
}