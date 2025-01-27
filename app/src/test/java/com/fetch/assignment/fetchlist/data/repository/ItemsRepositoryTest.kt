package com.fetch.assignment.fetchlist.data.repository

import com.fetch.assignment.fetchlist.data.api.ItemApiService
import com.fetch.assignment.fetchlist.data.model.Item
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ItemsRepositoryTest {

    private lateinit var repository: ItemsRepository
    private val api = mockk<ItemApiService>()

    @Before
    fun setUp() {
        repository = ItemsRepository(api)
    }

    @Test
    fun `getItemsFlow emits a list of items`() = runTest {
        val mockItems = listOf(
            Item(id = 1, listId = 1, name = "Item A"),
            Item(id = 2, listId = 2, name = "Item B")
        )
        coEvery { api.getItems() } returns mockItems

        val flow = repository.getItemsFlow()
        val result = mutableListOf<List<Item>>()
        flow.collect { result.add(it) }

        assert(result.size == 1)
        assert(result.first() == mockItems)
    }

    @Test
    fun `getItemsFlow emits an empty list when API returns no items`() = runTest {
        coEvery { api.getItems() } returns emptyList()

        val flow = repository.getItemsFlow()
        val result = mutableListOf<List<Item>>()
        flow.collect { result.add(it) }

        assert(result.size == 1)
        assert(result.first().isEmpty())
    }

    @Test
    fun `getItemsFlow emits an empty list and logs error when API throws exception`() = runTest {
        coEvery { api.getItems() } throws Exception("API Failure")
        val result = mutableListOf<List<Item>>()
        try {
            val flow = repository.getItemsFlow()
            flow.collect { result.add(it) }
        } catch (e: Exception) {
            e.message?.let { assert(it.contains("API Failure")) }
        }
        assert(result.size == 1)
        assert(result.first().isEmpty())
    }

    @Test
    fun `getItemDetails returns correct item when ID exists`() = runTest {
        val mockItems = listOf(
            Item(id = 1, listId = 1, name = "Item A"),
            Item(id = 2, listId = 2, name = "Item B")
        )
        coEvery { api.getItems() } returns mockItems

        val result = repository.getItemDetails(1)

        assert(result == mockItems.first())
    }

    @Test
    fun `getItemDetails returns null if item is not found`() = runTest {
        val mockItems = listOf(
            Item(id = 1, listId = 1, name = "Item A")
        )
        coEvery { api.getItems() } returns mockItems

        val result = repository.getItemDetails(99)

        assert(result == null)
    }

    @Test
    fun `getItemDetails handles API failure gracefully`() = runTest {
        coEvery { api.getItems() } throws Exception("API Failure")

        try {
            repository.getItemDetails(1)
        } catch (e: Exception) {
            assert(e.message == "API Failure")
        }
    }
}
