package ru.dev.fabled.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import ru.dev.fabled.data.remote.API_BASE_URL
import ru.dev.fabled.data.remote.GitHubApi
import ru.dev.fabled.data.remote.NetworkErrorsResolverImpl
import ru.dev.fabled.data.remote.NetworkExceptionResolver

@OptIn(ExperimentalSerializationApi::class)
val networkModule = module {
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    single<OkHttpClient> {
        if (BuildConfig.DEBUG) {
            OkHttpClient().newBuilder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
        } else {
            OkHttpClient().newBuilder()
                .build()
        }
    }

    single<Converter.Factory> {
        json.asConverterFactory("application/json".toMediaType())
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(get())
            .client(get())
            .build()
    }

    single<GitHubApi> {
        val retrofit: Retrofit = get()

        retrofit.create(GitHubApi::class.java)
    }

    single<NetworkExceptionResolver> { NetworkErrorsResolverImpl(context = get()) }
}