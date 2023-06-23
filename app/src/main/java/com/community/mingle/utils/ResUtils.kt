package com.community.mingle.utils

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.fonts.Font
import android.graphics.fonts.FontFamily
import android.os.Build
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.community.mingle.MingleApplication

object ResUtils {

    fun getColor(@ColorRes id: Int): Int = ContextCompat.getColor(MingleApplication.appCtx(), id)
    fun getString(@StringRes id: Int): String = MingleApplication.appCtx().getString(id)
    fun getDrawable(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(MingleApplication.appCtx(), id)
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFont(@FontRes id: Int): Typeface = MingleApplication.appCtx().resources.getFont(id)
    fun getStringArray(@ArrayRes id: Int): Array<String> = MingleApplication.appCtx().resources.getStringArray(id)

    fun hideLayout(layoutRes: LayoutRes) {

    }
}