package fr.isen.david.themaquereau.helpers

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import fr.isen.david.themaquereau.DishDetailsActivity
import fr.isen.david.themaquereau.ENC_VALUE
import fr.isen.david.themaquereau.IV_VALUE
import fr.isen.david.themaquereau.ORDER_FILE
import fr.isen.david.themaquereau.model.domain.Order
import fr.isen.david.themaquereau.util.fromByteToString
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
    private val context: Context,
    private val encryption: AesEncryptHelperImpl
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
            //writeEncrypt(userId, newJsonOrders.toByteArray())
            writeContent(userId, newJsonOrders.toByteArray())
            Log.i(TAG, "updated orders: $newJsonOrders")
        } else {
            val orders = JsonArray()
            val jsonOrder = gson.toJsonTree(order)
            orders.add(gson.toJsonTree(jsonOrder))
            //writeEncrypt(userId, orders.toString().toByteArray())
            writeContent(userId, orders.toString().toByteArray())
            val newOrders = gson.fromJson(orders.toString(), Array<Order>::class.java).toMutableList()
            // update quantity
            callback(newOrders)
            Log.i(TAG, "order saved: $jsonOrder")
        }
    }

    private fun writeEncrypt(userId: Int, bytes: ByteArray) {
        val enc = encryption.encryptData(bytes)
        val encryptedData = enc[ENC_VALUE]!!.fromByteToString()
        val ivVector = enc[IV_VALUE]!!.fromByteToString()
        writeContent(userId, "$ivVector;$encryptedData".toByteArray())
        //Log.i(TAG, "decrypted: ${decryptContent("$ivVector;$encryptedData")}")
    }

    private fun decryptContent(content: String): String {
        val sp = content.split(";")
        return encryption.decryptNoBase(sp[0].toByteArray(), sp[1].toByteArray())
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
            Log.i(DishDetailsActivity.TAG, "deleted order from basket: $ordersToFile")

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