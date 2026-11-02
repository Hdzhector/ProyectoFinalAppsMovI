package com.aquihay.app.model

import org.junit.Assert.*
import org.junit.Test

class SearchAndValidationTest {
    private val shops = listOf(
        Shop("s1", "Tienda Uno", "Roma Norte", "Roma", "09-18"),
        Shop("s2", "Tienda Dos", "Narvarte", "Narvarte", "09-18")
    )
    private val products = listOf(
        Product("1", "s1", "Pilas AA", "Hogar", 55.0, 4, 0L),
        Product("2", "s2", "Pilas AA", "Hogar", 40.0, 0, 0L),
        Product("3", "s1", "Leche", "Abarrotes", 28.0, 10, 0L)
    )

    @Test fun searchIgnoresCaseAndAccents() {
        val results = searchProducts(products, shops, "pÍLaS", null, null, false, PriceSort.NAME)
        assertEquals(2, results.size)
    }

    @Test fun availableFilterExcludesZeroStock() {
        val results = searchProducts(products, shops, "pilas", null, null, true, PriceSort.NAME)
        assertEquals(listOf("1"), results.map { it.id })
    }

    @Test fun zoneAndPriceSortWorkTogether() {
        val results = searchProducts(products, shops, "", "Hogar", "Narvarte", false, PriceSort.CHEAPEST)
        assertEquals(listOf("2"), results.map { it.id })
    }

    @Test fun cheapestSortOrdersProductsCorrectly() {
        val results = searchProducts(products, shops, "pilas", null, null, false, PriceSort.CHEAPEST)
        assertEquals(listOf("2", "1"), results.map { it.id })
    }

    @Test fun productValidationsRejectBadInputs() {
        assertNotNull(validateProduct("", "Hogar", "40", "2"))
        assertNotNull(validateProduct("Cuaderno", "Hogar", "-2", "2"))
        assertNotNull(validateProduct("Cuaderno", "Hogar", "20", "2.5"))
        assertNotNull(validateProduct("Cuaderno", "Hogar", "20.999", "3"))
        assertNull(validateProduct("Cuaderno", "Papelería", "20,50", "0"))
    }

    @Test fun shopValidationsRejectEmptyRequiredFields() {
        assertNotNull(validateShop("Tienda", "", "Dirección", "9-18"))
        assertNull(validateShop("Tienda", "Roma", "Dirección", "9-18"))
    }
}
