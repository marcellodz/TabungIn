package com.marcello0140.tabungin.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.ui.viewmodel.DetailViewModel
import com.marcello0140.tabungin.ui.viewmodel.MainViewModel

class ViewModelFactory(
    private val repository: WishListRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }
            // Kalau ada MainViewModel, tambahkan juga:
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
