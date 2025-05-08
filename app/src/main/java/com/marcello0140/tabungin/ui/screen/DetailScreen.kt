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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
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
import com.marcello0140.tabungin.ui.components.DialogDeleteWishlist
import com.marcello0140.tabungin.ui.components.DialogEditWishlist
import com.marcello0140.tabungin.ui.components.DialogRiwayat
import com.marcello0140.tabungin.ui.components.DialogTambahRiwayat
import com.marcello0140.tabungin.ui.components.formatDateToReadable
import com.marcello0140.tabungin.ui.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onNavigateBack: () -> Unit
) {
    val wishList = viewModel.wishList

    // STATE UNTUK MENGELOLA DIALOG YANG AKTIF
    var showDetailDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showEditWishlistDialog by remember { mutableStateOf(false) }
    var showDeleteWishlistDialog by remember { mutableStateOf(false) }
    var selectedHistoryItem by remember { mutableStateOf<TabunganHistory?>(null) }

    LaunchedEffect(showDetailDialog) {
        println("Show Detail Dialog: $showDetailDialog, Selected Item: $selectedHistoryItem")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(wishList?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (wishList != null) {
                        IconButton(onClick = { showEditWishlistDialog = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit Wishlist")
                        }
                        IconButton(onClick = { showDeleteWishlistDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Wishlist")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (wishList != null) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Tambah Catatan")
                }
            }
        },
        content = { innerPadding ->
            if (wishList == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                DetailScreenContent(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp),
                    wishList = wishList,
                    onHistoryItemClick = { item ->
                        selectedHistoryItem = item
                        showDetailDialog = true
                    }
                )
            }
        }
    )

    if (wishList != null) {
        if (showEditWishlistDialog) {
            DialogEditWishlist(
                initialName = wishList.name,
                initialTargetAmount = wishList.targetAmount.toString(),
                onDismiss = { showEditWishlistDialog = false },
                onConfirm = { newName, newTarget ->
                    viewModel.updateWishlist(newName, newTarget)
                    showEditWishlistDialog = false
                }
            )
        }

        if (showDeleteWishlistDialog) {
            DialogDeleteWishlist(
                onDismiss = { showDeleteWishlistDialog = false },
                onConfirm = {
                    viewModel.deleteWishlist(wishList.id)
                    onNavigateBack()
                    showDeleteWishlistDialog = false
                }
            )
        }

        if (showAddDialog) {
            DialogTambahRiwayat(
                onDismiss = { showAddDialog = false },
                onConfirm = { nominal, isPenambahan ->
                    viewModel.addHistoryItem(nominal, isPenambahan)
                    showAddDialog = false
                },
                currentAmount = wishList.currentAmount
            )
        }

        if (showDetailDialog && selectedHistoryItem != null) {
            DialogRiwayat(
                historyItem = selectedHistoryItem!!,
                onDismiss = { showDetailDialog = false },
                onEdit = {
                    showDetailDialog = false
                    showEditDialog = true
                },
                onDelete = { id ->
                    viewModel.deleteHistoryItem(id)
                    showDetailDialog = false
                }
            )
        }

        if (showEditDialog && selectedHistoryItem != null) {
            DialogTambahRiwayat(
                initialNominal = selectedHistoryItem!!.nominal.toString(),
                initialIsPenambahan = selectedHistoryItem!!.isPenambahan,
                onDismiss = { showEditDialog = false },
                onConfirm = { nominal, isPenambahan ->
                    viewModel.editHistoryItem(selectedHistoryItem!!.id, nominal, isPenambahan)
                    showEditDialog = false
                },
                currentAmount = selectedHistoryItem!!.nominal
            )
        }
    }
}

@Composable
fun DetailScreenContent(
    modifier: Modifier = Modifier,
    wishList: WishList,
    onHistoryItemClick: (TabunganHistory) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("📦", style = MaterialTheme.typography.displayMedium)
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Target: ${formatRupiah(wishList.targetAmount)}")
                    val progress =
                        (wishList.currentAmount.toFloat() / wishList.targetAmount).coerceIn(0f, 1f)
                    Text("Progress: ${(progress * 100).toInt()}%")
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Tanggal Dibuat: ${wishList.createdAt}")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Terkumpul")
                            Text(formatRupiah(wishList.currentAmount), fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Kekurangan")
                            Text(
                                formatRupiah(wishList.targetAmount - wishList.currentAmount),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (wishList.history.isNotEmpty()) {
                        Text("Riwayat Tabungan")
                        Spacer(Modifier.padding(8.dp))
                        wishList.history.forEachIndexed { index, item ->
                            HistoryItem(
                                historyItem = item,
                                onClick = { onHistoryItemClick(item) }
                            )
                            if (index != wishList.history.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Belum ada riwayat tabungan",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(historyItem: TabunganHistory, onClick: () -> Unit) {
    val color = if (historyItem.isPenambahan) Color(0xFF2E7D32) else Color(0xFFC62828)
    val sign = if (historyItem.isPenambahan) "+" else "−"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = formatDateToReadable(historyItem.tanggal),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (historyItem.isPenambahan) "Penambahan" else "Pengurangan",
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }

            Text(
                text = "$sign ${formatRupiah(historyItem.nominal)}",
                color = color,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Divider tipis di bawah setiap item
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            thickness = 0.8.dp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

fun formatRupiah(amount: Int): String {
    return "Rp %,d".format(amount).replace(',', '.')
}



@Preview(showBackground = true)
@Composable
fun PreviewDetailScreen() {
    // Dummy ViewModel atau hardcode data manual
    val dummyWishList = WishList(
        id = 1,
        name = "Beli iPhone 15",
        targetAmount = 20000000,
        currentAmount = 5000000,
        createdAt = "01 Jan 2025",
        history = listOf(
            TabunganHistory(1, "02 Jan 2025", 2000000, true),
            TabunganHistory(2, "15 Jan 2025", 1000000, true),
            TabunganHistory(3, "01 Feb 2025", 1000000, false)
        )
    )
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah")
            }
        },
        topBar = { /* optional */ },
        content = { innerPadding ->
            // Panggil konten utama dummy di sini
            DetailScreenContent(
                wishList = dummyWishList,
                onHistoryItemClick = { println("Clicked history item: ${it.id}") }
            )
            Column(modifier = Modifier.padding(innerPadding)) {
                Text("Preview Content")
            }
        }
    )
}