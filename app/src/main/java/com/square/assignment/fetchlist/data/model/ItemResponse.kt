package com.square.assignment.fetchlist.data.model

import kotlinx.serialization.Serializable



@Serializable
data class Item(
    val id: Int,
    val listId: Int,
    val name: String? = null
)