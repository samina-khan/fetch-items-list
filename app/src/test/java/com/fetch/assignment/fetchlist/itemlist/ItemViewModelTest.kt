package com.fetch.assignment.fetchlist.itemlist

import com.fetch.assignment.fetchlist.data.model.Item
import com.fetch.assignment.fetchlist.data.repository.ItemsRepository
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ItemListViewModelTest {

    private lateinit var viewModel: ItemListViewModel
    private lateinit var repository: ItemsRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = ItemListViewModel(repository, testDispatcher)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchItems sets UI state to Loading initially`() = runTest {
        coEvery { repository.getItemsFlow() } returns flowOf(emptyList())

        viewModel.fetchItems()

        assertEquals(ItemListViewModel.UiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `fetchItems sets UI state to Success when data is available`() = runTest {
        val mockItems = listOf(Item(id = 1, listId = 1, name = "Item 1"))
        coEvery { repository.getItemsFlow() } returns flowOf(mockItems)

        viewModel.fetchItems()
        advanceUntilIdle()

        assertEquals(ItemListViewModel.UiState.Success, viewModel.uiState.value)
        assertEquals(mockItems.groupBy { it.listId }, viewModel.groupedItemsFlow.value)
    }

    @Test
    fun `fetchItems sets UI state to Empty when no data is available`() = runTest {
        coEvery { repository.getItemsFlow() } returns flowOf(emptyList())

        viewModel.fetchItems()
        advanceUntilIdle()

        assertEquals(ItemListViewModel.UiState.Empty, viewModel.uiState.value)
        assertTrue(viewModel.groupedItemsFlow.value.isEmpty())
    }

    @Test
    fun `fetchItems sets UI state to Error when repository throws exception`() = runTest {
        coEvery { repository.getItemsFlow() } throws Exception("Network Error")

        viewModel.fetchItems()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ItemListViewModel.UiState.Error)
        assertEquals("Unexpected error. Failed to load item list.", (viewModel.uiState.value as ItemListViewModel.UiState.Error).message)
    }

    @Test
    fun `fetchItems filters out items with blank or null names`() = runTest {
        val mockItems = listOf(
            Item(id = 1, listId = 1, name = "Valid Item"),
            Item(id = 2, listId = 1, name = ""),
            Item(id = 3, listId = 1, name = null)
        )
        coEvery { repository.getItemsFlow() } returns flowOf(mockItems)

        viewModel.fetchItems()
        advanceUntilIdle()

        val filteredItems = viewModel.groupedItemsFlow.value.values.flatten()
        assertEquals(1, filteredItems.size)
        assertEquals("Valid Item", filteredItems.first().name)
    }

    @Test
    fun `fetchItems sorts items first by listId then by name`() = runTest {
        val mockItems = listOf(
            Item(id = 1, listId = 2, name = "B Item"),
            Item(id = 2, listId = 1, name = "A Item"),
            Item(id = 3, listId = 1, name = "Z Item"),
            Item(id = 4, listId = 2, name = "A Item")
        )
        coEvery { repository.getItemsFlow() } returns flowOf(mockItems)

        viewModel.fetchItems()
        advanceUntilIdle()

        val groupedItems = viewModel.groupedItemsFlow.value
        val sortedItems = groupedItems.values.flatten()

        assertEquals(listOf("A Item", "Z Item", "A Item", "B Item"), sortedItems.map { it.name })
    }
}