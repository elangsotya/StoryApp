package com.example.submissionaplikasistoryapp.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.submissionaplikasistoryapp.database.StoryDatabase
import com.example.submissionaplikasistoryapp.model.UserPreferences
import com.example.submissionaplikasistoryapp.response.ListStoryItem
import com.example.submissionaplikasistoryapp.service.ApiService

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, userPreferences),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}