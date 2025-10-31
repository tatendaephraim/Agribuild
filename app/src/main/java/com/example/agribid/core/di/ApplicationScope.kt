package com.example.agribid.core.di

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.BINARY

// Qualifier annotation to distinguish the application-level CoroutineScope
@Retention(BINARY)
@Qualifier
annotation class ApplicationScope
