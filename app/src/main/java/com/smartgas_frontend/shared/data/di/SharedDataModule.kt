package com.smartgas_frontend.shared.data.di

import com.smartgas_frontend.SmartGasApplication
import com.smartgas_frontend.common.Constants.BASE_URL
import com.smartgas_frontend.shared.data.local.AppPreferences
import com.smartgas_frontend.shared.data.local.SessionService
import com.smartgas_frontend.shared.data.remote.ExternalWeatherService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SharedDataModule {
    private val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val sessionServiceInstance: SessionService by lazy {
        SessionService(SmartGasApplication.instance.applicationContext)
    }

    private val appPreferencesInstance: AppPreferences by lazy {
        AppPreferences(SmartGasApplication.instance.applicationContext)
    }

    fun getRetrofit(): Retrofit = retrofitInstance

    fun getSessionService(): SessionService = sessionServiceInstance

    fun getAppPreferences(): AppPreferences = appPreferencesInstance

    fun getExternalWeatherService(): ExternalWeatherService {
        return getRetrofit().create(ExternalWeatherService::class.java)
    }
}
