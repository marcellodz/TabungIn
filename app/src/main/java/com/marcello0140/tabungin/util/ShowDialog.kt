package com.marcello0140.tabungin.util

import androidx.compose.runtime.Composable
import com.marcello0140.tabungin.model.TabunganHistory
import com.marcello0140.tabungin.model.WishList
import com.marcello0140.tabungin.ui.components.DialogDeleteWishlist
import com.marcello0140.tabungin.ui.components.DialogEditWishlist
import com.marcello0140.tabungin.ui.components.DialogRiwayat
import com.marcello0140.tabungin.ui.components.DialogTambahRiwayat
import com.marcello0140.tabungin.ui.viewmodel.DetailViewModel

@Composable
fun ShowDialog(
    wishList: WishList,  // data wishlist utama
    viewModel: DetailViewModel,  // akses logika & repository
    showEditWishlistDialog: Boolean,  // flag buka dialog edit wishlist
    onDismissEditWishlist: () -> Unit,  // aksi tutup dialog edit wishlist
    showDeleteWishlistDialog: Boolean,  // flag buka dialog hapus wishlist
    onDismissDeleteWishlist: () -> Unit,  // aksi tutup dialog hapus wishlist
    showAddDialog: Boolean,  // flag buka dialog tambah riwayat
    onDismissAddDialog: () -> Unit,  // aksi tutup dialog tambah riwayat
    selectedHistoryItem: TabunganHistory?,  // item riwayat yang dipilih user
    showEditHistoryDialog: Boolean,  // flag buka dialog edit riwayat
    onDismissEditHistory: () -> Unit,  // aksi tutup dialog edit riwayat
    onNavigateBack: () -> Unit,  // aksi untuk kembali (popBackStack)
    updateSelectedHistoryItem: (TabunganHistory?) -> Unit  // fungsi update item riwayat terpilih
) {
    // === 1️⃣ Dialog Edit Wishlist (edit nama & target) ===
    if (showEditWishlistDialog) {
        DialogEditWishlist(
            initialName = wishList.name,
            initialTargetAmount = wishList.targetAmount.toString(),
            onDismiss = onDismissEditWishlist,
            onConfirm = { newName, newTarget ->
                viewModel.updateWishlist(wishList.copy(name = newName, targetAmount = newTarget))
                onDismissEditWishlist()
            }
        )
    }

    // === 2️⃣ Dialog Konfirmasi Hapus Wishlist ===
    if (showDeleteWishlistDialog) {
        DialogDeleteWishlist(
            onDismiss = onDismissDeleteWishlist,
            onConfirm = {
                viewModel.deleteWishlist(wishList)
                onNavigateBack()  // kembali ke screen sebelumnya
                onDismissDeleteWishlist()
            }
        )
    }

    // === 3️⃣ Dialog Tambah Riwayat Baru ===
    if (showAddDialog) {
        DialogTambahRiwayat(
            onDismiss = onDismissAddDialog,
            onConfirm = { nominal, isPenambahan ->
                viewModel.addHistoryItem(wishList.id, nominal, isPenambahan)
                onDismissAddDialog()
            },
            currentAmount = wishList.currentAmount  // kirim saldo terkini untuk validasi
        )
    }

    // === 4️⃣ Dialog Detail Riwayat (klik item → lihat detail & tombol edit/hapus) ===
    if (selectedHistoryItem != null && !showEditHistoryDialog) {
        DialogRiwayat(
            historyItem = selectedHistoryItem,
            onDismiss = { updateSelectedHistoryItem(null) },  // tutup dialog & reset selected item
            onEdit = { },  // nanti tombol ini akan set showEditHistoryDialog = true dari atas
            onDelete = {
                viewModel.deleteHistoryItem(selectedHistoryItem)
                updateSelectedHistoryItem(null)
            }
        )
    }

    // === 5️⃣ Dialog Edit Riwayat (setelah user tekan edit di dialog detail) ===
    if (showEditHistoryDialog && selectedHistoryItem != null) {
        DialogTambahRiwayat(
            initialNominal = selectedHistoryItem.nominal.toString(),
            initialIsPenambahan = selectedHistoryItem.isPenambahan,
            onDismiss = onDismissEditHistory,
            onConfirm = { nominal, isPenambahan ->
                viewModel.editHistoryItem(selectedHistoryItem, nominal, isPenambahan)
                onDismissEditHistory()
                updateSelectedHistoryItem(null)
            },
            currentAmount = wishList.currentAmount  // kirim saldo untuk validasi pengurangan
        )
    }
}
