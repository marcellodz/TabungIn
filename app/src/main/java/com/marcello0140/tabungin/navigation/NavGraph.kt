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
import com.marcello0140.tabungin.util.ViewModelFactory

@Composable
fun NavGraph(
    navController: NavHostController
) {
    // Repository dummy; nanti bisa diganti RoomRepo jika sudah pakai database
    val repository = WishListRepository()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        // Screen utama (list semua wishlist)
        composable(Screen.Main.route) {
            MainScreen(navController)
        }

        // Screen detail (dengan argumen id)
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable

            // Ambil ViewModel khusus untuk detail
            val detailViewModel: DetailViewModel = viewModel(
                factory = ViewModelFactory(repository)
            )

            // Trigger load data saat id berubah
            LaunchedEffect(id) {
                detailViewModel.loadWishListById(id)
            }

            // Tampilkan DetailScreen + navigasi back
            DetailScreen(
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
