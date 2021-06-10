package com.example.ethysell.productUi

import androidx.lifecycle.*
import com.example.ethysell.model.Data
import com.example.ethysell.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository): ViewModel() {

    private val _navigateToSelectedProduct = MutableLiveData<Data?>()
    val navigateToSelectedProduct: LiveData<Data?>
    get() = _navigateToSelectedProduct

    val fetchResponse: LiveData<String>
        get() = repository.fetchResponse

    fun dataFromDatabase() = liveData(Dispatchers.IO) {
       emit(repository.dataFromDatabase())
    }


//    val productResponse: LiveData<List<Data>> = Transformations.map(newProducts()){ it ->
//        it.asDomainModel()
//    }

    init {
        refreshProducts()
    }

    fun refreshProducts(){
        viewModelScope.launch {
            repository.refreshData()
        }
    }


    fun displayProduct(data: Data){
        _navigateToSelectedProduct.value = data
    }

    fun displayProductCompleted(){
        _navigateToSelectedProduct.value = null
    }


}