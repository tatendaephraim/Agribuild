package com.example.agribid.presentation.settings // <-- FIX: Package name corrected

import androidx.lifecycle.ViewModel
import com.example.agribid.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // TODO: Connect this to a DataStore repository
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    fun onThemeChanged(isDark: Boolean) {
        _isDarkMode.update { isDark }
        // TODO: Save this value to DataStore
    }

    fun logout() {
        // TODO: Call a method in UserRepository to sign out
        // userRepository.signOut()
    }
}
