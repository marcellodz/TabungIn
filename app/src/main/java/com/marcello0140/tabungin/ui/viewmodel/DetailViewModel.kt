package com.marcello0140.tabungin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.model.TabunganHistory
import com.marcello0140.tabungin.model.WishList
import com.marcello0140.tabungin.model.WishListWithHistory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DetailViewModel(
    private val repository: WishListRepository
) : ViewModel() {

    // Expose selectedWishList as StateFlow, diambil dari repository (langsung mengalir ke UI)
    val wishListWithHistory: StateFlow<WishListWithHistory?> =
        repository.selectedWishList.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Memicu repository untuk load by id → repository akan update selectedWishList-nya
    fun loadWishListById(id: Long) {
        viewModelScope.launch {
            repository.loadWishListById(id)
        }
    }

    fun updateWishlist(updated: WishList) {
        viewModelScope.launch {
            repository.updateWishList(updated)
        }
    }

    fun deleteWishlist(wishList: WishList) {
        viewModelScope.launch {
            repository.deleteWishList(wishList)
        }
    }

    fun addHistoryItem(wishListId: Long, nominal: Int, isPenambahan: Boolean) {
        viewModelScope.launch {
            repository.addHistory(
                wishListId = wishListId,
                nominal = nominal,
                isPenambahan = isPenambahan,
                tanggal = getTodayDate()
            )
        }
    }

    fun editHistoryItem(history: TabunganHistory, newNominal: Int, isPenambahan: Boolean) {
        viewModelScope.launch {
            repository.updateHistory(
                history.copy(nominal = newNominal, isPenambahan = isPenambahan)
            )
        }
    }

    fun deleteHistoryItem(history: TabunganHistory) {
        viewModelScope.launch {
            repository.deleteHistory(history)
        }
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }
}
