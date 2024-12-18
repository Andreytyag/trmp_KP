package com.example.ridewithobstacles.ApiService.RetrofitConfig
import com.example.ridewithobstacles.ApiService.Interfaces.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitClient {
    private const val BASE_URL = "https://192.168.1.6:5000/api/rides/"


    fun getUnsafeOkHttpClient(): OkHttpClient {
        try {
            // Создаем TrustManager, который не выполняет проверки
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // Создаем SSLContext с кастомным TrustManager
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true } // Отключаем проверку хостов

            return builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }


    val apiService: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).client(getUnsafeOkHttpClient())
        .build()
        .create(ApiService::class.java)
}