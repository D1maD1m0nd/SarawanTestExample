package ru.sarawan.android.di.modules

import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.sarawan.android.model.datasource.ApiService
import ru.sarawan.android.service.IncomingCallReaderService
import ru.sarawan.android.service.callscreenservice.IncomingCallScreenBroadCastReceiver
import ru.sarawan.android.service.callscreenservice.IncomingCallScreenService
import ru.sarawan.android.service.contacts.ReceiveMessage
import ru.sarawan.android.utils.AndroidNetworkStatus
import ru.sarawan.android.utils.MoshiCustomAdapter.Companion.LENIENT_FACTORY
import ru.sarawan.android.utils.NetworkStatus
import ru.sarawan.android.utils.exstentions.localstore.token
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    fun getNetworkStatus(context: Context): NetworkStatus = AndroidNetworkStatus(context)

    // TODO: Убрать в отдельный модуль 
    @Provides
    fun provideIncomingCallBroadCastReceiver(context: Context): ReceiveMessage {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            val receiver = IncomingCallReaderService()
            val intentFilter = IntentFilter()
            intentFilter.addAction("android.intent.action.PHONE_STATE")
            context.registerReceiver(receiver, intentFilter)
            return receiver
        } else {
            val receiver = IncomingCallScreenBroadCastReceiver()
            val intentFilter = IntentFilter()
            intentFilter.addAction(IncomingCallScreenService.INTENT_INCOMING_CALL_ACTION)
            context.registerReceiver(receiver, intentFilter)
            return receiver
        }
    }

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
}