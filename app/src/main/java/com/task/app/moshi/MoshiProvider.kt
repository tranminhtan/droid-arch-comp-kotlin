package com.task.app.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

// This Set can gather all Factories by using Dagger power as every module has one Factory
class MoshiProvider(private val factories: Set<JsonAdapter.Factory>) {

    fun getMoshi(): Moshi {
        return Moshi.Builder()
            .apply {
                for (factory in factories) {
                    add(factory)
                }
            }
            .build()
    }
}