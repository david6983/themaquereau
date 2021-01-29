package fr.isen.david.themaquereau.model.database

import androidx.room.*
import fr.isen.david.themaquereau.model.converters.Converters
import fr.isen.david.themaquereau.model.dao.OrderDao
import fr.isen.david.themaquereau.model.domain.Order


@Database(entities = [Order::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao

    companion object {
        val DB_NAME = "themaquereau.db"
    }
}