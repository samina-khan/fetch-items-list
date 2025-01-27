package com.square.assignment.fetchlist.data.repository


import android.util.Log
import com.square.assignment.fetchlist.data.api.ItemApiService
import com.square.assignment.fetchlist.data.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ItemsRepository @Inject constructor(
    private val api: ItemApiService
) {
    fun getItemsFlow(): Flow<List<Item>> = flow {
        try {
            val response = api.getItems()
            emit(response)
        } catch (e: Exception) {
            emit(emptyList())
            Log.d("ItemRepository", "Failed to load items: ${e.message}")
            throw Exception("Server Error: ${e.message}")
        }
    }

    suspend fun getItemDetails(id: Int): Item? {
        return api.getItems().find { it.id == id }
    }
}
