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
import ru.sarawan.android.BuildConfig
import ru.sarawan.android.di.annotations.ApiYandex
import ru.sarawan.android.model.datasource.api.ApiService
import ru.sarawan.android.model.datasource.api.MapApiService
import ru.sarawan.android.utils.AndroidNetworkStatus
import ru.sarawan.android.utils.MoshiCustomAdapter.Companion.LENIENT_FACTORY
import ru.sarawan.android.utils.NetworkStatus
import ru.sarawan.android.utils.exstentions.localstore.token
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    fun getNetworkStatus(context: Context): NetworkStatus = AndroidNetworkStatus(context)

    @Provides
    fun moshi(): Moshi = Moshi.Builder().add(LENIENT_FACTORY).build()

    @Provides
    @Singleton
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
    @Singleton
    fun getRetrofit(httpClient: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(httpClient)
        .build()
    @Provides
    @Singleton
    fun getApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)



    @ApiYandex
    @Provides
    fun moshiMapYandex(): Moshi = Moshi.Builder().build()

    @ApiYandex
    @Provides
    fun getHttpClientYandexMap(): OkHttpClient {
        val queryType = "json"
        val countResults = "1"
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()

            val builder = request.method(original.method, original.body)
                .url(
                    original.url.newBuilder()
                        .addQueryParameter("apikey", BuildConfig.GEOCODER_API_KEY)
                        .addQueryParameter("format", queryType)
                        .addQueryParameter("results", countResults)
                        .build()
                )
                .build()

            chain.proceed(builder)
        }
        return httpClient.build()
    }

    @ApiYandex
    @Provides
    fun getRetrofitYandexMap(
        @ApiYandex httpClient: OkHttpClient,
        @ApiYandex moshi: Moshi
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL_YANDEX_MAP)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(httpClient)
            .build()


    @ApiYandex
    @Provides
    fun getYandexApiService(@ApiYandex retrofit: Retrofit): MapApiService =
        retrofit.create(MapApiService::class.java)
}