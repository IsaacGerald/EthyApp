package com.example.ethysell.productdetail

import androidx.lifecycle.*
import com.example.ethysell.db.DataBaseData
import com.example.ethysell.model.Comment
import com.example.ethysell.model.Data
import com.example.ethysell.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailViewModel(private val repository: ProductRepository,private val args: Data): ViewModel() {

 private val _data = MutableLiveData<Data>()

    val data: LiveData<Data>
    get() = _data

    val deleteItemResponse: LiveData<String>
    get() = repository.deleteItemResponse


    val fetchResponse: LiveData<String>
        get() = repository.fetchResponse

    fun dataFromDatabase() = liveData(Dispatchers.IO) {
        emit(repository.dataFromDatabase())
    }

    fun getDetailProduct(itemId: Int) = liveData(Dispatchers.IO){
        emit(repository.getDetailProduct(itemId))
    }


    val deleteCommentResponse: LiveData<String>
    get() = repository.deleteCommentResponse


    init {
        refreshProducts()
    }

     fun refreshProducts() {
        viewModelScope.launch {
            repository.refreshData()
        }

    }

    fun deleteItem(itemId: Int){
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }


    fun getComments(list: List<Data>): List<Comment> {
        val comment = mutableListOf<Comment>()

        for (items in list) {
            if (items.id == args.id) {
                items.comments?.let { comment.addAll(it) }
            }
        }
        return comment

    }





    fun recievedData(data: Data){
          _data.value = data
    }

    fun updateCommentData(data: DataBaseData){
        viewModelScope.launch {
            repository.updateComment(data)
        }
    }

    fun deleteComment(commentId: Int, isHidden: Boolean, newData: Data) {

        viewModelScope.launch {
            repository.deleteComment(commentId, isHidden, newData)
        }
    }

    fun getVisibleComments(comments: List<Comment>): List<Comment> {
        val newComments = mutableListOf<Comment>()


        for (item in comments){
            if (!item.isHidden){
                newComments.add(item)
            }
        }


        return newComments
    }


}