package com.community.mingle

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.community.mingle.service.models.OldUser
import com.community.mingle.service.SharedPreferenceController
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MingleApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        pref = SharedPreferenceController(applicationContext)

        // 다크모드 비활성화
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }

    init {
        instance = this
    }

    companion object {
        private lateinit var instance: MingleApplication
        lateinit var pref: SharedPreferenceController

        fun appCtx(): Context {
            return instance.applicationContext
        }
    }
}