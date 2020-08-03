package com.kishorejethava.cameraxcropping.utils

import android.app.Dialog
import android.content.Context
import com.kishorejethava.cameraxcropping.R


object ProgressDialogUtils {

    private var progressDialog: Dialog? = null

    val isShowing: Boolean
        get() = progressDialog != null

    fun showProgressDialog(context: Context) {
        if (progressDialog == null || !progressDialog!!.isShowing) {
            progressDialog = Dialog(context, R.style.Base_Theme_AppCompat_Dialog)
            progressDialog!!.setCancelable(false)
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
            progressDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            progressDialog!!.setContentView(R.layout.layout_progress_dialog)
        }
    }

    fun closeProgressDialog() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
                progressDialog = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            progressDialog = null
        }
    }

}
