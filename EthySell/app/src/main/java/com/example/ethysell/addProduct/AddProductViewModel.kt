package com.example.ethysell.addProduct

import android.graphics.Bitmap
import android.graphics.Camera
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ethysell.model.Data
import com.example.ethysell.model.Product
import com.example.ethysell.network.PostResponse
import com.example.ethysell.network.ProductContainer
import com.example.ethysell.repository.ProductRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File

class AddProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _itemType = MutableLiveData<String>()
    val itemTypes: LiveData<String>
    get() = _itemType

    val postItemResponse: LiveData<String>
    get() = repository.postItemResponse


    private val _isCamera = MutableLiveData<Boolean>()
    val isCamera: LiveData<Boolean>
    get() = _isCamera


    private val _bitmap = MutableLiveData<Bitmap>()

    val bitmap: LiveData<Bitmap>
        get() = _bitmap

    val myProductResponse: LiveData<String>
        get() = repository.productResponse

    fun postProducts(product: Data, bitmap: File) {
        viewModelScope.launch {
            repository.post(product, bitmap)
        }
    }

    fun setBitmapImage(bitmap: Bitmap) {
        _bitmap.value = bitmap
    }

    fun imageState(isCamera: Boolean){
        _isCamera.value = isCamera
    }

    fun setType(type: String) {
      _itemType.value = type
    }

}