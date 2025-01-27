package com.square.assignment.fetchlist.itemlist


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.square.assignment.fetchlist.data.model.Item
import com.square.assignment.fetchlist.itemlist.ItemListViewModel.UiState

@Composable
fun ItemListView(navController: NavController, viewModel: ItemListViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val items by viewModel.itemsFlow.collectAsState()

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { viewModel.fetchItems() }
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            when (uiState) {
                is UiState.Loading -> item {

                }
                is UiState.Error -> item {
                    Text(text = (uiState as UiState.Error).message, modifier = Modifier.padding(16.dp))
                }
                is UiState.Empty -> item {
                    Text(text = "No items found.", modifier = Modifier.padding(16.dp))
                }
                is UiState.Success -> {
                    if (items.isNotEmpty()) {
                        items(items) { item ->
                            ItemInfo(item) { itemId ->
                                navController.navigate("details/$itemId")
                            }
                        }
                    } else {
                        item {
                            Text(text = "No items found.", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun ItemInfo(item: Item, onItemClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column (modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = "ID: ${item.id}",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "List ID: ${item.listId}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
    HorizontalDivider(
        color = androidx.compose.ui.graphics.Color.LightGray
    )
}