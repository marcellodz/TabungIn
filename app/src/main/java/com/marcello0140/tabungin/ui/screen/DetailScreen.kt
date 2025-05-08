package com.marcello0140.tabungin.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.marcello0140.tabungin.model.WishList

@Composable
fun DetailScreen(id: Int, navController: NavHostController) {
    // Dummy list yang sama seperti di MainScreen
    val wishListDummy = listOf(
        WishList(id = 1, name = "Marcell", targetAmount = 3_000_000, currentAmount = 3_000_000),
        WishList(id = 2, name = "Marcell", targetAmount = 3_000_000, currentAmount = 30_000)
    )

    // Ambil item berdasarkan ID
    val item = wishListDummy.find { it.id == id }

    if (item != null) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Detail Wishlist", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Nama: ${item.name}")
            Text(text = "Target: Rp ${item.targetAmount}")
            Text(text = "Terkumpul: Rp ${item.currentAmount}")
            val percentage = (item.currentAmount.toFloat() / item.targetAmount * 100).toInt()
            Text(text = "Progress: $percentage%")
        }
    } else {
        // Jika ID tidak ditemukan
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Data tidak ditemukan.")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewDetailScreen() {
    DetailScreen(id = 1, rememberNavController())
}