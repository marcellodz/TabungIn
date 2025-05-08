package com.marcello0140.tabungin.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.viewmodel.MainViewModel

class ViewModelFactory(
    private val repository: WishListRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
