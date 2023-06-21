package com.example.submissionaplikasistoryapp.view.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submissionaplikasistoryapp.data.StoryRepository
import com.example.submissionaplikasistoryapp.model.UserModel
import com.example.submissionaplikasistoryapp.model.UserPreferences
import com.example.submissionaplikasistoryapp.response.ListStoryItem
import kotlinx.coroutines.launch


class MainViewModel(storyRepository: StoryRepository, private val pref: UserPreferences? = null): ViewModel() {

    val story : LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStory().cachedIn(viewModelScope)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser(): LiveData<UserModel> {
        return pref!!.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref!!.logout()
        }
    }
}

