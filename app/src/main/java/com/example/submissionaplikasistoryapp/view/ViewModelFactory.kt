package com.example.submissionaplikasistoryapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submissionaplikasistoryapp.di.Injection
import com.example.submissionaplikasistoryapp.view.maps.MapsViewModel
import com.example.submissionaplikasistoryapp.model.UserPreferences
import com.example.submissionaplikasistoryapp.view.detail.DetailViewModel
import com.example.submissionaplikasistoryapp.view.login.LoginViewModel
import com.example.submissionaplikasistoryapp.view.main.MainViewModel
import com.example.submissionaplikasistoryapp.view.story.StoryViewModel

class ViewModelFactory(private val context: Context, private val pref: UserPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(Injection.provideRepository(context, pref), pref) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(pref) as T
            }
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

}