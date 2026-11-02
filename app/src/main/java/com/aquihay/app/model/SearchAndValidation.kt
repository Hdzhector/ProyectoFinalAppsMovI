package com.aquihay.app.model

import java.text.Normalizer

fun normalized(text: String): String = Normalizer.normalize(text.trim(), Normalizer.Form.NFD)
    .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    .lowercase()

fun searchProducts(
    products: List<Product>,
    shops: List<Shop>,
    text: String,
    category: String?,
    neighborhood: String?,
    onlyAvailable: Boolean,
    sort: PriceSort
): List<Product> {
    val knownShops = shops.associateBy { it.id }
    val needle = normalized(text)
    val result = products.filter { product ->
        val shop = knownShops[product.shopId] ?: return@filter false
        (needle.isBlank() || normalized(product.name).contains(needle) ||
            normalized(shop.name).contains(needle)) &&
            (category == null || product.category == category) &&
            (neighborhood == null || shop.neighborhood == neighborhood) &&
            (!onlyAvailable || product.stock > 0)
    }
    return when (sort) {
        PriceSort.NAME -> result.sortedWith(compareBy<Product> { normalized(it.name) }.thenBy { it.price })
        PriceSort.CHEAPEST -> result.sortedWith(compareBy<Product> { it.price }.thenBy { normalized(it.name) })
        PriceSort.MOST_EXPENSIVE -> result.sortedWith(compareByDescending<Product> { it.price }.thenBy { normalized(it.name) })
    }
}

fun validateShop(name: String, neighborhood: String, address: String, hours: String): String? = when {
    name.isBlank() -> "Escribe el nombre del comercio."
    name.trim().length > 60 -> "El nombre no debe superar 60 caracteres."
    neighborhood.isBlank() -> "Indica la colonia o zona."
    neighborhood.trim().length > 50 -> "La colonia no debe superar 50 caracteres."
    address.isBlank() -> "Indica una dirección."
    address.trim().length > 160 -> "La dirección es demasiado larga."
    hours.isBlank() -> "Indica el horario de atención."
    hours.trim().length > 80 -> "El horario es demasiado largo."
    else -> null
}

fun validateProduct(name: String, category: String, priceText: String, stockText: String): String? {
    val price = priceText.trim().replace(',', '.').toDoubleOrNull()
    val stock = stockText.trim().toIntOrNull()
    return when {
        name.isBlank() -> "Escribe el nombre del producto."
        name.trim().length > 70 -> "El nombre no debe superar 70 caracteres."
        category !in categories -> "Selecciona una categoría válida."
        !priceText.trim().matches(Regex("[0-9]{1,7}([.,][0-9]{1,2})?")) ||
            price == null || price <= 0.0 || price > 1_000_000.0 ->
            "El precio debe ser mayor a $0, numérico y con máximo 2 decimales."
        stock == null || stock < 0 || stock > 99999 || !stockText.trim().matches(Regex("[0-9]{1,5}")) ->
            "Las existencias deben ser un entero entre 0 y 99999."
        else -> null
    }
}
