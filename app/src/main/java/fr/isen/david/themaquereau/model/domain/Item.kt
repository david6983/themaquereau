package fr.isen.david.themaquereau.model.domain

import androidx.room.*
import java.io.Serializable

data class Item(
        val item_id: Long,
        @ColumnInfo(name = "name_fr")
        val name_fr: String,
        @ColumnInfo(name = "name_en")
        val name_en: String,
        @ColumnInfo(name = "id_category")
        val id_category: Long,
        @ColumnInfo(name = "categ_name_fr")
        val categ_name_fr: String,
        @ColumnInfo(name = "categ_name_en")
        val categ_name_en: String,
        @Ignore
        val images: List<String>,
        @Relation(
                parentColumn = "item_id",
                entityColumn = "ingredient_id"
        )
        val ingredients: List<Ingredient>,
        @Relation(
                parentColumn = "item_id",
                entityColumn = "price_id"
        )
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
                listOf<Ingredient>(),
                listOf<Price>()
        )
}