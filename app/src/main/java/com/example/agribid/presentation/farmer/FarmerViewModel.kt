package com.example.agribid.presentation.farmer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Domain Layer Imports
import com.example.agribid.domain.model.HarvestForecast // **FIX: Correct domain model import**
import com.example.agribid.domain.model.User           // **FIX: Correct domain model import**
import com.example.agribid.domain.repository.ForecastRepository
import com.example.agribid.domain.repository.UserRepository
// Hilt/Coroutines Imports
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
// System/Util Imports
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FarmerViewModel @Inject constructor(
    private val forecastRepository: ForecastRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // --- State Flows for Form Input ---
    private val _productName = MutableStateFlow("")
    val productName: StateFlow<String> = _productName.asStateFlow()
    private val _estimatedQuantity = MutableStateFlow("")
    val estimatedQuantity: StateFlow<String> = _estimatedQuantity.asStateFlow()
    private val _pricePerKg = MutableStateFlow("")
    val pricePerKg: StateFlow<String> = _pricePerKg.asStateFlow()
    private val _harvestDate = MutableStateFlow<Long?>(null)
    val harvestDate: StateFlow<Long?> = _harvestDate.asStateFlow()
    // --- End Form Input ---

    // **FIX: Use currentUserData flow from UserRepository for the domain User object**
    val currentUser: StateFlow<User?> = userRepository.currentUserData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // **FIX: Track user ID separately from the auth flow**
    private val _currentUserId = MutableStateFlow<String?>(null)

    init {
        Log.d("FarmerViewModel", "Initializing...")
        viewModelScope.launch {
            // **FIX: Collect the currentAuthUser flow for the ID**
            userRepository.currentAuthUser.collectLatest { firebaseUser ->
                _currentUserId.value = firebaseUser?.uid
                if (firebaseUser == null) {
                    Log.d("FarmerViewModel", "No user logged in, attempting sign-in.")
                    // **FIX: Use getOrSignInUser function**
                    userRepository.getOrSignInUser()
                } else {
                    Log.d("FarmerViewModel", "User authenticated: ${firebaseUser.uid}")
                }
            }
        }
        // No need for a separate collector for currentUserData here,
        // as we get its latest value directly in saveForecast
    }

    // --- Update Functions (with basic validation) ---
    fun updateProductName(name: String) { _productName.value = name }
    fun updateHarvestDate(date: Long) { _harvestDate.value = date }
    fun updateEstimatedQuantity(quantity: String) {
        if (quantity.isEmpty() || quantity.matches(Regex("^\\d*\\.?\\d*$"))) { _estimatedQuantity.value = quantity }
    }
    fun updatePricePerKg(price: String) {
        if (price.isEmpty() || price.matches(Regex("^\\d*\\.?\\d*$"))) { _pricePerKg.value = price }
    }
    // --- End Update Functions ---

    fun saveForecast(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // **FIX: Get domain User object and user ID from state flows**
            val user: User? = currentUser.value
            val userId: String? = _currentUserId.value

            if (user == null || userId == null) {
                Log.e("FarmerViewModel", "Cannot save forecast: User data/ID unavailable.")
                // TODO: Show UI error message
                return@launch
            }

            // --- Validation ---
            val quantity = _estimatedQuantity.value.toDoubleOrNull() ?: 0.0
            val price = _pricePerKg.value.toDoubleOrNull() ?: 0.0
            val date = _harvestDate.value
            val name = _productName.value

            // Ensure date is in the future
            val todayStartMillis = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            if (name.isBlank() || quantity <= 0 || price <= 0 || date == null || date < todayStartMillis) {
                Log.w("FarmerViewModel", "Forecast validation failed. Name: '$name', Qty: $quantity, Price: $price, Date: $date")
                // TODO: Show detailed validation error message to user
                return@launch
            }
            // --- End Validation ---

            // **FIX: Instantiate the correct HarvestForecast domain model**
            val forecast = HarvestForecast(
                // forecastId is omitted - let repository handle it
                farmerId = userId, // **FIX: Use the String ID**
                productName = name,
                estimatedQuantityKg = quantity,
                pricePerKgUSD = price,
                estimatedHarvestDate = date,
                country = user.country, // **FIX: Access property on domain User object**
                province = user.province // **FIX: Access property on domain User object**
            )

            Log.d("FarmerViewModel", "Attempting to save forecast: $forecast")
            // Use Result's extension functions (onSuccess/onFailure)
            forecastRepository.createForecast(forecast).onSuccess { generatedId ->
                Log.i("FarmerViewModel", "Forecast saved successfully: $generatedId")
                clearInputFields() // Reset the form
                onSuccess() // Trigger UI callback (e.g., navigation)
            }.onFailure { exception ->
                Log.e("FarmerViewModel", "Failed to save forecast", exception)
                // TODO: Show error message to user via state/event
            }
        }
    }

    private fun clearInputFields() {
        _productName.value = ""
        _estimatedQuantity.value = ""
        _pricePerKg.value = ""
        _harvestDate.value = null
    }
}
