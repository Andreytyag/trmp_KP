package com.example.ridewithobstacles.ApiService

import com.example.ridewithobstacles.ApiService.DataModels.LoginRequest
import com.example.ridewithobstacles.ApiService.DataModels.LoginResponse
import com.example.ridewithobstacles.ApiService.DataModels.RegisterRequest
import com.example.ridewithobstacles.ApiService.DataModels.RegisterResponse
import com.example.ridewithobstacles.ApiService.DataModels.RecordDto
import com.example.ridewithobstacles.ApiService.RetrofitConfig.RetrofitClient
import java.io.IOException

class ApiRepository{

    private val apiService = RetrofitClient.apiService

    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse?> {
        return try {
            val response = apiService.login(loginRequest)
            if (response.token.isNotEmpty()) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Нет соединения с сервером!"))
        } catch (e: Exception) {
            Result.failure(Exception("An unexpected error occurred."))
        }
    }

    suspend fun register(registerRequest: RegisterRequest): Result<RegisterResponse?> {
        return try {
            val response = apiService.register(registerRequest)
            if(response.success){
                Result.success(response)
            }
            else{
                Result.failure(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Нет соединения с сервером!"))
        } catch (e: Exception) {
            Result.failure(Exception("An unexpected error occurred."))
        }
    }

    suspend fun sendRides(jwtToken: String, record: RecordDto): Result<String> {
        return try {
            val response = apiService.sendRecord("Bearer $jwtToken", record)
            if(response.isSuccessful){
                Result.success("Данные синхронизированы")
            }
            else if(response.code() == 401){
                Result.failure(Exception("Пользователь не авторизован. Пожалуйста, авторизуйтесь"))
            }
            else{
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message))
        }
    }

    suspend fun getRecordsByScore(jwtToken: String): Result<List<RecordDto>> {
        return try {
            val rides = apiService.getRecordsByScore("Bearer $jwtToken")
            if (rides.isSuccessful){
                Result.success(rides.body() ?: emptyList())
            }
            else{
                Result.failure(Exception("${rides.code()} - ${rides.message()}"))
            }

        } catch (e: IOException) {
            Result.failure(Exception("Нет соединения с сервером!"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error occurred."))
        }
    }

    suspend fun getRecordsByMoney(jwtToken: String): Result<List<RecordDto>> {
        return try {
            val rides = apiService.getRecordsByMoney("Bearer $jwtToken")
            if (rides.isSuccessful){
                Result.success(rides.body() ?: emptyList())
            }
            else{
                Result.failure(Exception("${rides.code()} - ${rides.message()}"))
            }

        } catch (e: IOException) {
            Result.failure(Exception("Нет соединения с сервером!"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error occurred."))
        }
    }
}

