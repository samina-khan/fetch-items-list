package com.fetch.assignment.fetchlist.data.api

import android.util.Log
import com.fetch.assignment.fetchlist.data.model.Item
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import java.net.HttpURLConnection


class ItemApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ItemApiService

    @OptIn(ExperimentalSerializationApi::class)
    @Before
    fun setup() {
        mockWebServer = MockWebServer()

        val json = Json {
            ignoreUnknownKeys = false
            isLenient = false
            coerceInputValues = false
            encodeDefaults = true
        }

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ItemApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getItems returns expected data`() = runBlocking {
        val mockItems = listOf(
            Item(id = 1, listId = 1, name = "Item A"),
            Item(id = 2, listId = 2, name = "Item B")
        )
        val mockResponse = Json.encodeToString(mockItems)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(mockResponse)
        )

        val response = apiService.getItems()
        assertEquals(mockItems, response)
    }

    @Test
    fun `getItems returns empty list when response is empty`() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody("[]")
        )

        val response = apiService.getItems()
        assertEquals(emptyList<Item>(), response)
    }

    @Test
    fun `getItems handles HTTP 404 error correctly`(): Unit = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
        )

        try {
            apiService.getItems()
        } catch (e: Exception) {
            assert(e is HttpException)
            assertEquals(404, (e as HttpException).code())
        }
    }

    @Test
    fun `getItems handles malformed JSON gracefully`(): Unit = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody("{invalid_json}")
        )

        try {
            apiService.getItems()
        } catch (e: Exception) {
            assert(e is SerializationException)
        }
    }
}