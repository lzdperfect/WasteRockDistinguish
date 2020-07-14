package com.lzd.network.utils

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.lzd.network.R
import java.util.regex.Pattern

/**
 * Created by lzd on 2020/3/3
 * Describe: 基础工具类
 */
class AppUtils {
    companion object{
        /**
         * 消息提示
         */
        fun showMsgWithToast(activity : Context, msg:String){
            Toast.makeText(activity,msg, Toast.LENGTH_SHORT).show()
        }
        /**
         * 消息提示
         */
        fun showMsgWithSnackbar(activity : Context, msg:String){
            (activity as Activity).window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            //带按钮的Snackbar
//            Snackbar.make((activity as Activity).window.decorView,"Snackbar with action",Snackbar.LENGTH_SHORT)
//                .setAction("Action") {
                    //纯文本的Snackbar
                    Snackbar.make((activity as Activity).window.decorView, msg, Snackbar.LENGTH_SHORT).show()
//                }.show()
        }
        /**
         * 进入activity动画
         *
         * @param activity
         */
        fun activityDrawingEnter(activity: Context) {
            (activity as Activity).overridePendingTransition(
                R.anim.in_from_right,
                R.anim.out_from_left
            )
        }

        /**
         * 退出activity动画
         * @param activity
         */
        fun activityDrawingExit(activity: Context) {
            (activity as Activity).overridePendingTransition(
                R.anim.in_from_left,
                R.anim.out_from_right
            )
        }

        /**
         * 防止连续点击
         * @return
         */
        private const val DELAY = 1000
        private var lastClickTime: Long = 0
        fun isNotFastClick(): Boolean {
            val currentTime = System.currentTimeMillis()
            return if (currentTime - lastClickTime > DELAY) {
                lastClickTime = currentTime
                true
            } else {
                false
            }
        }


    }
}