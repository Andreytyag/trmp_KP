package com.example.ridewithobstacles.Shop

import android.graphics.Bitmap
import android.media.Image

data class CarElement(
    val id: Int,
    val name: String,
    val image: Bitmap,
    val price: Int,
    var isBuyed: Boolean,
    var isChoosed: Boolean
)

