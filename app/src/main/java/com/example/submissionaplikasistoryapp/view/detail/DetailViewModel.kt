package com.example.submissionaplikasistoryapp.view.detail

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.submissionaplikasistoryapp.config.ApiConfig
import com.example.submissionaplikasistoryapp.model.UserModel
import com.example.submissionaplikasistoryapp.model.UserPreferences
import com.example.submissionaplikasistoryapp.response.DetailStoryResponse
import com.example.submissionaplikasistoryapp.response.Story
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private val pref: UserPreferences): ViewModel() {

    private val _detailStory = MutableLiveData<Story>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun setDetailStory(token: String?, id: String?) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStoryDetail(token, id)
        client.enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _detailStory.value = response.body()?.story!!
                } else{
                    Log.e(TAG, "\"onFailure: ${response.message()}\"")
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getDetailStory(): LiveData<Story>{
        return _detailStory
    }
}