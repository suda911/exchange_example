package com.gligent.exchange.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Фабрика для viewModel
 * */
class BaseViewModelFactory<T>(val creator: () -> T) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return creator() as T
    }
}

fun <T> factory(creator: () -> T): ViewModelProvider.Factory {
    return BaseViewModelFactory(creator)
}