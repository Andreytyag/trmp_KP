package com.example.ridewithobstacles.Database.Models

import java.io.Serializable

data class UserModel(
    val id: Int,
    var username: String,
    var money: Int,
    var car: CarModel,
    var password: String,
    var token: String,
    var synced: Boolean
) : Serializable