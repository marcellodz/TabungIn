package com.marcello0140.tabungin.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marcello0140.tabungin.model.TabunganHistory
import com.marcello0140.tabungin.model.WishList
import com.marcello0140.tabungin.ui.components.*
import com.marcello0140.tabungin.ui.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val data by viewModel.wishListWithHistory.collectAsState()
    val wishList = data?.wishList
    val histories = data?.histories ?: emptyList()

    // Hitung ulang currentAmount secara dinamis
    val calculatedCurrentAmount = histories.filter { it.isPenambahan }.sumOf { it.nominal } -
            histories.filter { !it.isPenambahan }.sumOf { it.nominal }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditWishlistDialog by remember { mutableStateOf(false) }
    var showDeleteWishlistDialog by remember { mutableStateOf(false) }
    var selectedHistoryItem by remember { mutableStateOf<TabunganHistory?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }
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
                modifier = Modifier.padding(innerPadding).padding(16.dp),
                wishList = wishList.copy(currentAmount = calculatedCurrentAmount),
                histories = histories,
                onHistoryItemClick = {
                    selectedHistoryItem = it
                    showDetailDialog = true
                }
            )
        }
    }

    // Dialog Edit Wishlist
    if (showEditWishlistDialog && wishList != null) {
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

    // Dialog Delete Wishlist
    if (showDeleteWishlistDialog && wishList != null) {
        DialogDeleteWishlist(
            onDismiss = { showDeleteWishlistDialog = false },
            onConfirm = {
                viewModel.deleteWishlist(wishList)
                onNavigateBack()
                showDeleteWishlistDialog = false
            }
        )
    }

    // Dialog Tambah Riwayat
    if (showAddDialog && wishList != null) {
        DialogTambahRiwayat(
            onDismiss = { showAddDialog = false },
            onConfirm = { nominal, isPenambahan ->
                if (!isPenambahan && nominal > calculatedCurrentAmount) {
                    Toast.makeText(context, "Pengurangan melebihi saldo!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.addHistoryItem(wishList.id, nominal, isPenambahan)
                    showAddDialog = false
                }
            },
            currentAmount = calculatedCurrentAmount
        )
    }

    // Dialog Detail Riwayat
    if (showDetailDialog && selectedHistoryItem != null) {
        DialogRiwayat(
            historyItem = selectedHistoryItem!!,
            onDismiss = { showDetailDialog = false },
            onEdit = {
                showDetailDialog = false
                showEditHistoryDialog = true
            },
            onDelete = {
                viewModel.deleteHistoryItem(selectedHistoryItem!!)
                selectedHistoryItem = null
                showDetailDialog = false
            }
        )
    }

    // Dialog Edit Riwayat
    if (showEditHistoryDialog && selectedHistoryItem != null) {
        DialogTambahRiwayat(
            initialNominal = selectedHistoryItem!!.nominal.toString(),
            initialIsPenambahan = selectedHistoryItem!!.isPenambahan,
            onDismiss = { showEditHistoryDialog = false },
            onConfirm = { nominal, isPenambahan ->
                if (!isPenambahan && nominal > wishList!!.currentAmount) {
                    Toast.makeText(context, "Tidak bisa mengurangi lebih dari saldo!", Toast.LENGTH_SHORT).show()
                } else if (nominal <= 0) {
                    Toast.makeText(context, "Nominal harus lebih dari 0!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.editHistoryItem(selectedHistoryItem!!, nominal, isPenambahan)
                    showEditHistoryDialog = false
                    selectedHistoryItem = null
                }
            },
            currentAmount = wishList!!.currentAmount
        )
    }
}

@Composable
fun DetailScreenContent(
    modifier: Modifier = Modifier,
    wishList: WishList,
    histories: List<TabunganHistory>,
    onHistoryItemClick: (TabunganHistory) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("📦", style = MaterialTheme.typography.displayMedium)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val progress = (wishList.currentAmount.toFloat() / wishList.targetAmount).coerceIn(0f, 1f)
                    Text("Target: ${formatRupiah(wishList.targetAmount)}")
                    Text("Progress: ${(progress * 100).toInt()}%")
                    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Tanggal Dibuat: ${wishList.createdAt}")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column { Text("Terkumpul"); Text(formatRupiah(wishList.currentAmount), fontWeight = FontWeight.Bold) }
                        Column(horizontalAlignment = Alignment.End) { Text("Kekurangan"); Text(formatRupiah(wishList.targetAmount - wishList.currentAmount), fontWeight = FontWeight.Bold) }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

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
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
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

    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(historyItem.tanggal, style = MaterialTheme.typography.bodySmall)
            Text(if (historyItem.isPenambahan) "Penambahan" else "Pengurangan", color = color, style = MaterialTheme.typography.labelSmall)
        }
        Text("$sign ${formatRupiah(historyItem.nominal)}", color = color, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

fun formatRupiah(amount: Int): String {
    return "Rp %,d".format(amount).replace(',', '.')
}


@Preview(showBackground = true)
@Composable
fun PreviewDetailScreenContent() {
    val dummyWishList = WishList(
        id = 1L,
        name = "Dummy Wish",
        targetAmount = 1000000,
        currentAmount = 500000,
        createdAt = "2025-05-10"
    )

    val dummyHistories = listOf(
        TabunganHistory(1, 1, "2025-05-11", 100000, true),
        TabunganHistory(2, 1, "2025-05-12", 50000, false)
    )

    Surface {
        DetailScreenContent(
            wishList = dummyWishList,
            histories = dummyHistories,
            onHistoryItemClick = {}
        )
    }
}
