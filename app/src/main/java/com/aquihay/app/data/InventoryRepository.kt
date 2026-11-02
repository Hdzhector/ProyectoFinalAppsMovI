package com.sihay.app.data

import android.content.Context
import com.aquihay.app.model.Inventory
import com.aquihay.app.model.Product
import com.aquihay.app.model.Shop
import org.json.JSONArray
import org.json.JSONObject


class InventoryRepository(context: Context) {
    private val preferences = context.getSharedPreferences("Sihay_inventory", Context.MODE_PRIVATE)

    fun load(): Inventory {
        val text = preferences.getString("inventory_v1", null) ?: return demoInventory()
        return try {
            val json = JSONObject(text)
            val shops = json.getJSONArray("shops")
            val products = json.getJSONArray("products")
            Inventory(
                shops = List(shops.length()) { index ->
                    val item = shops.getJSONObject(index)
                    Shop(
                        id = item.getString("id"),
                        name = item.getString("name"),
                        neighborhood = item.getString("neighborhood"),
                        address = item.getString("address"),
                        openingHours = item.getString("openingHours")
                    )
                },
                products = List(products.length()) { index ->
                    val item = products.getJSONObject(index)
                    Product(
                        id = item.getString("id"),
                        shopId = item.getString("shopId"),
                        name = item.getString("name"),
                        category = item.getString("category"),
                        price = item.getDouble("price"),
                        stock = item.getInt("stock"),
                        updatedAt = item.getLong("updatedAt")
                    )
                }
            )
        } catch (_: Exception) {
            // Evitar bloqueo por contenido inválido en los datos locales.
            demoInventory()
        }
    }

    fun save(inventory: Inventory) {
        val shops = JSONArray()
        inventory.shops.forEach { shop ->
            shops.put(JSONObject().apply {
                put("id", shop.id)
                put("name", shop.name)
                put("neighborhood", shop.neighborhood)
                put("address", shop.address)
                put("openingHours", shop.openingHours)
            })
        }
        val products = JSONArray()
        inventory.products.forEach { product ->
            products.put(JSONObject().apply {
                put("id", product.id)
                put("shopId", product.shopId)
                put("name", product.name)
                put("category", product.category)
                put("price", product.price)
                put("stock", product.stock)
                put("updatedAt", product.updatedAt)
            })
        }
        preferences.edit().putString("inventory_v1", JSONObject().apply {
            put("shops", shops)
            put("products", products)
        }.toString()).apply()
    }
}


fun demoInventory(): Inventory {
    val shops = listOf(
        Shop("demo-roma", "Abarrotes La Esquina (demo)", "Roma Norte", "Roma Norte, Ciudad de México", "Lun–Sáb · 09:00–20:00"),
        Shop("demo-narvarte", "Papelería Central (demo)", "Narvarte", "Narvarte, Ciudad de México", "Lun–Vie · 08:00–19:00"),
        Shop("demo-valle", "Ferretería del Barrio (demo)", "Del Valle", "Del Valle, Ciudad de México", "Lun–Sáb · 09:00–18:00"),
        Shop("demo-coyoacan", "Hogar y Más (demo)", "Coyoacán", "Coyoacán, Ciudad de México", "Todos los días · 10:00–19:00")
    )
    fun product(id: String, shop: String, name: String, category: String, price: Double, stock: Int) =
        Product(id, shop, name, category, price, stock, 0L)
    val products = listOf(
        product("p01", "demo-roma", "Leche entera 1 L", "Abarrotes", 29.5, 12),
        product("p02", "demo-roma", "Arroz 1 kg", "Abarrotes", 34.0, 7),
        product("p03", "demo-roma", "Pilas AA paquete de 4", "Hogar", 54.0, 4),
        product("p04", "demo-narvarte", "Pilas AA paquete de 4", "Hogar", 49.0, 8),
        product("p05", "demo-narvarte", "Cuaderno profesional", "Papelería", 37.0, 16),
        product("p06", "demo-narvarte", "Pegamento en barra", "Papelería", 22.5, 0),
        product("p07", "demo-valle", "Pilas AA paquete de 4", "Hogar", 51.0, 5),
        product("p08", "demo-valle", "Foco LED 9 W", "Ferretería", 45.0, 6),
        product("p09", "demo-valle", "Cinta aislante", "Ferretería", 23.0, 0),
        product("p10", "demo-coyoacan", "Jabón líquido 1 L", "Hogar", 62.0, 9),
        product("p11", "demo-coyoacan", "Detergente 1 kg", "Hogar", 48.0, 3),
        product("p12", "demo-coyoacan", "Pilas AA paquete de 4", "Hogar", 58.0, 2)
    )
    return Inventory(shops, products)
}
