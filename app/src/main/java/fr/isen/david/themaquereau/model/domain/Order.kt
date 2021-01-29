package fr.isen.david.themaquereau.model.domain

import androidx.room.*
import java.io.Serializable
import java.util.*

@Entity
data class Order(
    @PrimaryKey(autoGenerate = true)
    var order_id: Int,
    @Ignore val item: Item,
    @ColumnInfo(name = "quantity")
    var quantity: Int,
    @ColumnInfo(name = "real_price")
    var realPrice: Double
) : Serializable {
    constructor() : this(
        0,
        Item(),
        0,
        0.0
    )
}