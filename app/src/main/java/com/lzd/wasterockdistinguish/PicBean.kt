package com.lzd.wasterockdistinguish

/**
 * Created by lzd on 2020/3/19
 * Describe:图片回调结果
 */
data class PicBean(
    val coordinates: String,
    val error: String,
    val image_name: String,
    val status_code: Int
)