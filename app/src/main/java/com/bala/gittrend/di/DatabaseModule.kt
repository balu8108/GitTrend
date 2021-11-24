package com.bala.gittrend.di

import android.content.Context
import androidx.room.Room
import com.bala.gittrend.GitTrendApplication
import com.bala.gittrend.repository.AppDatabase
import com.bala.gittrend.repository.ProjectDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app.db"
        ).build()
    }

    @Provides
    fun provideMovieDao(appDatabase: AppDatabase): ProjectDao {
        return appDatabase.projectDao()
    }

    @Singleton
    @Provides
    fun provideApplicationContext(@ApplicationContext context: Context): GitTrendApplication {
        return context as GitTrendApplication
    }
}