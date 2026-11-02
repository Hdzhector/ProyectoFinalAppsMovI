package com.aquihay.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.aquihay.app.model.PriceSort
import com.aquihay.app.model.Product
import com.aquihay.app.model.Shop
import com.aquihay.app.model.categories
import com.aquihay.app.model.searchProducts

@Composable
fun SearchScreen(
    shops: List<Shop>,
    products: List<Product>,
    search: String,
    onSearchChange: (String) -> Unit,
    category: String?,
    onCategoryChange: (String?) -> Unit,
    neighborhood: String?,
    onNeighborhoodChange: (String?) -> Unit,
    onlyAvailable: Boolean,
    onAvailabilityChange: (Boolean) -> Unit,
    sort: PriceSort,
    onSortChange: (PriceSort) -> Unit,
    onSelectProduct: (String) -> Unit
) {
    val results = searchProducts(products, shops, search, category, neighborhood, onlyAvailable, sort)
    val shopsById = shops.associateBy { it.id }
    val zones = listOf("Todas las zonas") + shops.map { it.neighborhood }.distinct().sorted()
    val sorts = listOf("Nombre", "Menor precio", "Mayor precio")
    val selectedSort = when (sort) {
        PriceSort.NAME -> sorts[0]
        PriceSort.CHEAPEST -> sorts[1]
        PriceSort.MOST_EXPENSIVE -> sorts[2]
    }
    val filtered = search.isNotBlank() || category != null || neighborhood != null || onlyAvailable || sort != PriceSort.NAME

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { SectionHeading("¿Qué estás buscando?", "Consulta productos y compara comercios de la zona") }
        item { DemoNotice() }
        item {
            OutlinedTextField(
                value = search,
                onValueChange = onSearchChange,
                label = { Text("Producto o nombre del comercio") },
                placeholder = { Text("Ejemplo: Pilas AA") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (search.isNotBlank()) IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Borrar búsqueda")
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth()
            )
        }
        item { Text("Categorías", style = MaterialTheme.typography.titleSmall) }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(selected = category == null, onClick = { onCategoryChange(null) }, label = { Text("Todas") })
                }
                items(categories) { item ->
                    FilterChip(selected = category == item, onClick = { onCategoryChange(item) }, label = { Text(item) })
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("Colonia o zona", style = MaterialTheme.typography.labelSmall)
                    ChoiceMenu(neighborhood ?: "Todas las zonas", zones, onChoose = {
                        onNeighborhoodChange(if (it == "Todas las zonas") null else it)
                    })
                }
                Column(Modifier.weight(1f)) {
                    Text("Ordenar", style = MaterialTheme.typography.labelSmall)
                    ChoiceMenu(selectedSort, sorts, onChoose = {
                        onSortChange(when (it) {
                            "Menor precio" -> PriceSort.CHEAPEST
                            "Mayor precio" -> PriceSort.MOST_EXPENSIVE
                            else -> PriceSort.NAME
                        })
                    })
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = onlyAvailable,
                    onClick = { onAvailabilityChange(!onlyAvailable) },
                    label = { Text("Solo con existencias") }
                )
                if (filtered) {
                    TextButton(onClick = {
                        onSearchChange("")
                        onCategoryChange(null)
                        onNeighborhoodChange(null)
                        onAvailabilityChange(false)
                        onSortChange(PriceSort.NAME)
                    }) { Text("Limpiar filtros") }
                }
            }
        }
        item { Text("${results.size} resultado(s)", style = MaterialTheme.typography.titleMedium) }
        if (results.isEmpty()) {
            item {
                Text(
                    "No se encontraron productos con estos filtros. Prueba otra palabra, zona o categoría.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(results, key = { it.id }) { product ->
                val shop = shopsById[product.shopId]
                if (shop != null) ProductCard(product, shop, onClick = { onSelectProduct(product.id) })
            }
        }
    }
}
