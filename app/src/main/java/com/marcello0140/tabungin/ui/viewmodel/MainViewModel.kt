package com.marcello0140.tabungin.viewmodel

import androidx.lifecycle.ViewModel
import com.marcello0140.tabungin.model.WishList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    // Dummy data sementara
    private val _wishLists = MutableStateFlow(
        listOf(
            WishList(id = 1, name = "MacBook", targetAmount = 30_000_000, currentAmount = 5_000_000),
            WishList(id = 2, name = "iPhone", targetAmount = 20_000_000, currentAmount = 20_000_000)
        )
    )
    val wishLists: StateFlow<List<WishList>> = _wishLists

    fun getFilteredList(tercapai: Boolean): List<WishList> {
        return if (tercapai) {
            _wishLists.value.filter { it.currentAmount >= it.targetAmount }
        } else {
            _wishLists.value.filter { it.currentAmount < it.targetAmount }
        }
    }

    fun getWishById(id: Int): WishList? {
        return _wishLists.value.find { it.id == id }
    }
}
