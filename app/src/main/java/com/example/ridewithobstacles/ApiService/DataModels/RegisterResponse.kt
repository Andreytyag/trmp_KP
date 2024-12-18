package com.example.ridewithobstacles.ApiService.DataModels

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val token: String
)