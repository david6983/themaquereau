package fr.isen.david.themaquereau.model.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Price(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "price_id")
    val price_id: Long,
    @ColumnInfo(name = "price")
    val price: Double
) : Serializable