package fr.isen.david.themaquereau.model.dao

import androidx.room.*
import fr.isen.david.themaquereau.model.domain.Order

/**
 * The orders will be saved in a sqlite database
 */
@Dao
interface OrderDao {
    @Delete
    suspend fun delete(order: Order)

    @Update
    suspend fun updateOrders(vararg orders: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<Order>)
}