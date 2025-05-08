package com.marcello0140.tabungin.data

import com.marcello0140.tabungin.model.TabunganHistory
import com.marcello0140.tabungin.model.WishList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class WishListRepository {

    // Mutable state dummy di memori
    private val _wishLists = MutableStateFlow(listOf(
        WishList(
            id = 1,
            name = "Beli iPhone 15",
            targetAmount = 15000000,
            currentAmount = 7000000,
            createdAt = "01 Mei 2025",
            history = listOf(
                TabunganHistory(1, "02 Mei 2025", 2000000, true),
                TabunganHistory(2, "04 Mei 2025", 5000000, true),
                TabunganHistory(3, "06 Mei 2025", 1000000, false),
                TabunganHistory(5, "06 Mei 2025", 1000000, false),
                TabunganHistory(6, "06 Mei 2025", 1000000, false),
                TabunganHistory(7, "06 Mei 2025", 1000000, false),
                TabunganHistory(8, "06 Mei 2025", 1000000, false)
            )
        )
    ))

    // Public flow dibaca dari ViewModel
    fun getAllWishList(): Flow<List<WishList>> = _wishLists.asStateFlow()

    // Dapatkan wishlist spesifik
    fun getWishListById(id: Int): Flow<WishList?> {
        return _wishLists.asStateFlow().map { list ->
            list.find { it.id == id }
        }
    }


    // Tambah wishlist baru (dummy, langsung update list)
     fun addWishList(name: String, targetAmount: Int) {
        val currentList = _wishLists.value
        val newId = (currentList.maxOfOrNull { it.id } ?: 0) + 1

        val newWishList = WishList(
            id = newId,
            name = name,
            targetAmount = targetAmount,
            currentAmount = 0,
            createdAt = getTodayDate(),
            history = emptyList()
        )

        _wishLists.value = currentList + newWishList
    }

    private fun getTodayDate(): String {
        return java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
            .format(java.util.Date())
    }
}
