package fr.isen.david.themaquereau.helpers

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import fr.isen.david.themaquereau.ORDER_FILE
import fr.isen.david.themaquereau.model.domain.Order
import java.io.*

interface PersistOrdersHelper {
    fun readOrders(
        userId: Int,
        callback: (MutableList<Order>) -> (Unit),
        errorCallback: () -> (Unit)
    )
    fun saveOrder(
        userId: Int,
        order: Order,
        callback: (MutableList<Order>) -> (Unit),
        errorCallback: (quantity: Int) -> (Unit)
    )
    fun deleteOrder(
        userId: Int,
        position: Int,
        callback: (MutableList<Order>) -> (Unit),
        errorCallback: () -> (Unit)
    )
}

class PersistOrdersHelperImpl(
    private val context: Context
): PersistOrdersHelper {
    private val gson: Gson = Gson()

    override fun readOrders(
        userId: Int,
        callback: (MutableList<Order>) -> (Unit),
        errorCallback: () -> (Unit)
    ) {
        val content = readText(userId)
        if (content == "") {
            errorCallback()
        } else {
            val orders = gson.fromJson(content, Array<Order>::class.java).toMutableList()
            callback(orders)
        }
    }

    private fun readText(userId: Int): String {
        val file = getFile(userId)
        return file.readText()
    }

    private fun writeContent(userId: Int, content: ByteArray) {
        val file = getFile(userId)
        file.outputStream().apply {
            write(content)
            flush()
            close()
        }
    }

    override fun saveOrder(
        userId: Int, order: Order,
        callback: (MutableList<Order>) -> (Unit),
        errorCallback: (quantity: Int) -> (Unit)
    ) {
        val content: String = readText(userId)
        if (content != "") {
            val orders = gson.fromJson(content, Array<Order>::class.java).toMutableList()
            // update id
            order.order_id += 1
            // verify if the order already exist by name
            orders.find { ord -> ord.item.name_fr == order.item.name_fr }.let { foundOrder ->
                if (foundOrder !== null) {
                    val position = orders.indexOf(foundOrder)
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
            writeContent(userId, newJsonOrders.toByteArray())
        } else {
            val orders = JsonArray()
            val jsonOrder = gson.toJsonTree(order)
            orders.add(gson.toJsonTree(jsonOrder))
            writeContent(userId, orders.toString().toByteArray())
            val newOrders = gson.fromJson(orders.toString(), Array<Order>::class.java).toMutableList()
            // update quantity
            callback(newOrders)
        }
    }

    override fun deleteOrder(
        userId: Int,
        position: Int,
        callback: (MutableList<Order>) -> (Unit),
        errorCallback: () -> (Unit)
    ) {
        val content: String = readText(userId)
        if (content != "") {
            val gson = Gson()
            val ordersFromFile = gson.fromJson(content, Array<Order>::class.java).toMutableList()
            // delete the order
            ordersFromFile.removeAt(position)
            val ordersToFile = gson.toJson(ordersFromFile)
            writeContent(userId, ordersToFile.toString().toByteArray())

            callback(ordersFromFile)
        } else {
            errorCallback()
        }
    }

    private fun getFile(userId: Int): File {
        val fileName = "$ORDER_FILE$userId"
        val file = File(context.filesDir, fileName)
        if(!file.exists()){
            file.createNewFile()
        }else{
            Log.i(TAG, "file already exist")
        }
        return file
    }

    companion object {
        val TAG: String = PersistOrdersHelperImpl::class.java.simpleName
    }
}