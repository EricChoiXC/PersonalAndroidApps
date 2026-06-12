package com.ericxc.android.accounting.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.ericxc.android.accounting.AccountingApp

object UiUtils {

    fun showToast(@StringRes resId: Int) {
        showToast(AccountingApp.instance.getString(resId))
    }

    fun showToast(msg: String) {
        Toast.makeText(AccountingApp.instance, msg, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(@StringRes resId: Int) {
        showLongToast(AccountingApp.instance.getString(resId))
    }

    fun showLongToast(msg: String) {
        Toast.makeText(AccountingApp.instance, msg, Toast.LENGTH_LONG).show()
    }

    fun showSnackbar(view: android.view.View, @StringRes resId: Int) {
        Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show()
    }

    fun showSnackbar(view: android.view.View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }

    fun showSnackbarLong(view: android.view.View, @StringRes resId: Int) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).show()
    }

    fun showSnackbarLong(view: android.view.View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
    }
}
