package com.example.agribid.presentation.navigation // <-- FIX: Package name corrected

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.agribid.presentation.buyer.BidCreationScreen
import com.example.agribid.presentation.buyer.BuyerViewModel
import com.example.agribid.presentation.buyer.ProductListScreen
import com.example.agribid.presentation.farmer.FarmerViewModel
import com.example.agribid.presentation.farmer.HarvestForecastScreen
import com.example.agribid.presentation.settings.AboutScreen
import com.example.agribid.presentation.settings.SettingsScreen
import com.example.agribid.presentation.settings.SettingsViewModel


@Composable
fun AppNavigation(
    navController: NavHostController,
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == item.route) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.BuyerNav.route, // Default screen
            modifier = Modifier.padding(innerPadding)
        ) {
            // --- Main Buyer Flow ---
            navigation(
                startDestination = Screen.ProductList.route,
                route = Screen.BuyerNav.route
            ) {
                composable(Screen.ProductList.route) {
                    val buyerViewModel = hiltViewModel<BuyerViewModel>()
                    ProductListScreen(
                        viewModel = buyerViewModel,
                        onProductClick = { product ->
                            navController.navigate(Screen.BidCreation.createRoute(product.productId))
                        }
                    )
                }
                composable(
                    route = Screen.BidCreation.route,
                    arguments = listOf(navArgument("productId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val buyerViewModel = hiltViewModel<BuyerViewModel>()
                    val productId = backStackEntry.arguments?.getString("productId")

                    // Load the product details when navigating to the screen
                    LaunchedEffect(productId) {
                        if (productId != null) {
                            buyerViewModel.selectListingById(productId)
                        }
                    }

                    BidCreationScreen(
                        viewModel = buyerViewModel,
                        onBidSubmitted = {
                            navController.popBackStack() // Go back to list
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            // --- Main Farmer Flow ---
            navigation(
                startDestination = Screen.HarvestForecast.route,
                route = Screen.FarmerNav.route
            ) {
                composable(Screen.HarvestForecast.route) {
                    val farmerViewModel = hiltViewModel<FarmerViewModel>()
                    HarvestForecastScreen(
                        viewModel = farmerViewModel,
                        onForecastSaved = {
                            // Show a snackbar or simple confirmation
                        }
                    )
                }
                // ... (Add other farmer screens here like "My Bids", "My Contracts")
            }

            // --- Settings Flow ---
            navigation(
                startDestination = Screen.Settings.route,
                route = Screen.SettingsNav.route
            ) {
                composable(Screen.Settings.route) {
                    val settingsViewModel = hiltViewModel<SettingsViewModel>()
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onAboutClick = {
                            navController.navigate(Screen.About.route)
                        },
                        onLogoutClick = {
                            // Handle logout
                        }
                    )
                }
                composable(Screen.About.route) {
                    AboutScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
