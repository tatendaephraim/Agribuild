package com.example.agribid.presentation.buyer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Domain Layer Imports
import com.example.agribid.domain.model.Bid
import com.example.agribid.domain.model.ContractStatus
import com.example.agribid.domain.model.ProductListing
import com.example.agribid.domain.model.User
import com.example.agribid.domain.repository.BidRepository // Interface
import com.example.agribid.domain.repository.ProductRepository // Interface
import com.example.agribid.domain.repository.UserRepository // Interface
// Hilt/Coroutines Imports
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
// System/Util Imports
import java.util.*
import javax.inject.Inject
import kotlin.math.*

@HiltViewModel
class BuyerViewModel @Inject constructor(
    // Inject INTERFACES, not implementations
    private val productRepository: ProductRepository,
    private val bidRepository: BidRepository,
    private val userRepository: UserRepository // Using the new interface definition
) : ViewModel() {

    // --- State Flows ---
    private val _productListings = MutableStateFlow<List<ProductListing>>(emptyList())
    val productListings: StateFlow<List<ProductListing>> = _productListings.asStateFlow()

    private val _currentListing = MutableStateFlow<ProductListing?>(null)
    val currentListing: StateFlow<ProductListing?> = _currentListing.asStateFlow()

    private val _bidAmount = MutableStateFlow("")
    val bidAmount: StateFlow<String> = _bidAmount.asStateFlow()

    private val _bidQuantity = MutableStateFlow("")
    val bidQuantity: StateFlow<String> = _bidQuantity.asStateFlow()

    private val _isLoading = MutableStateFlow(true) // Start loading
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    // --- End State Flows ---

    // Expose User data collected directly from the repository's getCurrentUser flow
    val currentUser: StateFlow<User?> = userRepository.getCurrentUser() // <-- Use the new function
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // Start with null until the flow emits
        )

    // No separate _currentUserId needed, derive from currentUser StateFlow

    init {
        Log.d("BuyerViewModel", "Initializing...")
        // Observe the user data flow (which now also implies auth state)
        viewModelScope.launch {
            currentUser.collect { user -> // Collect the StateFlow derived from repo's getCurrentUser()
                if (user != null) {
                    Log.d("BuyerViewModel", "User data/auth state updated: User logged in (${user.userId})")
                    // Load listings now that we have user details (including country/province)
                    loadProductListings(user.country, user.province)
                } else {
                    Log.d("BuyerViewModel", "User data/auth state updated: No user found/logged out.")
                    // Attempt anonymous sign-in if no user is found
                    handleAnonymousSignInAndLoad()
                }
            }
        }
    }

    private fun handleAnonymousSignInAndLoad() {
        viewModelScope.launch {
            Log.d("BuyerViewModel", "Handling anonymous sign-in...")
            // Call the new signInAnonymously function (doesn't return user)
            userRepository.signInAnonymously() // <-- Use the new function
            // After calling this, the `currentUser` flow (listening to `getCurrentUser`)
            // should eventually emit the new anonymous user's data, triggering
            // the collector in init to call loadProductListings.
            // We might need to handle potential errors from signInAnonymously if the repo throws.
            // For now, assume it triggers the flow update on success.
            // If sign-in fails repeatedly, the flow might keep emitting null.
            _isLoading.value = false // Set loading false after attempting sign in, list loading will handle its own loading state.
        }
    }


    fun loadProductListings(country: String?, province: String?) {
        // Get the current user ID directly from the StateFlow's value for validation
        val currentUserId = currentUser.value?.userId
        if (currentUserId == null) {
            Log.w("BuyerViewModel", "Skipping loadProductListings - user ID is null in currentUser.")
            _isLoading.value = false // Make sure loading stops
            _productListings.value = emptyList() // Clear list
            return // Don't proceed if no user ID
        }

        viewModelScope.launch {
            _isLoading.value = true
            Log.d("BuyerViewModel", "Loading product listings for User: $currentUserId, Country: $country, Province: $province")

            productRepository.getProductListings(country, province)
                .catch { e ->
                    Log.e("BuyerViewModel", "Error collecting product listings", e)
                    _productListings.value = emptyList() // Set empty list on error
                    _isLoading.value = false
                    // TODO: Optionally expose error state to UI
                }
                .collectLatest { listings -> // Use collectLatest
                    val user = currentUser.value // Get latest user data snapshot from StateFlow
                    Log.d("BuyerViewModel", "Collected ${listings.size} listings.")
                    val updatedListings = if (user != null && user.latitude != 0.0 && user.longitude != 0.0) {
                        listings.mapNotNull { listing -> // Use mapNotNull
                            try {
                                listing.copy(
                                    distanceKm = calculateDistance(
                                        user.latitude, user.longitude,
                                        listing.latitude, listing.longitude
                                    )
                                )
                            } catch (e: Exception) {
                                Log.e("BuyerViewModel", "Error calculating distance for listing ${listing.productId}", e)
                                null // Filter out errors
                            }
                        }.sortedBy { it.distanceKm }
                    } else {
                        listings // Don't sort if location is unavailable
                    }

                    _productListings.value = updatedListings
                    _isLoading.value = false // Set loading false after processing
                    Log.d("BuyerViewModel", "Product listings updated.")
                }
        }
    }


    fun selectListing(listing: ProductListing) {
        _currentListing.value = listing
        _bidAmount.value = listing.basePriceUSD.toString()
        _bidQuantity.value = try {
            listing.quantityKg.toInt().toString()
        } catch (e: Exception) {
            listing.quantityKg.toString()
        }
    }

    fun selectListingById(productId: String) {
        viewModelScope.launch {
            _currentListing.value = productRepository.getListingById(productId)
        }
    }

    fun clearBidState() {
        _currentListing.value = null
        _bidAmount.value = ""
        _bidQuantity.value = ""
    }

    fun updateBidAmount(amount: String) {
        if (amount.isEmpty() || amount == "." || amount.matches(Regex("^\\d*\\.?\\d*\$"))) {
            _bidAmount.value = amount
        }
    }

    fun updateBidQuantity(quantity: String) {
        if (quantity.isEmpty() || quantity == "." || quantity.matches(Regex("^\\d*\\.?\\d*\$"))) {
            _bidQuantity.value = quantity
        }
    }

    fun submitBid() {
        viewModelScope.launch {
            val listing = _currentListing.value ?: run {
                Log.w("BuyerViewModel", "Submit bid: No listing selected.")
                return@launch
            }
            val amount = _bidAmount.value.toDoubleOrNull() ?: run {
                Log.w("BuyerViewModel", "Submit bid: Invalid amount format.")
                return@launch
            }
            val quantity = _bidQuantity.value.toDoubleOrNull() ?: run {
                Log.w("BuyerViewModel", "Submit bid: Invalid quantity format.")
                return@launch
            }
            // Get user ID from the currentUser StateFlow
            val userId = currentUser.value?.userId ?: run {
                Log.e("BuyerViewModel", "Cannot submit bid, user ID is null.")
                return@launch
            }

            // --- Validation ---
            if (quantity <= 0 || amount <= 0 || quantity > listing.quantityKg) {
                Log.w("BuyerViewModel", "Submit bid: Validation failed (qty=$quantity, amount=$amount, available=${listing.quantityKg}).")
                return@launch
            }
            // --- End Validation ---

            val bid = Bid(
                buyerId = userId,
                productId = listing.productId,
                offeredPriceUSD = amount,
                quantityKg = quantity,
                status = ContractStatus.PENDING,
                createdAt = System.currentTimeMillis()
            )

            Log.d("BuyerViewModel", "Submitting bid: $bid")
            val result = bidRepository.createBid(bid)

            if (result.isSuccess) {
                Log.d("BuyerViewModel", "Bid submitted successfully: ${result.getOrNull()}")
                clearBidState()
            } else {
                Log.e("BuyerViewModel", "Bid submission failed", result.exceptionOrNull())
            }
        }
    }

    fun calculateTransportCost(distanceKm: Double): Double {
        return if (distanceKm.isFinite() && distanceKm >= 0) distanceKm * 0.10 else 0.0
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        if (lat1 == 0.0 || lon1 == 0.0 || lat2 == 0.0 || lon2 == 0.0) return Double.POSITIVE_INFINITY

        val R = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = R * c
        return distance
    }
}
