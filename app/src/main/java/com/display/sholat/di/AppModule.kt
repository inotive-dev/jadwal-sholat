package com.display.sholat.di

import android.app.Application
import com.display.sholat.App
import com.display.sholat.data.Preference
import com.display.sholat.data.api.ApiPrayerService
import com.display.sholat.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(
    includes = [ViewModelModule::class]
)
class AppModule {

    @Singleton
    @Provides
    fun provideApiPrayerService() = Retrofit.Builder()
        .baseUrl("http://jadwal-sholat.inotive.id/api/")
        .client(OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).apply { level = HttpLoggingInterceptor.Level.BODY })
            .build())
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(ApiPrayerService::class.java)

    @Singleton
    @Provides
    fun provideAppDataBase(app: Application) = AppDatabase.newInstance(app)

    @Singleton
    @Provides
    fun providePreference(app: Application) = Preference(app).also {
        App.setTimeZoneId(it.timeZoneId)
    }
}