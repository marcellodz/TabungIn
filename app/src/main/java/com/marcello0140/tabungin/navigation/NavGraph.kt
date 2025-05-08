package com.marcello0140.tabungin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.ui.screen.DetailScreen
import com.marcello0140.tabungin.ui.screen.MainScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    val repository = WishListRepository()

    NavHost(navController = navController, startDestination = Screen.Main.route) {

        composable(Screen.Main.route) {
            MainScreen(navController)
        }

        // Navigasi ke DetailScreen dengan id sebagai argumen
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable

            // Mengambil data dari repository menggunakan id
            val wishList = repository.getAllWishList().collectAsState(initial = emptyList()).value
            val selectedWishList = wishList.find { it.id == id }

            // Jika wishList ditemukan, tampilkan DetailScreen
            selectedWishList?.let {
                DetailScreen(
                    wishList = it,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { /* Handle edit */ },
                    onDeleteClick = { /* Handle delete */ },
                    onAddNote = { nominal, isPenambahan ->
                        // Handle the Add Note action with nominal and isPenambahan
                        println("Nominal: $nominal, Penambahan: $isPenambahan")
                    }
                )
            }
        }
    }
}
