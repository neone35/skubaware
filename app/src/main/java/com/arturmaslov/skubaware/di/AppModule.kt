package com.arturmaslov.skubaware.di

import android.content.Context
import com.arturmaslov.skubaware.BuildConfig
import com.arturmaslov.skubaware.data.source.remote.Api
import com.arturmaslov.skubaware.data.source.remote.ApiService
import com.arturmaslov.tgnba.utils.Constants
import com.arturmaslov.tgnba.utils.NetworkChecker
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

val appModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }
    single { provideNetworkChecker(androidContext()) }
    single { Api(get()) }
}

private fun provideNetworkChecker(context: Context) = NetworkChecker(context)

private fun provideApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)

private fun provideRetrofit(
    okHttpClient: OkHttpClient
): Retrofit {
    val baseUrl = Constants.BASE_URL
    Timber.d("baseUrl is $baseUrl")

    val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
    val retrofit: Retrofit = if (BuildConfig.DEBUG) {
        retrofitBuilder.client(okHttpClient)
        retrofitBuilder.build()
    } else {
        retrofitBuilder.build()
    }
    return retrofit
}

private fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
} else OkHttpClient
    .Builder()
    .build()