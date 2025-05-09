package com.marcello0140.tabungin.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.marcello0140.tabungin.model.WishList
import com.marcello0140.tabungin.model.WishListWithHistory
import com.marcello0140.tabungin.navigation.Screen
import com.marcello0140.tabungin.ui.components.DialogTambahWishlist
import com.marcello0140.tabungin.ui.viewmodel.MainViewModel
import com.marcello0140.tabungin.ui.viewmodel.ThemeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    themeViewModel: ThemeViewModel
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState(initial = isSystemInDarkTheme())
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TabungIn") },
                actions = {
                    IconButton(onClick = { themeViewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.NightsStay,
                            contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Tabungan")
            }
        }
    ) { innerPadding ->
        MainScreenContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            wishListWithHistory = viewModel.wishListWithHistory.collectAsState().value
        )
    }

    if (showAddDialog) {
        DialogTambahWishlist(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, targetAmount ->
                viewModel.viewModelScope.launch {
                    viewModel.addWishlist(name, targetAmount)
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    wishListWithHistory: List<WishListWithHistory>
) {
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

        Spacer(modifier = Modifier.height(12.dp)) // Spacer tambahan agar tidak mepet

        val filteredList = if (selectedTabIndex == 0) {
            wishListWithHistory.filter {
                val totalAmount = it.histories.sumOf { h -> if (h.isPenambahan) h.nominal else -h.nominal }
                totalAmount < it.wishList.targetAmount
            }
        } else {
            wishListWithHistory.filter {
                val totalAmount = it.histories.sumOf { h -> if (h.isPenambahan) h.nominal else -h.nominal }
                totalAmount >= it.wishList.targetAmount
            }
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.SentimentDissatisfied,
                        contentDescription = "Empty",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Belum ada wishlist", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList.size) { index ->
                    val item = filteredList[index]
                    val calculatedAmount = item.histories.sumOf { h -> if (h.isPenambahan) h.nominal else -h.nominal }
                    WishListItem(
                        item = item.wishList.copy(currentAmount = calculatedAmount),
                        onClick = {
                            navController.navigate(Screen.Detail.navigationWithId(item.wishList.id.toInt()))
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
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
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(item.name, style = MaterialTheme.typography.bodyLarge)
                Text("Rp ${item.targetAmount}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
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
    val dummyWishLists = listOf(
        WishListWithHistory(
            wishList = WishList(
                id = 1L,
                name = "Dummy Wish",
                targetAmount = 1000000,
                currentAmount = 500000,
                createdAt = "2025-05-10"
            ),
            histories = listOf()
        )
    )

    MainScreenContent(
        navController = rememberNavController(),
        wishListWithHistory = dummyWishLists
    )
}
