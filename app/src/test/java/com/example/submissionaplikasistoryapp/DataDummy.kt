package com.example.submissionaplikasistoryapp

import com.example.submissionaplikasistoryapp.response.ListStoryItem

object DataDummy {

    fun generateDummyListStoryItem(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                i.toString(),
                "date + $i",
                "name + $i",
                "desc + $i",
                1.0,
                "photo + $i",
                1.0
            )
            items.add(quote)
        }
        return items
    }
}