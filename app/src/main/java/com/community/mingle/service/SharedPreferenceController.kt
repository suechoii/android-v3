package com.community.mingle.service

import android.content.Context
import android.content.SharedPreferences
import com.community.mingle.R
import com.community.mingle.service.models.OldUser

class SharedPreferenceController(context: Context) {

    private val pref: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    var fcmToken: String?
        get() = pref.getString("fcmToken",null)
        set(value) {
            pref.edit().putString("fcmToken",value).apply()
        }

    var accessToken: String?
        get() = pref.getString("access_token", null)
        set(value) {
            pref.edit().putString("access_token", value).apply()
        }

    var refreshToken: String?
        get() = pref.getString("refresh_token", null)
        set(value) {
            pref.edit().putString("refresh_token", value).apply()
        }

    /* 로그인 했을때 이메일 값 저장 */
    var email: String?
        get() = pref.getString("email", null)
        set(value) {
            pref.edit().putString("email", value).apply()
        }

    var nickname: String?
        get() = pref.getString("nickname", null)
        set(value) {
            pref.edit().putString("nickname", value).apply()
        }

    var univName: String?
        get() = pref.getString("univName", null)
        set(value) {
            pref.edit().putString("univName", value).apply()
        }

    var isRefreshing: Boolean
        get() = pref.getBoolean("isRefreshing",true)
        set(value) {
            pref.edit().putBoolean("isRefreshing",value).apply()
        }

    var isBlind: Boolean
        get() = pref.getBoolean("isBlind",false)
        set(value) {
            pref.edit().putBoolean("isBlind",value).apply()
        }

    var isUpdate: Boolean
        get() = pref.getBoolean("isUpdate",false)
        set(value) {
            pref.edit().putBoolean("isUpdate",value).apply()
        }

    var keyword: String?
        get() = pref.getString("keyword", null)
        set(value) {
            pref.edit().putString("keyword", value).apply()
        }

    fun deleteToken() {
        val edit = pref.edit()
        edit.clear()
        edit.apply()
    }
}