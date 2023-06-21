package com.example.submissionaplikasistoryapp.di

import android.content.Context
import com.example.submissionaplikasistoryapp.database.StoryDatabase
import com.example.submissionaplikasistoryapp.config.ApiConfig
import com.example.submissionaplikasistoryapp.data.StoryRepository
import com.example.submissionaplikasistoryapp.model.UserPreferences

object Injection {
    fun provideRepository(context: Context, pref: UserPreferences): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService, pref)
    }
}