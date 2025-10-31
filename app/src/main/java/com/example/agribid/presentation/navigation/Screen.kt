package com.example.agribid.presentation.navigation // <-- FIX: Package name corrected

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.ui.graphics.vector.ImageVector

// Type-safe navigation routes
sealed class Screen(val route: String) {
    object ProductList : Screen("product_list")
    object BidCreation : Screen("bid_creation/{productId}") {
        fun createRoute(productId: String) = "bid_creation/$productId"
    }
    object HarvestForecast : Screen("harvest_forecast")
    object Settings : Screen("settings")
    object About : Screen("about")

    // For the main app sections
    object BuyerNav : Screen("buyer_nav")
    object FarmerNav : Screen("farmer_nav")
    object SettingsNav : Screen("settings_nav")
}

// Data class for Bottom Bar items
data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.BuyerNav.route,
        title = "Browse",
        selectedIcon = Icons.Filled.ShoppingBasket,
        unselectedIcon = Icons.Outlined.ShoppingBasket
    ),
    BottomNavItem(
        route = Screen.FarmerNav.route,
        title = "My Farm",
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Outlined.List
    ),
    BottomNavItem(
        route = Screen.SettingsNav.route,
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
)
