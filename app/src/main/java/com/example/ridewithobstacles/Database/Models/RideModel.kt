package com.example.ridewithobstacles.Database.Models

data class RideModel(
    var id: Int,
    var score: Int,
    var moneyCollected: Int,
    val user_id: Int,
    var synchred: Boolean
)