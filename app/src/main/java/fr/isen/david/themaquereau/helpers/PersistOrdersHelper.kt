package fr.isen.david.themaquereau.helpers

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import fr.isen.david.themaquereau.DishDetailsActivity
import fr.isen.david.themaquereau.ORDER_FILE
import fr.isen.david.themaquereau.ORDER_FILE_SUFFIX
import fr.isen.david.themaquereau.model.domain.Order
import java.io.BufferedReader
import java.io.FileNotFoundException

interface PersistOrdersHelper {
    fun readOrders(userId: Int, callback: (MutableList<Order>) -> (Unit), errorCallback: () -> (Unit))
    fun saveOrder(userId: Int, order: Order, callback: (MutableList<Order>) -> (Unit), errorCallback: (quantity: Int) -> (Unit))
    fun deleteOrder(userId: Int, position: Int, callback: (MutableList<Order>) -> (Unit), errorCallback: () -> (Unit))
}

class PersistOrdersHelperImpl(private val context: Context): PersistOrdersHelper {
    private val gson: Gson = Gson()

    override fun readOrders(
        userId: Int,
        callback: (MutableList<Order>) -> (Unit),
        errorCallback: () -> (Unit)
    ) {
        try {
            context.openFileInput("$ORDER_FILE$userId$ORDER_FILE_SUFFIX").use { inputStream ->
                inputStream.bufferedReader().use {
                    val orders = gson.fromJson(it.readText(), Array<Order>::class.java).toMutableList()
                    if (orders.isEmpty()) {
                        errorCallback()
                    } else {
                        callback(orders)
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            errorCallback()
        }
    }

    override fun saveOrder(
        userId: Int, order: Order,
        callback: (MutableList<Order>) -> (Unit),
        errorCallback: (quantity: Int) -> (Unit)
    ) {
        try {
            context.openFileInput("$ORDER_FILE$userId$ORDER_FILE_SUFFIX").use { inputStream ->
                inputStream.bufferedReader().use { reader ->
                    val orders = gson.fromJson(reader.readText(), Array<Order>::class.java).toMutableList()
                    // update id
                    order.order_id += 1
                    // verify if the order already exist by name
                    orders.find { ord -> ord.item.name_fr == order.item.name_fr }.let { foundOrder ->
                        if (foundOrder !== null) {
                            val position = orders.indexOf(foundOrder);
                            foundOrder.quantity += order.quantity
                            foundOrder.realPrice += order.realPrice
                            orders.set(position, foundOrder)
                        } else {
                            orders.add(order)
                        }
                    }
                    // update quantity
                    callback(orders)
                    // Save order
                    val newJsonOrders = gson.toJson(orders)
                    context.openFileOutput("$ORDER_FILE$userId$ORDER_FILE_SUFFIX", Context.MODE_PRIVATE).use { outputStream ->
                        outputStream.write(newJsonOrders.toString().toByteArray())
                        Log.i(TAG, "updated orders: $newJsonOrders")
                    }
                }
            }
        } catch(e: FileNotFoundException) {
            val orders = JsonArray()
            val jsonOrder = gson.toJsonTree(order)
            orders.add(gson.toJsonTree(jsonOrder))
            context.openFileOutput("$ORDER_FILE$userId$ORDER_FILE_SUFFIX", Context.MODE_PRIVATE).use { outputStream ->
                outputStream.write(orders.toString().toByteArray())
                errorCallback(order.quantity)
                Log.i(TAG, "order saved: $jsonOrder")
            }
        }
    }

    override fun deleteOrder(userId: Int, position: Int, callback: (MutableList<Order>) -> (Unit), errorCallback: () -> (Unit)) {
        try {
            context.openFileInput("$ORDER_FILE$userId$ORDER_FILE_SUFFIX").use { inputStream ->
                inputStream.bufferedReader().use { reader ->
                    val gson = Gson()
                    val ordersFromFile = retrieveOrders(reader, gson)
                    // delete the order
                    ordersFromFile.removeAt(position)
                    val ordersToFile = gson.toJson(ordersFromFile)
                    // save the file again
                    context.openFileOutput("$ORDER_FILE$userId$ORDER_FILE_SUFFIX", Context.MODE_PRIVATE).use { outputStream ->
                        outputStream.write(ordersToFile.toString().toByteArray())
                    }
                    Log.i(DishDetailsActivity.TAG, "deleted order from basket: $ordersToFile")

                    callback(ordersFromFile)
                }
            }
        } catch(e: FileNotFoundException) {
            errorCallback()
        }
    }

    private fun retrieveOrders(reader: BufferedReader, gson: Gson): MutableList<Order> {
        return gson.fromJson(reader.readText(), Array<Order>::class.java).toMutableList()
    }

    companion object {
        val TAG: String = PersistOrdersHelperImpl::class.java.simpleName
    }
}