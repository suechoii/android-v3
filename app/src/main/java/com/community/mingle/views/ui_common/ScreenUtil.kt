package com.community.mingle.views.ui_common

import android.content.Context
import android.content.res.Configuration

object ScreenUtil {

    fun getStatusBarHeight(context: Context): Int {
        val screenSizeType = context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK
        var statusbar = 0
        if (screenSizeType != Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                statusbar = context.resources.getDimensionPixelSize(resourceId)
            }
        }
        return statusbar
    }
}