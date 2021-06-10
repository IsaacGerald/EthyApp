package com.example.ethysell.updateproduct

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ethysell.model.Data
import com.example.ethysell.model.Product
import com.example.ethysell.network.PostResponse
import com.example.ethysell.repository.ProductRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File

class UpdateViewModel(private val repository: ProductRepository): ViewModel() {

private val _myProductResponse = MutableLiveData<Response<Product>>()
    private val  _bitmap = MutableLiveData<Bitmap>()

    private val _isCamera = MutableLiveData<Boolean>()
    val isCamera: LiveData<Boolean>
        get() = _isCamera

    val bitmap: LiveData<Bitmap>
    get() = _bitmap

    val updateResponse: LiveData<String>
    get() = repository.updateResponse

    fun updateProduct(product: Data, file: File){
        viewModelScope.launch {
          repository.update(product, file)
        }
    }




    fun setBitmapImage(bitmap: Bitmap){
        _bitmap.value = bitmap
    }

    fun imageState(isCamera: Boolean){
        _isCamera.value = isCamera
    }

}