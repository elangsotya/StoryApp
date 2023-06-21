package com.example.submissionaplikasistoryapp.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.submissionaplikasistoryapp.response.ListStoryItem
import androidx.room.Query


@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStory(story: List<ListStoryItem>)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}