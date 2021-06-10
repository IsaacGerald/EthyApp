package com.example.ethysell.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ethyscore.review.ReviewViewModel
import com.example.ethysell.model.Data
import com.example.ethysell.repository.ProductRepository
import java.lang.IllegalArgumentException

class ReviewViewModelFactory(
    private val repository: ProductRepository,
    private val args: Data
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ReviewViewModel(repository, args) as T
        }else{
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}