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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.marcello0140.tabungin.model.TabunganHistory
import com.marcello0140.tabungin.model.WishListWithHistory
import com.marcello0140.tabungin.ui.components.DialogDeleteWishlist
import com.marcello0140.tabungin.ui.components.DialogEditWishlist
import com.marcello0140.tabungin.ui.components.DialogRiwayat
import com.marcello0140.tabungin.ui.components.DialogTambahRiwayat
import com.marcello0140.tabungin.ui.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onNavigateBack: () -> Unit
) {
    val data by viewModel.wishListWithHistory.collectAsState()
    val wishList = data?.wishList

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditWishlistDialog by remember { mutableStateOf(false) }
    var showDeleteWishlistDialog by remember { mutableStateOf(false) }
    var selectedHistoryItem by remember { mutableStateOf<TabunganHistory?>(null) }
    var showEditHistoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(wishList?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
        }
    ) { innerPadding ->
        if (wishList == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            DetailScreenContent(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                wishList = data!!,
                onHistoryItemClick = { selectedHistoryItem = it }
            )
        }
    }

    if (wishList != null) {
        if (showEditWishlistDialog) {
            DialogEditWishlist(
                initialName = wishList.name,
                initialTargetAmount = wishList.targetAmount.toString(),
                onDismiss = { showEditWishlistDialog = false },
                onConfirm = { newName, newTarget ->
                    viewModel.updateWishlist(wishList.copy(name = newName, targetAmount = newTarget))
                    showEditWishlistDialog = false
                }
            )
        }

        if (showDeleteWishlistDialog) {
            DialogDeleteWishlist(
                onDismiss = { showDeleteWishlistDialog = false },
                onConfirm = {
                    viewModel.deleteWishlist(wishList)
                    onNavigateBack()
                    showDeleteWishlistDialog = false
                }
            )
        }

        if (showAddDialog) {
            DialogTambahRiwayat(
                onDismiss = { showAddDialog = false },
                onConfirm = { nominal, isPenambahan ->
                    viewModel.addHistoryItem(wishList.id, nominal, isPenambahan)
                    showAddDialog = false
                },
                currentAmount = wishList.currentAmount
            )
        }
    }

    selectedHistoryItem?.let { history ->
        DialogRiwayat(
            historyItem = history,
            onDismiss = { selectedHistoryItem = null },
            onEdit = { showEditHistoryDialog = true },
            onDelete = {
                viewModel.deleteHistoryItem(history)
                selectedHistoryItem = null
            }
        )
    }

    if (showEditHistoryDialog && selectedHistoryItem != null) {
        DialogTambahRiwayat(
            initialNominal = selectedHistoryItem!!.nominal.toString(),
            initialIsPenambahan = selectedHistoryItem!!.isPenambahan,
            onDismiss = { showEditHistoryDialog = false },
            onConfirm = { nominal, isPenambahan ->
                viewModel.editHistoryItem(selectedHistoryItem!!, nominal, isPenambahan)
                showEditHistoryDialog = false
            },
            currentAmount = selectedHistoryItem!!.nominal
        )
    }
}

@Composable
fun DetailScreenContent(
    modifier: Modifier = Modifier,
    wishList: WishListWithHistory,
    onHistoryItemClick: (TabunganHistory) -> Unit
) {
    val wish = wishList.wishList
    val histories = wishList.histories

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("📦", style = MaterialTheme.typography.displayMedium)
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val progress = (wish.currentAmount.toFloat() / wish.targetAmount).coerceIn(0f, 1f)
                    Text("Target: ${formatRupiah(wish.targetAmount)}")
                    Text("Progress: ${(progress * 100).toInt()}%")
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Tanggal Dibuat: ${wish.createdAt}")
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
                        Column { Text("Terkumpul"); Text(formatRupiah(wish.currentAmount), fontWeight = FontWeight.Bold) }
                        Column(horizontalAlignment = Alignment.End) { Text("Kekurangan"); Text(formatRupiah(wish.targetAmount - wish.currentAmount), fontWeight = FontWeight.Bold) }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (histories.isNotEmpty()) {
                        Text("Riwayat Tabungan")
                        Spacer(modifier = Modifier.height(8.dp))
                        histories.forEachIndexed { index, item ->
                            HistoryItem(historyItem = item, onClick = { onHistoryItemClick(item) })
                            if (index != histories.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Belum ada riwayat tabungan", style = MaterialTheme.typography.bodySmall)
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(historyItem.tanggal, style = MaterialTheme.typography.bodySmall)
                Text(
                    if (historyItem.isPenambahan) "Penambahan" else "Pengurangan",
                    color = color,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Text(
                "$sign ${formatRupiah(historyItem.nominal)}",
                color = color,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
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
