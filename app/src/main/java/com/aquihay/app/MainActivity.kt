package com.aquihay.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aquihay.app.data.InventoryRepository
import com.aquihay.app.ui.AquiHayApp
import com.aquihay.app.ui.AquiHayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = InventoryRepository(applicationContext)
        setContent {
            AquiHayTheme {
                AquiHayApp(repository)
            }
        }
    }
}
