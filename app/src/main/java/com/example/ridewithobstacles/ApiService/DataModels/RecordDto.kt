package com.example.ridewithobstacles.ApiService.DataModels

data class RecordDto(
    val scoreCollected: Int,
    val moneyCollected: Int,
    val avgScore: Float,
    val avgMoney: Float,
    var username: String
)
