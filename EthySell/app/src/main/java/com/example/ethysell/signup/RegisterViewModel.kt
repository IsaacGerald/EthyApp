package com.example.ethysell.signup

import androidx.lifecycle.*
import com.example.ethysell.model.Register
import com.example.ethysell.repository.ProductRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _newUser = MutableLiveData<String>()
    val newUser:LiveData<String>
    get() = _newUser
     val userResponse: LiveData<Response<Register>>
        get() = repository.createResponse

  val exceptionAuth: LiveData<String>
  get() = repository.exceptionAuthResponse

    fun createUser(name: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            repository.register(name, email, password, role)
        }
    }

    fun setUser(newUser: String) {
       _newUser.value = newUser
    }


}