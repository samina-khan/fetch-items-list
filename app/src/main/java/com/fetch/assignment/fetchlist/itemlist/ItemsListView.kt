package com.fetch.assignment.fetchlist.itemlist


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.fetch.assignment.fetchlist.data.model.Item
import com.fetch.assignment.fetchlist.itemlist.ItemListViewModel.UiState

@Composable
fun ItemListView(navController: NavController, viewModel: ItemListViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val groupedItems by viewModel.groupedItemsFlow.collectAsState()
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { viewModel.fetchItems() }
    ){
        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            when (uiState) {
                is UiState.Loading -> item {
                    Text(text = "Loading...", modifier = Modifier.padding(16.dp))
                }
                is UiState.Error -> item {
                    Text(text = (uiState as UiState.Error).message, modifier = Modifier.padding(16.dp))
                }
                is UiState.Empty -> item {
                    Text(text = "No items found.", modifier = Modifier.padding(16.dp))
                }
                is UiState.Success -> {
                    groupedItems.forEach { (listId, items) ->
                        item {
                            CollapsibleGroup(
                                title = "List ID: $listId",
                                items = items,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun CollapsibleGroup(title: String, items: List<Item>, navController: NavController) {
    var isExpanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Expand/Collapse"
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))


        if (isExpanded) {
            items.forEach { item ->
                ItemInfo(item) { itemId ->
                    navController.navigate("details/$itemId")
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
                text = "${item.name}",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "ID: ${item.id}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
}