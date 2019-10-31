package com.gligent.exchange.di.module

import android.app.Application
import com.gligent.exchange.ApiCurrency
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetModule(private val server: String) {

    @Provides
    @Singleton
    internal fun provideOkHttpCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .disableHtmlEscaping()
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val clientBuilder = OkHttpClient().newBuilder()
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        clientBuilder.addInterceptor(logging)
        clientBuilder.cache(cache)
        clientBuilder.connectTimeout(30, TimeUnit.SECONDS)
        clientBuilder.readTimeout(0, TimeUnit.SECONDS)
        return clientBuilder.build()
    }

    @Provides
    @Singleton
    internal fun provideApiCurrency(gson: Gson, okHttpClient: OkHttpClient): ApiCurrency {
        return Retrofit.Builder()
            .baseUrl(server)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(ApiCurrency::class.java)
    }
}