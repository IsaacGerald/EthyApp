package com.example.ethyscore.login

import androidx.lifecycle.*
import com.example.ethysell.model.CurrentUser

import com.example.ethysell.repository.ProductRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val repository: ProductRepository) : ViewModel() {

     val loginResponse: LiveData<Response<CurrentUser>>
        get() = repository.loginResponse

    val exceptionResponse: LiveData<String>
    get() = repository.exceptionAuthResponse


    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password)
        }
    }


}