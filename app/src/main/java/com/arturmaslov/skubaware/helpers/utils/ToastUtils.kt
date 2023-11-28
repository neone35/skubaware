package com.arturmaslov.skubaware.helpers.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {

    private var mainToast: Toast? = null

    fun updateShort(ctx: Context?, text: String) {
        mainToast = null
        mainToast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT)
        mainToast?.show()
    }

    fun updateLong(ctx: Context?, text: String) {
        mainToast = null
        mainToast = Toast.makeText(ctx, text, Toast.LENGTH_LONG)
        mainToast?.show()
    }
}