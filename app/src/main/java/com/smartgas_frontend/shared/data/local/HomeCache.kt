package com.smartgas_frontend.shared.data.local

import android.content.Context
import com.google.gson.Gson
import com.smartgas_frontend.shared.domain.model.HomeSummary
import com.smartgas_frontend.shared.domain.model.Weather
import com.smartgas_frontend.shared.utils.nowISO

private const val FILE = "smartgas_home_cache"
private const val SUMMARY_KEY = "summary"
private const val WEATHER_KEY = "weather"
private const val SAVED_AT_KEY = "saved_at"

class HomeCache(context: Context) {
    private val preferences = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveSummary(summary: HomeSummary) {
        preferences.edit()
            .putString(SUMMARY_KEY, gson.toJson(summary.copy(offlineSavedAt = null)))
            .putString(SAVED_AT_KEY, nowISO())
            .apply()
    }

    fun loadSummary(): HomeSummary? = try {
        preferences.getString(SUMMARY_KEY, null)
            ?.let { gson.fromJson(it, HomeSummary::class.java) }
            ?.copy(offlineSavedAt = preferences.getString(SAVED_AT_KEY, null))
    } catch (e: Exception) {
        null
    }

    fun saveWeather(weather: Weather) {
        preferences.edit().putString(WEATHER_KEY, gson.toJson(weather)).apply()
    }

    fun loadWeather(): Weather? = try {
        preferences.getString(WEATHER_KEY, null)?.let { gson.fromJson(it, Weather::class.java) }
    } catch (e: Exception) {
        null
    }

    fun clear() {
        preferences.edit().clear().apply()
    }
}
