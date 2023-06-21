package com.example.submissionaplikasistoryapp.view.maps

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.submissionaplikasistoryapp.config.ApiConfig
import com.example.submissionaplikasistoryapp.model.UserModel
import com.example.submissionaplikasistoryapp.model.UserPreferences
import com.example.submissionaplikasistoryapp.response.GetStoriesResponse
import com.example.submissionaplikasistoryapp.response.ListStoryItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: UserPreferences): ViewModel() {

    private val _storyMaps = MutableLiveData<List<ListStoryItem>>()
    val storyMaps: LiveData<List<ListStoryItem>> = _storyMaps

    fun getUser(): LiveData<UserModel>{
        return pref.getUser().asLiveData()
    }
    
    fun setStoryMap(page: Int?, size: Int?, location: Int, token: String){
        val client = ApiConfig.getApiService().getStoryMaps(token, page, size, location)
        client.enqueue(object : Callback<GetStoriesResponse> {
            override fun onResponse(
                call: Call<GetStoriesResponse>,
                response: Response<GetStoriesResponse>
            ) {
                if(response.isSuccessful){
                    _storyMaps.value = response.body()?.listStory!!
                }else{
                    Log.e(ContentValues.TAG, "\"onFailure: ${response.message()}\"")
                }
            }
            override fun onFailure(call: Call<GetStoriesResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

}