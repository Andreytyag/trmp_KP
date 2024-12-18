package com.example.ridewithobstacles.ApiService.DataModels

data class LoginResponse(
    val token: String,
    val success: Boolean,
    val message: String
)