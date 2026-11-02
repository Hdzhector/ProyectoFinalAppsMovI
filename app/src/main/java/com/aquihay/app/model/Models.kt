package com.aquihay.app.model

data class Shop(
    val id: String,
    val name: String,
    val neighborhood: String,
    val address: String,
    val openingHours: String
)

data class Product(
    val id: String,
    val shopId: String,
    val name: String,
    val category: String,
    val price: Double,
    val stock: Int,
    val updatedAt: Long
)

data class Inventory(val shops: List<Shop>, val products: List<Product>)

enum class PriceSort { NAME, CHEAPEST, MOST_EXPENSIVE }

val categories = listOf("Abarrotes", "Papelería", "Ferretería", "Hogar", "Otros")
