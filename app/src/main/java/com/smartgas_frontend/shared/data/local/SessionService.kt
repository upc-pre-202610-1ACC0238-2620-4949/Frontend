package com.smartgas_frontend.shared.data.local

import android.content.Context
import com.google.gson.Gson
import com.smartgas_frontend.iam.domain.model.Account

private const val KEY = "smartgas_session_v1"

class SessionService(context: Context) {
    private val preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
    private val gson = Gson()

    private var memoryUser: Account? = null

    fun save(user: Account, persist: Boolean = true) {
        memoryUser = user

        if (persist) {
            preferences.edit().putString(KEY, gson.toJson(user)).apply()
        } else {
            preferences.edit().remove(KEY).apply()
        }
    }

    fun getCurrentUser(): Account? = memoryUser ?: try {
        preferences.getString(KEY, null)?.let { gson.fromJson(it, Account::class.java) }
    } catch (e: Exception) {
        null
    }

    fun isLoggedIn(): Boolean = getCurrentUser() != null

    fun getAccountId(): Int = getCurrentUser()?.id ?: 1

    fun clear() {
        memoryUser = null
        preferences.edit().remove(KEY).apply()
    }
}
