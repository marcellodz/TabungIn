package com.marcello0140.tabungin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.ui.screen.DetailScreen
import com.marcello0140.tabungin.ui.screen.MainScreen
import com.marcello0140.tabungin.ui.viewmodel.DetailViewModel
import com.marcello0140.tabungin.ui.viewmodel.MainViewModel
import com.marcello0140.tabungin.util.ViewModelFactory

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: WishListRepository // DI-berikan dari MainActivity
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        // Main Screen
        composable(Screen.Main.route) {
            val mainViewModel: MainViewModel = viewModel(factory = ViewModelFactory(repository))
            MainScreen(
                navController = navController,
                viewModel = mainViewModel
            )
        }

        // Detail Screen
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable

            val detailViewModel: DetailViewModel = viewModel(factory = ViewModelFactory(repository))

            // Load data saat ID diterima
            LaunchedEffect(id) {
                detailViewModel.loadWishListById(id)
            }

            DetailScreen(
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
