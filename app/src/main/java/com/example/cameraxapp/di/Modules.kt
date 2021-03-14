package com.example.cameraxapp.di

import android.content.Context
import com.example.cameraxapp.data.LocalDataSource
import com.example.cameraxapp.framework.ImageAnalyzer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Singleton

/*
package com.example.cameraxapp.di

import com.example.cameraxapp.data.LocalDataSource
import com.example.cameraxapp.domain.UseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCasesModules {
    @Provides
    @Singleton
    fun provideUseCases(
        dataSource: LocalDataSource
    ): UseCases = UseCases(dataSource)
}

@Module
@InstallIn(SingletonComponent::class)
object LocalDataSourceModules {
    @Provides
    @Singleton
    fun provideLocalDataSource(): LocalDataSource = LocalDataSource()
}*/

@Module
@InstallIn(ActivityComponent::class)
object ImageAnalyzerModule {
    @Provides
    fun provideImageAnalyzer(
        @ActivityContext context: Context,
        localDataSource: LocalDataSource
    ): ImageAnalyzer {
        return ImageAnalyzer(context, localDataSource)
    }
}