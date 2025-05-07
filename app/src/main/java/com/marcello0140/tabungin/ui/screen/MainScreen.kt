package com.marcello0140.tabungin.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marcello0140.tabungin.model.WishList
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TabungIn") },
            )
        }
    ) { innerPadding ->
        MainScreenContent(Modifier.padding(innerPadding))
    }
}

@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier
) {
    // Data Dummy
    val wishListDummy = listOf(
        WishList( id = 1, name = "Marcell", targetAmount = 3_000_000, currentAmount = 3_000_000)
    )

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

        Spacer(modifier = Modifier.height(16.dp))

        val filteredList = if (selectedTabIndex == 0) {
            wishListDummy.filter { it.currentAmount < it.targetAmount }
        } else {
            wishListDummy.filter { it.currentAmount >= it.targetAmount }
        }

        if (filteredList.isEmpty()) {
            // 🌟 Tampilan kosong jika tidak ada data
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.SentimentDissatisfied,
                    contentDescription = "Empty",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tidak ada data untuk ditampilkan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredList.size) { index ->
                    WishListItem(filteredList[index])
                }
            }
            }
    }
}

@Composable
fun WishListItem(item: WishList) {
    val progress = item.currentAmount.toFloat() / item.targetAmount
    val percentage = (progress * 100).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
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
    MainScreen()
}
