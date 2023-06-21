package com.example.submissionaplikasistoryapp.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.submissionaplikasistoryapp.model.UserModel
import com.example.submissionaplikasistoryapp.model.UserPreferences

class StoryViewModel(private val pref: UserPreferences):ViewModel() {

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

}