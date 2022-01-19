package ru.sarawan.android.di.modules

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.sarawan.android.model.datasource.ApiService
import ru.sarawan.android.utils.AndroidNetworkStatus
import ru.sarawan.android.utils.MoshiCustomAdapter
import ru.sarawan.android.utils.NetworkStatus
import ru.sarawan.android.utils.exstentions.token

@Module
class NetworkModule {

    @Provides
    fun getNetworkStatus(context: Context): NetworkStatus = AndroidNetworkStatus(context)

    @Provides
    fun moshi(): Moshi = Moshi.Builder().build()

    @Provides
    fun getHttpClient(shared: SharedPreferences): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
            val token = shared.token
            if (!token.isNullOrEmpty()) {
                request.header("Authorization", "Token $token")
            }
            val builder = request.method(original.method, original.body)
                .build()

            chain.proceed(builder)
        }
        return httpClient.build()
    }
    @Provides
    fun getMoshiCustomAdapter(moshi : Moshi) : MoshiCustomAdapter = MoshiCustomAdapter(moshi)

    @Provides
    fun getRetrofit(httpClient: OkHttpClient,moshi : Moshi): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(httpClient)
        .build()

    @Provides
    fun getApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    companion object {
        private const val BASE_URL = "https://dev.sarawan.ru/"
    }
}