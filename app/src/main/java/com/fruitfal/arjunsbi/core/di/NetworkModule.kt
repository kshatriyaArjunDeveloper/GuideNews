package com.fruitfal.arjunsbi.core.di

import com.fruitfal.arjunsbi.data.retrofit.API
import com.fruitfal.arjunsbi.data.retrofit.RetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideApi(): API = RetrofitService.getRetrofitInstance().create(API::class.java)
}

