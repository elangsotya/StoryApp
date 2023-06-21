package com.example.submissionaplikasistoryapp.view.signup

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import com.example.submissionaplikasistoryapp.config.ApiConfig
import com.example.submissionaplikasistoryapp.response.RegisterResponse
import com.example.submissionaplikasistoryapp.service.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupViewModel: ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRegistered = MutableLiveData<Boolean>()
    val isRegistered: LiveData<Boolean> = _isRegistered

    fun registerAction(name: String, password: String, email: String){
        _isLoading.value = true
        val request = RegisterRequest(name, email, password)
        val client = ApiConfig.getApiService().doRegister(request)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if(response.isSuccessful){
                    _isRegistered.value = true
                }else{
                    _isRegistered.value = false
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isRegistered.value = false
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")

            }
        })

    }

}