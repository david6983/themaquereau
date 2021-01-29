package fr.isen.david.themaquereau.model.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ingredient_id")
    val ingredient_id: Long,
    @ColumnInfo(name = "id_shop")
    val id_shop: Long,
    @ColumnInfo(name = "name_fr")
    val name_fr: String,
    @ColumnInfo(name = "create_date")
    val create_date: Date,
    @ColumnInfo(name = "update_date")
    val update_date: Date,
    @ColumnInfo(name = "id_pizza")
    val id_pizza: Long
) : Serializable {
    constructor() : this(
        0,
        0,
        "",
        Date(),
        Date(),
        0
    )
}