package com.aquihay.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aquihay.app.model.Product
import com.aquihay.app.model.Shop

@Composable
fun ShopsScreen(shops: List<Shop>, products: List<Product>, onSelectShop: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { SectionHeading("Comercios registrados", "Selecciona un local para conocer sus artículos y horario") }
        item { DemoNotice() }
        if (shops.isEmpty()) {
            item { Text("Todavía no hay comercios. Crea uno desde la pestaña Inventario.") }
        }
        items(shops.sortedBy { it.name }, key = { it.id }) { shop ->
            val count = products.count { it.shopId == shop.id }
            val availableCount = products.count { it.shopId == shop.id && it.stock > 0 }
            ElevatedCard(onClick = { onSelectShop(shop.id) }, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Store, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(shop.name, style = MaterialTheme.typography.titleMedium)
                        Text(shop.neighborhood, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$availableCount de $count productos con existencias", style = MaterialTheme.typography.bodySmall)
                        Text(shop.openingHours, style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Ver comercio")
                }
            }
        }
    }
}
