package com.lzd.network.utils

import com.lzd.network.constants.Config
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by lzd on 2020/3/2
 * Describe:
 */
 open class HttpUtils {
    companion object{
        private val client:OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(Config.DEFAULT_TIMEOUT,TimeUnit.SECONDS)
            .readTimeout(Config.DEFAULT_TIMEOUT,TimeUnit.SECONDS)
            .writeTimeout(Config.DEFAULT_TIMEOUT,TimeUnit.SECONDS)
            .build()

        private var httpUtils: HttpUtils? = null
        private var retrofit: Retrofit? = null

        /**
         * 单例模式
         */
        fun getInstance(): HttpUtils? {
            if (httpUtils == null){
                synchronized(HttpUtils){
                    httpUtils =
                        HttpUtils()
                }
            }
            return httpUtils
        }
    }

    fun getRetrofit(baseUrl:String):Retrofit?{
        if (retrofit == null){
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
        return retrofit
    }
}