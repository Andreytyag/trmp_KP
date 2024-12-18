package com.example.ridewithobstacles.ApiService.Interfaces

import com.example.ridewithobstacles.ApiService.DataModels.LoginRequest
import com.example.ridewithobstacles.ApiService.DataModels.LoginResponse
import com.example.ridewithobstacles.ApiService.DataModels.RegisterRequest
import com.example.ridewithobstacles.ApiService.DataModels.RegisterResponse
import com.example.ridewithobstacles.ApiService.DataModels.RecordDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header

interface ApiService {

    @POST("login")
    suspend fun login(@Body credentials: LoginRequest): LoginResponse

    @POST("register")
    suspend fun register(@Body registrationRequest: RegisterRequest): RegisterResponse

    @POST("syncRecord")
    suspend fun sendRecord(
        @Header("Authorization") token: String,
        @Body rides: RecordDto
    ): Response<Boolean>

    @GET("scoreRecords")
    suspend fun getRecordsByScore(@Header("Authorization") token: String): Response<List<RecordDto>>

    @GET("moneyRecords")
    suspend fun getRecordsByMoney(@Header("Authorization") token: String): Response<List<RecordDto>>
}