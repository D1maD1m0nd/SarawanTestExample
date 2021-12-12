package com.example.sarawan.di.modules

import android.content.Context
import android.content.SharedPreferences
import com.example.sarawan.app.App
import com.example.sarawan.model.datasource.ApiService
import com.example.sarawan.utils.AndroidNetworkStatus
import com.example.sarawan.utils.NetworkStatus
import com.example.sarawan.utils.exstentions.token
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
class NetworkModule {

    @Provides
    fun getNetworkStatus(context: Context): NetworkStatus = AndroidNetworkStatus(context)

    @Provides
    fun getHttpClient(shared: SharedPreferences): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
            val token = shared.token ?: ""
            if(token.isNotEmpty()) {
                request.header("Authorization", "Token $token")
            }
            val builder = request.method(original.method, original.body)
                .build()

            chain.proceed(builder)
        }
        return httpClient.build()
    }

    @Provides
    fun getRetrofit(httpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(httpClient)
        .build()

    @Provides
    fun getApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    companion object {
        private const val BASE_URL = "https://dev.sarawan.ru/"
    }
}