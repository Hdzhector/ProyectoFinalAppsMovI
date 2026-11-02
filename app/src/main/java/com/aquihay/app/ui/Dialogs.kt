package com.aquihay.app.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.aquihay.app.model.Product
import com.aquihay.app.model.Shop
import com.aquihay.app.model.categories
import com.aquihay.app.model.validateProduct
import com.aquihay.app.model.validateShop
import java.util.UUID

@Composable
fun ConfirmDialog(title: String, message: String, onCancel: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Confirmar")
            }
        },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancelar") } }
    )
}

@Composable
fun ShopEditorDialog(existing: Shop?, onDismiss: () -> Unit, onSave: (Shop) -> Unit) {
    var name by remember(existing?.id) { mutableStateOf(existing?.name ?: "") }
    var neighborhood by remember(existing?.id) { mutableStateOf(existing?.neighborhood ?: "") }
    var address by remember(existing?.id) { mutableStateOf(existing?.address ?: "") }
    var hours by remember(existing?.id) { mutableStateOf(existing?.openingHours ?: "") }
    var validation by remember(existing?.id) { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Nuevo comercio" else "Editar comercio") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 440.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Todos los campos son obligatorios.", style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(value = name, onValueChange = { name = it; validation = null }, label = { Text("Nombre del local") }, singleLine = true)
                OutlinedTextField(value = neighborhood, onValueChange = { neighborhood = it; validation = null }, label = { Text("Colonia o zona") }, singleLine = true)
                OutlinedTextField(value = address, onValueChange = { address = it; validation = null }, label = { Text("Dirección para el mapa") }, minLines = 2, maxLines = 3)
                OutlinedTextField(value = hours, onValueChange = { hours = it; validation = null }, label = { Text("Horario de atención") }, singleLine = true)
                SmallError(validation)
            }
        },
        confirmButton = {
            Button(onClick = {
                val error = validateShop(name, neighborhood, address, hours)
                if (error != null) validation = error
                else onSave(Shop(
                    id = existing?.id ?: UUID.randomUUID().toString(),
                    name = name.trim(),
                    neighborhood = neighborhood.trim(),
                    address = address.trim(),
                    openingHours = hours.trim()
                ))
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun ProductEditorDialog(shop: Shop, existing: Product?, onDismiss: () -> Unit, onSave: (Product) -> Unit) {
    var name by remember(existing?.id) { mutableStateOf(existing?.name ?: "") }
    var category by remember(existing?.id) { mutableStateOf(existing?.category ?: categories.first()) }
    var price by remember(existing?.id) { mutableStateOf(existing?.price?.toString() ?: "") }
    var stock by remember(existing?.id) { mutableStateOf(existing?.stock?.toString() ?: "") }
    var validation by remember(existing?.id) { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Agregar producto" else "Editar producto") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 445.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Comercio: ${shop.name}", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(value = name, onValueChange = { name = it; validation = null }, label = { Text("Nombre del artículo") }, singleLine = true)
                Text("Categoría", style = MaterialTheme.typography.labelMedium)
                ChoiceMenu(category, categories, onChoose = { category = it; validation = null })
                OutlinedTextField(
                    value = price, onValueChange = { price = it; validation = null },
                    label = { Text("Precio en MXN") }, placeholder = { Text("Ejemplo: 49.50") },
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = stock, onValueChange = { stock = it; validation = null },
                    label = { Text("Unidades disponibles") }, placeholder = { Text("Ejemplo: 8") },
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                SmallError(validation)
            }
        },
        confirmButton = {
            Button(onClick = {
                val error = validateProduct(name, category, price, stock)
                if (error != null) validation = error
                else onSave(Product(
                    id = existing?.id ?: UUID.randomUUID().toString(),
                    shopId = shop.id,
                    name = name.trim(),
                    category = category,
                    price = price.trim().replace(',', '.').toDouble(),
                    stock = stock.trim().toInt(),
                    updatedAt = System.currentTimeMillis()
                ))
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun ProductDetailsDialog(product: Product, shop: Shop, onDismiss: () -> Unit, onNotice: (String) -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(product.name) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(money(product.price), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(if (product.stock == 0) "Sin existencias reportadas" else "${product.stock} unidades reportadas")
                Text(lastUpdate(product.updatedAt), style = MaterialTheme.typography.bodySmall)
                HorizontalDivider()
                Text(shop.name, fontWeight = FontWeight.SemiBold)
                Text("${shop.neighborhood}\n${shop.address}\n${shop.openingHours}")
                if (shop.id.startsWith("demo-")) {
                    Text("Comercio ficticio: el mapa únicamente mostrará la zona, no un local real.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error)
                } else {
                    Text("Antes de trasladarte, confirma la disponibilidad con el comercio.", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = { openMap(context, shop, onNotice) }) {
                Text(if (shop.id.startsWith("demo-")) "Ver zona en Maps" else "Abrir dirección")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}

@Composable
fun ShopDetailsDialog(shop: Shop, products: List<Product>, onDismiss: () -> Unit, onNotice: (String) -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(shop.name) },
        text = {
            Column(
                Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Colonia: ${shop.neighborhood}")
                Text("Dirección: ${shop.address}")
                Text("Horario: ${shop.openingHours}")
                if (shop.id.startsWith("demo-")) {
                    Text("Este comercio es ficticio y su dirección es referencial.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error)
                }
                HorizontalDivider()
                Text("Productos (${products.size})", fontWeight = FontWeight.SemiBold)
                if (products.isEmpty()) Text("Este comercio no tiene productos registrados.")
                products.sortedBy { it.name }.forEach {
                    Text("• ${it.name}: ${money(it.price)} · ${it.stock} unidades", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = { openMap(context, shop, onNotice) }) {
                Text(if (shop.id.startsWith("demo-")) "Ver zona en Maps" else "Abrir dirección")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}

private fun openMap(context: Context, shop: Shop, onNotice: (String) -> Unit) {
    // Abre una búsqueda en Maps. No simula coordenadas ni pines de locales ficticios.
    val uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(shop.address))
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (_: ActivityNotFoundException) {
        onNotice("No se encontró una aplicación para abrir el mapa.")
    } catch (_: SecurityException) {
        onNotice("No se pudo abrir el mapa en este dispositivo.")
    }
}
