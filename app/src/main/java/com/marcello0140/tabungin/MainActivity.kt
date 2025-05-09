package com.marcello0140.tabungin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.database.DatabaseInstance
import com.marcello0140.tabungin.datastore.PreferenceManager
import com.marcello0140.tabungin.navigation.NavGraph
import com.marcello0140.tabungin.ui.theme.TabungInTheme
import com.marcello0140.tabungin.ui.viewmodel.ThemeViewModel
import com.marcello0140.tabungin.util.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = DatabaseInstance.getDatabase(applicationContext)
        val repository = WishListRepository(database.wishListDao(), database.historyDao())
        val preferenceManager = PreferenceManager(applicationContext)
        val factory = ViewModelFactory(repository, preferenceManager)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel(factory = factory)
            val isDarkMode by themeViewModel.isDarkMode.collectAsState(initial = isSystemInDarkTheme())

            TabungInTheme(darkTheme = isDarkMode) {
                Surface {
                    val navController = rememberNavController()
                    NavGraph(navController = navController, repository = repository, preferenceManager)
                }
            }
        }
    }
}
