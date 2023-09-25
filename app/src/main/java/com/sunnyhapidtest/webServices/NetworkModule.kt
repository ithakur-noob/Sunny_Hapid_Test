package com.sunnyhapidtest.webServices

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
//        val httpLoggingInterceptor = HttpLoggingInterceptor()
//        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//
//
//        builder.addNetworkInterceptor(httpLoggingInterceptor)
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(60, TimeUnit.SECONDS)
        builder.readTimeout(60, TimeUnit.SECONDS)
        builder.writeTimeout(60, TimeUnit.SECONDS)
        builder.addInterceptor(Interceptor { chain ->
            val request = chain.request()
            val requestBuilder = request.newBuilder()
                .header("Authorization", "YOUR_ACCESS_TOKEN")
                .header("Accept", "application/json")
            val build = requestBuilder.build()
            chain.proceed(build)
        })
        return Retrofit.Builder()
            .client(builder.build())
            .baseUrl("https://www.google.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
