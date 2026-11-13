package com.smartgas_frontend

import android.app.Application

class SmartGasApplication : Application() {
    companion object {
        lateinit var instance: SmartGasApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
