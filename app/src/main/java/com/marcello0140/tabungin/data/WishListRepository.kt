package com.marcello0140.tabungin.data

import com.marcello0140.tabungin.database.HistoryDao
import com.marcello0140.tabungin.database.WishListDao
import com.marcello0140.tabungin.model.TabunganHistory
import com.marcello0140.tabungin.model.WishList
import com.marcello0140.tabungin.model.WishListWithHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class WishListRepository(
    private val wishListDao: WishListDao,
    private val historyDao: HistoryDao
) {
    // State untuk menyimpan data detail saat ini (diakses oleh DetailViewModel)
    val selectedWishList = MutableStateFlow<WishListWithHistory?>(null)

    /**
     * Mengambil semua wishlist beserta riwayatnya.
     */
    fun getAllWishLists(): Flow<List<WishListWithHistory>> =
        wishListDao.getAllWishListsWithHistory()

    /**
     * Memuat wishlist spesifik berdasarkan ID dan simpan ke selectedWishList.
     */
    suspend fun loadWishListById(id: Long) {
        wishListDao.getWishListWithHistoryById(id).collect { result ->
            selectedWishList.value = result
        }
    }

    /**
     * Menambahkan wishlist baru.
     */
    suspend fun addWishList(name: String, targetAmount: Int, createdAt: String): Long {
        val wishList = WishList(
            name = name,
            targetAmount = targetAmount,
            createdAt = createdAt
        )
        return wishListDao.insertWishList(wishList)
    }

    /**
     * Menambahkan riwayat tabungan ke wishlist tertentu.
     */
    suspend fun addHistory(
        wishListId: Long,
        nominal: Int,
        isPenambahan: Boolean,
        tanggal: String
    ) {
        val history = TabunganHistory(
            wishListId = wishListId,
            nominal = nominal,
            isPenambahan = isPenambahan,
            tanggal = tanggal
        )
        historyDao.insertHistory(history)
    }

    /**
     * Memperbarui data wishlist.
     */
    suspend fun updateWishList(wishList: WishList) {
        wishListDao.updateWishList(wishList)
    }

    /**
     * Menghapus wishlist.
     */
    suspend fun deleteWishList(wishList: WishList) {
        wishListDao.deleteWishList(wishList)
    }

    /**
     * Memperbarui data history (misal nominal atau jenis).
     */
    suspend fun updateHistory(history: TabunganHistory) {
        historyDao.updateHistory(history)
    }

    /**
     * Menghapus satu riwayat history.
     */
    suspend fun deleteHistory(history: TabunganHistory) {
        historyDao.deleteHistory(history)
    }
}
