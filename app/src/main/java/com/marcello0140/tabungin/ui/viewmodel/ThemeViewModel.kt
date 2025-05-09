package com.marcello0140.tabungin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcello0140.tabungin.datastore.PreferenceManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = preferenceManager.darkModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Fungsi asli: set dark mode secara spesifik (true/false)
    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferenceManager.setDarkMode(enabled)
        }
    }

    // Tambahkan ini: toggle otomatis
    fun toggleTheme() {
        viewModelScope.launch {
            val current = isDarkMode.value
            preferenceManager.setDarkMode(!current)
        }
    }
}
