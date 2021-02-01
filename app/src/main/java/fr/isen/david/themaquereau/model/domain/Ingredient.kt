package fr.isen.david.themaquereau.model.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ingredient_id")
    val ingredient_id: Long,
    @ColumnInfo(name = "id_shop")
    val id_shop: Long,
    @ColumnInfo(name = "name_fr")
    val name_fr: String
) : Serializable