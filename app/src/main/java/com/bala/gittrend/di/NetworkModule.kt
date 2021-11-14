package com.bala.gittrend.di

import android.content.Context
import com.bala.gittrend.GitTrendApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun provideApplicationContext(@ApplicationContext context: Context): GitTrendApplication {
        return context as GitTrendApplication
    }

    companion object {
        var BASE_URL =
            "https://api.github.com/search/"
    }
}