package com.marcello0140.tabungin.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marcello0140.tabungin.model.TabunganHistory
import com.marcello0140.tabungin.model.WishList
import com.marcello0140.tabungin.ui.components.DialogTambahCatatan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    wishList: WishList,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddNote: (nominal: Int, isPenambahan: Boolean) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedHistoryItem by remember { mutableStateOf<TabunganHistory?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(wishList.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Catatan")
            }
        },
        content = { innerPadding ->
            DetailScreenContent(Modifier.padding(innerPadding).padding(16.dp), wishList)
        }
    )

    if (showDialog) {
        DialogTambahCatatan(
            onDismiss = { showDialog = false },
            onConfirm = { nominal, isPenambahan ->
                onAddNote(nominal, isPenambahan)
                showDialog = false
            },
            currentAmount = 700000
        )
    }
}

@Composable
fun DetailScreenContent(
    modifier: Modifier = Modifier,
    wishList: WishList
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {

        // Gambar Placeholder
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("📦", style = MaterialTheme.typography.displayMedium)
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Card Progress
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Target: ${formatRupiah(wishList.targetAmount)}", style = MaterialTheme.typography.titleLarge)
                    val progress = (wishList.currentAmount.toFloat() / wishList.targetAmount).coerceIn(0f, 1f)
                    Text("Progress: ${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.Gray)
                    Text("Tanggal Dibuat: ${wishList.createdAt}", modifier = Modifier.padding(top = 8.dp))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Informasi Terkumpul, Kekurangan, dan Riwayat Tabungan
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Baris Terkumpul dan Kekurangan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Terkumpul", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                formatRupiah(wishList.currentAmount),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Kekurangan", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                formatRupiah(wishList.targetAmount - wishList.currentAmount),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Menampilkan Riwayat Tabungan jika ada
                    if (wishList.history.isNotEmpty()) {
                        Text(
                            "Riwayat Tabungan",
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    wishList.history.forEachIndexed { index, item ->
                        CatatanItem(historyItem = item)

                        if (index != wishList.history.lastIndex) {
                            HorizontalDivider(
                                color = Color.LightGray,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CatatanItem(historyItem: TabunganHistory) {
    val color = if (historyItem.isPenambahan) Color(0xFF2E7D32) else Color.Red
    val sign = if (historyItem.isPenambahan) "+" else "-"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Detail riwayat */ }
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(26.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = historyItem.tanggal, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "$sign ${formatRupiah(historyItem.nominal)}",
            color = color,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

fun formatRupiah(amount: Int): String {
    return "Rp %,d".format(amount).replace(',', '.')
}

@Preview(showBackground = true)
@Composable
fun PreviewDetailScreen() {
    val dummyWishList = WishList(
        id = 1,
        name = "Beli iPhone 15",
        targetAmount = 20000000,
        currentAmount = 5000000,
        createdAt = "01 Jan 2025",
        history = listOf(
            TabunganHistory("02 Jan 2025", 2000000, true),
            TabunganHistory("15 Jan 2025", 1000000, true),
            TabunganHistory("01 Feb 2025", 1000000, false)
        )
    )
    DetailScreen(
        wishList = dummyWishList,
        onBackClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onAddNote = { nominal, isPenambahan ->
            // Handle Add Note action here, with nominal and isPenambahan
            println("Nominal: $nominal, Penambahan: $isPenambahan")
        }
    )
}
