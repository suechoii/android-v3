package com.community.mingle.di

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.community.mingle.MingleApplication
import com.community.mingle.di.NetworkModule.getRefreshService
import com.community.mingle.service.models.AuthResponse
import com.community.mingle.service.models.Email
import com.community.mingle.service.repository.AuthRepository
import com.community.mingle.service.repository.HomeRepository
import com.community.mingle.utils.Event
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor
@Inject
constructor(
    private val authRepository: AuthRepository
) :
    Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response  {
        val originalRequest = chain.request()
        val originalBuilder = originalRequest.newBuilder()
        if (MingleApplication.pref.accessToken != null) {
            originalBuilder.header("Authorization", MingleApplication.pref.accessToken.toString())
        }

        val originalResponse = chain.proceed(originalBuilder.build())

        var newRequest: Request? = null

        if (originalResponse.code == 403) {
            val email = MingleApplication.pref.email
            val refreshToken = MingleApplication.pref.refreshToken
            runBlocking {
                    authRepository.refresh(Email(email.toString()), refreshToken.toString())
                        .let { response ->
                            if (response.isSuccessful && response.body()!!.code == 1000) {
                                MingleApplication.pref.accessToken =
                                    response.body()!!.result.accessToken
                                MingleApplication.pref.refreshToken =
                                    response.body()!!.result.refreshToken
                                newRequest = originalRequest.newBuilder()
                                    .header(
                                        "Authorization",
                                        MingleApplication.pref.accessToken.toString()
                                    )
                                    .build()
                            } else {
                                MingleApplication.pref.refreshToken = null
                                MingleApplication.pref.accessToken = null
                            }
                        }
                }
            }

        return if (newRequest != null) {
            chain.proceed(newRequest!!)
        } else
            originalResponse
    }

}