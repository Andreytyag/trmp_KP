package com.example.ridewithobstacles.Database.Models

import java.io.Serializable

data class CarModel(
    val id: Int,
    var carName: String,
    val price: Int,
    var buyed: Boolean,
    var choosed: Boolean
) : Serializable