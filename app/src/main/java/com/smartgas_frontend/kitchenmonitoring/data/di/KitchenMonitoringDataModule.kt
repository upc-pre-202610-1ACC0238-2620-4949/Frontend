package com.smartgas_frontend.kitchenmonitoring.data.di

import com.smartgas_frontend.kitchenmonitoring.data.remote.MonitoringService
import com.smartgas_frontend.kitchenmonitoring.data.remote.SensorService
import com.smartgas_frontend.kitchenmonitoring.data.repository.MonitoringRepository
import com.smartgas_frontend.kitchenmonitoring.data.repository.SensorRepository
import com.smartgas_frontend.paymentmanagement.data.remote.SubscriptionService
import com.smartgas_frontend.shared.data.di.SharedDataModule

object KitchenMonitoringDataModule {

    fun getSensorService(): SensorService {
        return SharedDataModule.getRetrofit().create(SensorService::class.java)
    }

    fun getMonitoringService(): MonitoringService {
        return SharedDataModule.getRetrofit().create(MonitoringService::class.java)
    }

    fun getSensorRepository(): SensorRepository {
        return SensorRepository(
            getSensorService(),
            getMonitoringService(),
            SharedDataModule.getRetrofit().create(SubscriptionService::class.java)
        )
    }

    fun getMonitoringRepository(): MonitoringRepository {
        return MonitoringRepository(
            getMonitoringService(),
            getSensorService(),
            SharedDataModule.getRetrofit().create(SubscriptionService::class.java)
        )
    }
}
