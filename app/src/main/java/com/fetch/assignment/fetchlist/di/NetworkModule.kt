package com.fetch.assignment.fetchlist.data.api


import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@OptIn(ExperimentalSerializationApi::class)
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://fetch-hiring.s3.amazonaws.com/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val json = Json {
            ignoreUnknownKeys = false
            isLenient = false
            coerceInputValues = false
            encodeDefaults = true
        }

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
            .build()
    }

    @Provides
    @Singleton
    fun provideEmployeeApiService(retrofit: Retrofit): ItemApiService {
        return retrofit.create(ItemApiService::class.java)
    }
}