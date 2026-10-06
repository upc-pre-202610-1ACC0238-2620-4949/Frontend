package com.smartgas_frontend.shared.data.remote

import android.util.Log
import com.smartgas_frontend.common.Constants
import com.smartgas_frontend.common.Resource
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    val retrofit: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}

const val API_ERROR = "API_ERROR"

class DomainException(val code: String) : Exception(code)

fun <T> Response<T>.bodyOrThrow(): T {
    if (isSuccessful) {
        body()?.let { return it }
    }
    throw HttpException(this)
}

fun <T> Response<T>.bodyOrNull(): T? = if (isSuccessful) body() else null

fun Response<*>.errorMessage(): String = try {
    errorBody()?.string().orEmpty()
} catch (e: Exception) {
    ""
}

suspend fun <T> safeCall(block: suspend () -> T): Resource<T> = try {
    Resource.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: DomainException) {
    Resource.Error(e.code)
} catch (e: Exception) {
    Log.e("SmartGasApi", "API call failed", e)
    Resource.Error(API_ERROR)
}
