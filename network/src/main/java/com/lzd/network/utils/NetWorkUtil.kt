package com.lzd.network.utils

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by lzd on 2020/3/2
 * Describe:
 */
open class NetWorkUtil {
    /**
     * 单例模式
     */
    companion object {
        private var mInstance: NetWorkUtil?= null
        private val LOCKER = ByteArray(0)

        open fun getInstance(): NetWorkUtil? {
            if (mInstance == null){
                synchronized(LOCKER){
                    mInstance =
                        NetWorkUtil()
                }
            }
            return mInstance
        }
    }

    /**
     * 插入观察者
     * @param observable
     * @param observer
     * @param <T>
     */
    open fun <T> setSubscribe(observable: Single<T>, observer: SingleObserver<T>){
        observable.subscribeOn(Schedulers.io())
            .subscribeOn(Schedulers.newThread())//子线程访问网络
            .observeOn(AndroidSchedulers.mainThread())//回调主线程
            .subscribe(observer)
    }
}