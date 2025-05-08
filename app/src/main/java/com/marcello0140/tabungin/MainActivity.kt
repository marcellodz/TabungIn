package com.marcello0140.tabungin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.database.DatabaseInstance
import com.marcello0140.tabungin.navigation.NavGraph
import com.marcello0140.tabungin.ui.theme.TabungInTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TabungInTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val db = DatabaseInstance.getDatabase(this)
                    val repository = WishListRepository(
                        wishListDao = db.wishListDao(),
                        historyDao = db.historyDao()
                    )
                    val navController = rememberNavController()
                    NavGraph(navController = navController, repository = repository)
                }
            }
        }
    }
}
