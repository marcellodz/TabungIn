// DetailViewModel.kt
package com.marcello0140.tabungin.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.model.TabunganHistory
import com.marcello0140.tabungin.model.WishList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailViewModel(
    private val repository: WishListRepository // DI: inject repository untuk data sumber
) : ViewModel() {

    // State utama: menyimpan wishlist yang sedang ditampilkan
    var wishList by mutableStateOf<WishList?>(null)
        private set

    /**
     * Fungsi untuk menambahkan riwayat tabungan baru.
     */
    fun addHistoryItem(nominal: Int, isPenambahan: Boolean) {
        val current = wishList
        if (current != null) {
            val newId = (current.history.maxOfOrNull { it.id } ?: 0) + 1

            val newHistoryItem = TabunganHistory(
                id = newId,
                nominal = nominal,
                isPenambahan = isPenambahan,
                tanggal = getTodayDate()
            )

            val updatedHistory = current.history + newHistoryItem

            // Hitung ulang currentAmount setelah penambahan/pengurangan
            val updatedAmount = updatedHistory.filter { it.isPenambahan }.sumOf { it.nominal } -
                    updatedHistory.filter { !it.isPenambahan }.sumOf { it.nominal }

            wishList = current.copy(
                history = updatedHistory,
                currentAmount = updatedAmount
            )
        }
    }

    /**
     * Fungsi untuk memperbarui detail wishlist (nama dan target).
     */
    fun updateWishlist(newName: String, newTargetAmount: Int) {
        wishList = wishList?.copy(
            name = newName,
            targetAmount = newTargetAmount
        )
        // Catatan: kalau pakai Room nanti, tambahkan logic update di database
    }

    /**
     * Fungsi untuk menghapus wishlist.
     */
    fun deleteWishlist(id: Int) {
        wishList = null
        // Catatan: kalau pakai Room nanti, tambahkan logic delete di database
    }

    /**
     * Fungsi untuk mengambil wishlist dari repository (dummy sekarang, Room nanti).
     */
    fun loadWishListById(id: Int) {
        viewModelScope.launch {
            val data = repository.getWishListById(id).first()
            wishList = data
        }
    }

    /**
     * Fungsi untuk mengedit item riwayat tabungan tertentu.
     */
    fun editHistoryItem(historyId: Int, newNominal: Int, isPenambahan: Boolean) {
        wishList = wishList?.copy(
            history = wishList?.history?.map {
                if (it.id == historyId) it.copy(nominal = newNominal, isPenambahan = isPenambahan)
                else it
            } ?: emptyList()
        )
    }

    /**
     * Fungsi untuk menghapus item riwayat tabungan tertentu.
     */
    fun deleteHistoryItem(historyId: Int) {
        wishList = wishList?.copy(
            history = wishList?.history?.filterNot { it.id == historyId } ?: emptyList()
        )
    }

    /**
     * Fungsi utilitas untuk mendapatkan tanggal hari ini sebagai string.
     */
    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}
