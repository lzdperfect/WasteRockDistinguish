package com.lzd.wasterockdistinguish

import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by lzd on 2020/3/19
 * Describe:
 */
interface PicInterface {
    @POST("{picname}")
    fun upLoadPicFile(@Path("picname")picname:String,@Body requestBody: RequestBody): Single<PicBean>
}