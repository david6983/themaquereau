package fr.isen.david.themaquereau.model.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity
data class Price(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "price_id")
    val price_id: Long,
    @ColumnInfo(name = "id_pizza")
    val id_pizza: Long,
    @ColumnInfo(name = "id_size")
    val id_size: Long,
    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "create_date")
    val create_date: Date,
    @ColumnInfo(name = "update_date")
    val update_date: Date,
    @ColumnInfo(name = "size")
    val size: String
) : Serializable {
    constructor() : this(
        0,
        0,
        0,
        0.0,
        Date(),
        Date(),
        ""
    )
}