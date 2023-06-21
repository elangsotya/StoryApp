package com.example.submissionaplikasistoryapp.view.login

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import com.example.submissionaplikasistoryapp.config.ApiConfig
import com.example.submissionaplikasistoryapp.model.UserModel
import com.example.submissionaplikasistoryapp.model.UserPreferences
import com.example.submissionaplikasistoryapp.response.LoginResponse
import com.example.submissionaplikasistoryapp.service.LoginRequest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreferences): ViewModel() {

    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun loginAction(email: String, password: String){
        _isLoading.value = true
        val request = LoginRequest(email, password)
        val client = ApiConfig.getApiService().doLogin(request)
        client.enqueue(object : Callback<LoginResponse>{
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    viewModelScope.launch {
                        response.body()?.loginResult?.token?.let { pref.login(it) }
                    }
                    _isLogin.value = true
                }else {
                    Log.e(ContentValues.TAG, "\"onFailure: ${response.message()}\"")
                    _isLogin.value = false
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(ContentValues.TAG, "\"onFailure: ${t.message}\"")
            }
        })
    }



}