package com.smartgas_frontend.shared.data.local

import android.content.Context
import com.google.gson.Gson
import com.smartgas_frontend.iam.domain.model.Account

private const val KEY = "smartgas_session_v1"

class SessionService(context: Context) {
    private val preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun save(user: Account) {
        preferences.edit().putString(KEY, gson.toJson(user)).apply()
    }

    fun getCurrentUser(): Account? = try {
        preferences.getString(KEY, null)?.let { gson.fromJson(it, Account::class.java) }
    } catch (e: Exception) {
        null
    }

    fun isLoggedIn(): Boolean = getCurrentUser() != null

    fun getAccountId(): Int = getCurrentUser()?.id ?: 1

    fun clear() {
        preferences.edit().remove(KEY).apply()
    }
}
