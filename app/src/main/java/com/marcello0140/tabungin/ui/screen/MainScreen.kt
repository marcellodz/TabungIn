package com.marcello0140.tabungin.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.model.WishList
import com.marcello0140.tabungin.navigation.Screen
import com.marcello0140.tabungin.util.ViewModelFactory
import com.marcello0140.tabungin.viewmodel.MainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TabungIn") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO: Aksi saat FAB diklik
                },
                modifier = Modifier.padding(16.dp) // Memberikan jarak agar tidak terlalu menempel ke pinggir layar
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Tambah Tabungan",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        MainScreenContent(Modifier.padding(innerPadding), navController)
    }
}

@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val context = LocalContext.current
    val repository = WishListRepository()
    val viewModel: MainViewModel = viewModel(factory = ViewModelFactory(repository))
    val wishList by viewModel.wishList.collectAsState()



    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Belum Tercapai", "Tercapai")

    Column(modifier = modifier.padding(16.dp)) {

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        val filteredList = if (selectedTabIndex == 0) {
            wishList.filter { it.currentAmount < it.targetAmount }
        } else {
            wishList.filter { it.currentAmount >= it.targetAmount }
        }

        if (filteredList.isEmpty()) {
            // 🌟 Tampilan kosong jika tidak ada data
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 160.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SentimentDissatisfied,
                        contentDescription = "Empty",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Belum ada wishlist",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList.size) { index ->
                    WishListItem(filteredList[index], onClick = {
                        // Navigasi ke DetailScreen dengan id
                        navController.navigate(Screen.Detail.navigationWithId(filteredList[index].id))
                    })
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun WishListItem(item: WishList, onClick: () -> Unit) {
    val progress = item.currentAmount.toFloat() / item.targetAmount
    val percentage = (progress * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clickable(onClick = onClick), // Tambah aksi onClick
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(item.name, style = MaterialTheme.typography.bodyLarge)
                Text("Rp ${item.targetAmount}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "$percentage% terkumpul (Rp ${item.currentAmount})",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen(navController = rememberNavController())
}
