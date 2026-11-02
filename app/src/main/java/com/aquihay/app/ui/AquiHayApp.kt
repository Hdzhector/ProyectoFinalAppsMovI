package com.aquihay.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aquihay.app.data.InventoryRepository
import com.aquihay.app.data.demoInventory
import com.aquihay.app.model.Inventory
import com.aquihay.app.model.PriceSort
import com.aquihay.app.model.Product
import com.aquihay.app.model.Shop
import kotlinx.coroutines.launch

enum class AppTab { SEARCH, SHOPS, INVENTORY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AquiHayApp(repository: InventoryRepository) {
    val initial = remember { repository.load() }
    val shops = remember { mutableStateListOf<Shop>().apply { addAll(initial.shops) } }
    val products = remember { mutableStateListOf<Product>().apply { addAll(initial.products) } }
    var tab by rememberSaveable { mutableStateOf(AppTab.SEARCH) }
    var searchText by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedNeighborhood by rememberSaveable { mutableStateOf<String?>(null) }
    var onlyAvailable by rememberSaveable { mutableStateOf(false) }
    var sort by rememberSaveable { mutableStateOf(PriceSort.NAME) }
    var selectedProductId by remember { mutableStateOf<String?>(null) }
    var selectedShopId by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    fun persist() { repository.save(Inventory(shops.toList(), products.toList())) }
    fun notice(text: String) { scope.launch { snackbar.showSnackbar(text) } }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text("AquíHay", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        Text("Encuentra cerca, sin ir en vano", style = MaterialTheme.typography.labelSmall)
                    }
                },
                actions = { AssistChip(onClick = { tab = AppTab.INVENTORY }, label = { Text("Editar datos") }) }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == AppTab.SEARCH,
                    onClick = { tab = AppTab.SEARCH },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Buscar") }
                )
                NavigationBarItem(
                    selected = tab == AppTab.SHOPS,
                    onClick = { tab = AppTab.SHOPS },
                    icon = { Icon(Icons.Default.Store, contentDescription = null) },
                    label = { Text("Comercios") }
                )
                NavigationBarItem(
                    selected = tab == AppTab.INVENTORY,
                    onClick = { tab = AppTab.INVENTORY },
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = null) },
                    label = { Text("Inventario") }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbar) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                AppTab.SEARCH -> SearchScreen(
                    shops = shops,
                    products = products,
                    search = searchText,
                    onSearchChange = { searchText = it },
                    category = selectedCategory,
                    onCategoryChange = { selectedCategory = it },
                    neighborhood = selectedNeighborhood,
                    onNeighborhoodChange = { selectedNeighborhood = it },
                    onlyAvailable = onlyAvailable,
                    onAvailabilityChange = { onlyAvailable = it },
                    sort = sort,
                    onSortChange = { sort = it },
                    onSelectProduct = { selectedProductId = it }
                )
                AppTab.SHOPS -> ShopsScreen(shops, products, onSelectShop = { selectedShopId = it })
                AppTab.INVENTORY -> InventoryScreen(
                    shops = shops,
                    products = products,
                    onSaveShop = { shop ->
                        val existingIndex = shops.indexOfFirst { it.id == shop.id }
                        if (existingIndex == -1) shops.add(shop) else shops[existingIndex] = shop
                        persist()
                        notice("Comercio guardado en este dispositivo")
                    },
                    onDeleteShop = { shopId ->
                        shops.removeAll { it.id == shopId }
                        products.removeAll { it.shopId == shopId }
                        persist()
                        notice("Comercio y sus productos eliminados")
                    },
                    onSaveProduct = { product ->
                        val existingIndex = products.indexOfFirst { it.id == product.id }
                        if (existingIndex == -1) products.add(product) else products[existingIndex] = product
                        persist()
                        notice("Producto guardado correctamente")
                    },
                    onDeleteProduct = { productId ->
                        products.removeAll { it.id == productId }
                        persist()
                        notice("Producto eliminado")
                    },
                    onResetDemo = {
                        val sample = demoInventory()
                        shops.replaceWith(sample.shops)
                        products.replaceWith(sample.products)
                        persist()
                        searchText = ""
                        selectedCategory = null
                        selectedNeighborhood = null
                        onlyAvailable = false
                        notice("Datos de demostración restaurados")
                    }
                )
            }
        }
    }

    val selectedProduct = products.find { it.id == selectedProductId }
    val selectedProductShop = shops.find { it.id == selectedProduct?.shopId }
    if (selectedProduct != null && selectedProductShop != null) {
        ProductDetailsDialog(
            product = selectedProduct,
            shop = selectedProductShop,
            onDismiss = { selectedProductId = null },
            onNotice = ::notice
        )
    }
    val selectedShop = shops.find { it.id == selectedShopId }
    if (selectedShop != null) {
        ShopDetailsDialog(
            shop = selectedShop,
            products = products.filter { it.shopId == selectedShop.id },
            onDismiss = { selectedShopId = null },
            onNotice = ::notice
        )
    }
}

private fun <T> SnapshotStateList<T>.replaceWith(items: List<T>) {
    clear()
    addAll(items)
}
