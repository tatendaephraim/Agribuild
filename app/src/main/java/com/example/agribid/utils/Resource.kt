package com.example.agribid.utils // 👈 Make sure this package name matches the file's location

/**
 * A generic class to wrap responses from the repository,
 * indicating success, error, or loading.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * Represents a successful response.
     */
    class Success<T>(data: T) : Resource<T>(data = data)

    /**
     * Represents a failed response.
     */
    class Error<T>(message: String) : Resource<T>(message = message)

    /**
     * Represents a loading state (optional, but good to have).
     */
    class Loading<T> : Resource<T>()
}