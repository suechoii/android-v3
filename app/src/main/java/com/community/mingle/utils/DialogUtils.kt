package com.community.mingle.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.view.ContextThemeWrapper
import android.view.Window
import android.widget.TextView
import com.community.mingle.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object DialogUtils {

    fun showYesNoDialog(
        context: Context,
        message: String,
        onPositiveClick: DialogInterface.OnClickListener,
        onNegativeClick: DialogInterface.OnClickListener
    ) {
        val cw = ContextThemeWrapper(context, R.style.AlertDialogTheme)
        val alertDialog = AlertDialog.Builder(cw)
            .setMessage(message)
            .setPositiveButton("예", onPositiveClick)
            .setNegativeButton("아니오", onNegativeClick)
            .show()
        //This code should be always after alertDialog.show() function

        val tv = alertDialog.findViewById<TextView>(android.R.id.message)
        tv.setTypeface(
            Typeface.createFromAsset(context.assets, "pretendard_regular.otf")
        )
    }

    fun showYesDialog(context: Context, message: String) {
        AlertDialog.Builder(context).apply {
            setMessage(message)
            setNegativeButton("예", null)
            show()
        }
    }

    fun showCustomOneTextDialog(context: Context, message: String, buttonText: String) {
        val cw = ContextThemeWrapper(context, R.style.AlertDialogTheme)
        AlertDialog.Builder(cw).apply {
            setMessage(message)
            setNegativeButton(buttonText, null)
            show()
        }
    }

    fun showAutoCloseDialog(context: Context, message: String) {
        AlertDialog.Builder(context).create().apply {
            setMessage(message)
            CoroutineScope(Dispatchers.Main).launch {
                show()
                delay(500)
                dismiss()
            }
        }
    }
}