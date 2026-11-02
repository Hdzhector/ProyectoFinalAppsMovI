package com.aquihay.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aquihay.app.model.Product
import com.aquihay.app.model.Shop

@Composable
fun InventoryScreen(
    shops: List<Shop>,
    products: List<Product>,
    onSaveShop: (Shop) -> Unit,
    onDeleteShop: (String) -> Unit,
    onSaveProduct: (Product) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onResetDemo: () -> Unit
) {
    var activeShopId by rememberSaveable { mutableStateOf<String?>(null) }
    val activeShop = shops.find { it.id == activeShopId } ?: shops.firstOrNull()
    var showShopEditor by remember { mutableStateOf(false) }
    var shopToEdit by remember { mutableStateOf<Shop?>(null) }
    var shopToDelete by remember { mutableStateOf<Shop?>(null) }
    var showProductEditor by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var confirmRestore by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SectionHeading("Administrar inventario", "Agrega comercios y actualiza sus productos") }
        item {
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium) {
                Text(
                    "Edición local de demostración: cualquier persona que use este teléfono puede modificar estos datos. " +
                        "No existe autenticación ni sincronización con tiendas reales.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        item {
            Button(onClick = { shopToEdit = null; showShopEditor = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Agregar comercio")
            }
        }
        if (shops.isEmpty()) {
            item { Text("No hay establecimientos. Pulsa «Agregar comercio» para comenzar.") }
        } else {
            item { Text("Selecciona un comercio", style = MaterialTheme.typography.titleSmall) }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(shops, key = { it.id }) { shop ->
                        FilterChip(
                            selected = activeShop?.id == shop.id,
                            onClick = { activeShopId = shop.id },
                            label = { Text(shop.name) }
                        )
                    }
                }
            }
        }
        if (activeShop != null) {
            item {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(activeShop.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(activeShop.address, style = MaterialTheme.typography.bodySmall)
                        Text(activeShop.openingHours, style = MaterialTheme.typography.bodySmall)
                        Row {
                            TextButton(onClick = { shopToEdit = activeShop; showShopEditor = true }) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Editar local")
                            }
                            TextButton(onClick = { shopToDelete = activeShop }) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Productos (${products.count { it.shopId == activeShop.id }})", style = MaterialTheme.typography.titleMedium)
                    Button(onClick = { productToEdit = null; showProductEditor = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Producto")
                    }
                }
            }
            val shopProducts = products.filter { it.shopId == activeShop.id }.sortedBy { it.name }
            if (shopProducts.isEmpty()) {
                item { Text("Este comercio no tiene productos. Agrega el primero.") }
            }
            items(shopProducts, key = { it.id }) { product ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(product.name, fontWeight = FontWeight.SemiBold)
                            Text("${money(product.price)} · ${product.stock} en existencia", style = MaterialTheme.typography.bodySmall)
                            Text(lastUpdate(product.updatedAt), style = MaterialTheme.typography.labelSmall)
                        }
                        IconButton(onClick = { productToEdit = product; showProductEditor = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar ${product.name}")
                        }
                        IconButton(onClick = { productToDelete = product }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar ${product.name}")
                        }
                    }
                }
            }
        }
        item {
            HorizontalDivider()
            TextButton(onClick = { confirmRestore = true }) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Restablecer datos de demostración")
            }
        }
    }

    if (showShopEditor) {
        ShopEditorDialog(
            existing = shopToEdit,
            onDismiss = { showShopEditor = false },
            onSave = { saved ->
                onSaveShop(saved)
                activeShopId = saved.id
                showShopEditor = false
            }
        )
    }
    if (showProductEditor && activeShop != null) {
        ProductEditorDialog(
            shop = activeShop,
            existing = productToEdit,
            onDismiss = { showProductEditor = false },
            onSave = { saved ->
                onSaveProduct(saved)
                showProductEditor = false
            }
        )
    }
    val deletingProduct = productToDelete
    if (deletingProduct != null) {
        ConfirmDialog(
            title = "¿Eliminar producto?",
            message = "Se eliminará «${deletingProduct.name}» de este dispositivo.",
            onCancel = { productToDelete = null },
            onConfirm = { onDeleteProduct(deletingProduct.id); productToDelete = null }
        )
    }
    val deletingShop = shopToDelete
    if (deletingShop != null) {
        ConfirmDialog(
            title = "¿Eliminar comercio?",
            message = "Se eliminarán «${deletingShop.name}» y todos sus productos guardados en este dispositivo.",
            onCancel = { shopToDelete = null },
            onConfirm = {
                onDeleteShop(deletingShop.id)
                if (activeShopId == deletingShop.id) activeShopId = null
                shopToDelete = null
            }
        )
    }
    if (confirmRestore) {
        ConfirmDialog(
            title = "¿Restablecer la demostración?",
            message = "Perderás todos los comercios y productos que hayas agregado o modificado en este dispositivo.",
            onCancel = { confirmRestore = false },
            onConfirm = { onResetDemo(); activeShopId = null; confirmRestore = false }
        )
    }
}
