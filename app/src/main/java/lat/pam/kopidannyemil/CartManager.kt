package lat.pam.kopidannyemil

import android.content.Context
import org.json.JSONObject

object CartManager {

    private const val PREF_NAME = "KopiNyemilCart"
    private const val CART_KEY = "cart_items"

    // Mengambil data keranjang
    fun getItems(context: Context): Map<String, Int> {
        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        val jsonString = preferences.getString(CART_KEY, "{}")
        val jsonObject = JSONObject(jsonString ?: "{}")

        val items = mutableMapOf<String, Int>()

        val keys = jsonObject.keys()

        while (keys.hasNext()) {
            val productId = keys.next()
            val quantity = jsonObject.getInt(productId)

            items[productId] = quantity
        }

        return items
    }

    // Menambahkan produk ke keranjang
    fun addItem(
        context: Context,
        productId: String,
        quantity: Int
    ) {
        val items = getItems(context).toMutableMap()

        val currentQuantity = items[productId] ?: 0

        items[productId] = currentQuantity + quantity

        saveItems(context, items)
    }

    // Mengubah jumlah produk
    fun updateQuantity(
        context: Context,
        productId: String,
        quantity: Int
    ) {
        val items = getItems(context).toMutableMap()

        if (quantity <= 0) {
            items.remove(productId)
        } else {
            items[productId] = quantity
        }

        saveItems(context, items)
    }

    // Menghapus produk dari keranjang
    fun removeItem(
        context: Context,
        productId: String
    ) {
        val items = getItems(context).toMutableMap()

        items.remove(productId)

        saveItems(context, items)
    }

    // Menghapus semua isi keranjang
    fun clearCart(context: Context) {
        saveItems(context, emptyMap())
    }

    // Menyimpan data keranjang
    private fun saveItems(
        context: Context,
        items: Map<String, Int>
    ) {
        val jsonObject = JSONObject()

        for ((productId, quantity) in items) {
            jsonObject.put(productId, quantity)
        }

        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        preferences.edit()
            .putString(CART_KEY, jsonObject.toString())
            .apply()
    }
}