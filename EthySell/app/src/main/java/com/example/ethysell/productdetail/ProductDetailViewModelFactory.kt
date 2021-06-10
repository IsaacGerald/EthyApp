package com.example.ethysell.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ethysell.model.Data
import com.example.ethysell.repository.ProductRepository
import java.lang.IllegalArgumentException

class ProductDetailViewModelFactory(private val repository: ProductRepository,private val args: Data): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ProductDetailViewModel(repository, args) as T
        }else{
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}