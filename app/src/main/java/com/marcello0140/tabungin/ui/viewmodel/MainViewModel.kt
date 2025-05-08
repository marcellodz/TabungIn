package com.marcello0140.tabungin.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.model.WishListWithHistory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(
    private val repository: WishListRepository
) : ViewModel() {

    val wishListWithHistories: StateFlow<List<WishListWithHistory>> =
        repository.getAllWishLists()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addWishlist(name: String, targetAmount: Int, createdAt: String = getTodayDate()) {
        viewModelScope.launch {
            repository.addWishList(name, targetAmount, createdAt)
        }
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }
}
