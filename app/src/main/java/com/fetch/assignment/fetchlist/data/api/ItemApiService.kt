package com.fetch.assignment.fetchlist.data.api

import com.fetch.assignment.fetchlist.data.model.Item
import retrofit2.http.GET

interface ItemApiService {
    @GET("hiring.json")
    suspend fun getItems(): List<Item>
}