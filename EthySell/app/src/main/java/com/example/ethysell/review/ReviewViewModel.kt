package com.example.ethyscore.review

import androidx.lifecycle.*
import com.example.ethysell.db.DataBaseData
import com.example.ethysell.model.Comment
import com.example.ethysell.model.CurrentUser
import com.example.ethysell.model.Data

import com.example.ethysell.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewViewModel(private val repository: ProductRepository, private val args: Data) :
    ViewModel() {

    private val _data = MutableLiveData<Data>()
    val data: LiveData<Data>
        get() = _data

    fun getDetailProduct(itemId: Int) = liveData(Dispatchers.IO) {
        emit(repository.getDetailProduct(itemId))
    }


    fun getRecievedData(data: Data) {
        _data.value = data
    }



    val commentResponse: LiveData<String>
        get() = repository.commentResponse


    init {
        refreshProducts()
    }

     fun refreshProducts() {
        viewModelScope.launch {
            repository.refreshData()
        }

    }

    //
    fun postComment(itemId: Int, comment: String, currentUser: CurrentUser, data: Data) {

        viewModelScope.launch {
            repository.postComment(itemId, comment, currentUser, data)
        }
    }
    fun deleteComment(commentId: Int, isHidden: Boolean, data: Data) {

        viewModelScope.launch {
            repository.deleteComment(commentId, isHidden, data)
        }
    }

    fun updateDatabaseComment(data: DataBaseData) {
        viewModelScope.launch {
            repository.updateComment(data)
        }
    }

    fun getVisibleComments(comments: List<Comment>): List<Comment> {
        val newComments = mutableListOf<Comment>()

        for (item in comments) {
            if (!item.isHidden) {
                newComments.add(item)
            }
        }


        return newComments
    }
}