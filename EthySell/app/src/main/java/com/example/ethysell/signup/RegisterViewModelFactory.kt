package com.example.ethysell.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ethysell.repository.ProductRepository
import java.lang.IllegalArgumentException

class RegisterViewModelFactory(private val repository: ProductRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(repository) as T
        }else{
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}