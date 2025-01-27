package com.square.assignment.fetchlist.itemlist


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.square.assignment.fetchlist.data.model.Item
import com.square.assignment.fetchlist.data.repository.ItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ItemListViewModel @Inject constructor(
    private val repository: ItemsRepository,
    private val dispatcher: CoroutineDispatcher

) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> get() = _uiState

    private val _itemsFlow = MutableStateFlow<List<Item>>(emptyList())
    val itemsFlow: StateFlow<List<Item>> get() = _itemsFlow

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        fetchItems()
    }

    fun fetchItems() {
        viewModelScope.launch(dispatcher) {
            _isRefreshing.value = true
            _uiState.value = UiState.Loading
            try {
                repository.getItemsFlow().collect { items ->
                    _itemsFlow.value = items
                    _uiState.value = if (items.isEmpty()) UiState.Empty
                    else UiState.Success
                }
            } catch (e: Exception) {
                _itemsFlow.value = emptyList()
                _uiState.value = UiState.Error("Unexpected error. Failed to load item list.")
            }
            _isRefreshing.value = false
        }
    }


    sealed class UiState {
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
        object Empty : UiState()
    }
}