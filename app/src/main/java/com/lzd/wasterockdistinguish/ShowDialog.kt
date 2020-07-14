package com.lzd.wasterockdistinguish

import android.content.Context
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE

/**
 * Created by lzd on 2020/3/19
 * Describe: loading
 */
class ShowDialog {
    companion object{
        private var showDialog: ShowDialog? = null
        private var dialog: ZLoadingDialog? = null
        /**
         * 单例封装
         * @return
         */
        open fun getInstance(): ShowDialog? {
            if (showDialog == null) {
                synchronized(ShowDialog::class.java) {
                    if (showDialog == null) {
                        showDialog =
                            ShowDialog()
                    }
                }
            }
            return showDialog
        }
    }

    /**
     */
    fun getDialog(context: Context) {
        if (dialog == null) {
            dialog = ZLoadingDialog(context)
        }
        dialog!!.setLoadingBuilder(Z_TYPE.CIRCLE) //设置dialog类型
            .setDialogBackgroundColor(context.resources.getColor(R.color.hyaline))
            .setHintText("loading")
            .setHintTextColor(context.resources.getColor(R.color.colorPrimary,null))
            .setLoadingColor(context.resources.getColor(R.color.colorPrimary,null))
            .setHintTextSize(25f)
            .setCancelable(false)
            .setCanceledOnTouchOutside(false)
            .show()
    }

    /**
     * 关闭dialog
     */
    fun dismissDialog() {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
    }
}