package com.community.mingle.di

import com.community.mingle.api.*
import com.community.mingle.service.repository.AuthRepository
import com.google.gson.GsonBuilder
//import com.yuuuzzzin.offoff_android.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/* 네트워크 통신을 위한 모듈 */

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule  {

    private const val BASE_URL = "https://dev.api.mingle.community/"

    /* Retrofit2 통신 모듈 */
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls().setLenient().create()
                )
            )
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        getAuthRepository: AuthRepository
    ): AuthInterceptor {
        return AuthInterceptor(getAuthRepository)
    }

    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Singleton
    @Provides
    fun okHttpClient( authInterceptor: AuthInterceptor): OkHttpClient {
        //ADD DISPATCHER WITH MAX REQUEST TO 1
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1

        return OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .addInterceptor(authInterceptor)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(logger)
                .build()
    }

    /* 회원 관련 API 를 위한 모듈 */
    @Singleton
    @Provides
    fun provideMemberService(retrofit: Retrofit): MemberService {
        return retrofit.create(MemberService::class.java)
    }

//    lateinit var authService : AuthService
//    /* refresh API 를 위한 모듈 */
//    @Singleton
//    @Provides
//    fun provideAuthService(retrofit: Retrofit): AuthService {
//        authService = retrofit.create(AuthService::class.java)
//        return authService
//    }
//
//    fun getRefreshService() : AuthService{
//        return authService
//    }

    /* 홈 관련 API 를 위한 모듈 */
    @Singleton
    @Provides
    fun provideHomeService(retrofit: Retrofit): HomeService {
        return retrofit.create(HomeService::class.java)
    }

    /* 게시물 관련 API 를 위한 모듈 */
    @Singleton
    @Provides
    fun providePostService(retrofit: Retrofit): PostService {
        return retrofit.create(PostService::class.java)
    }

    /* 광장 잔디밭 게시물 리스트 관련 API 를 위한 모듈 */
    @Singleton
    @Provides
    fun provideUnivTotalService(retrofit: Retrofit): UnivTotalService {
        return retrofit.create(UnivTotalService::class.java)
    }

    /* 마이 페이지 관련 API를 위한 모듈 */
    @Singleton
    @Provides
    fun provideMyPageService(retrofit: Retrofit): MyPageService {
        return retrofit.create(MyPageService::class.java)
    }

    /* 중고거래 관련 API를 위한 모듈 */
    @Singleton
    @Provides
    fun provideMarketService(retrofit: Retrofit): MarketService {
        return retrofit.create(MarketService::class.java)
    }

    private lateinit var refreshService : AuthService
    private val okHttp = OkHttpClient.Builder().addInterceptor(logger)

    @Singleton
    @Provides
    fun getRefreshService(): AuthService {

        if (!::refreshService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttp.build())
                .build()

            refreshService = retrofit.create(AuthService::class.java)
        }

        return refreshService
    }
}