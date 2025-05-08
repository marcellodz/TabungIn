package com.marcello0140.tabungin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.model.WishList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(private val repository: WishListRepository) : ViewModel() {

    private val _wishList = MutableStateFlow<List<WishList>>(emptyList())
    val wishList: StateFlow<List<WishList>> = _wishList

    init {
        viewModelScope.launch {
            repository.getAllWishList().collectLatest {
                _wishList.value = it
            }
        }
    }

}
