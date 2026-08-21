package com.kelompok2.portalsiswa

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    // Mengirim JSON Raw Body untuk Login
    @POST("login.php")
    fun login(
        @Body request: LoginRequest
    ): Call<AuthResponse>

    // Mengirim JSON Raw Body untuk Register
    @POST("register.php")
    fun register(
        @Body request: RegisterRequest
    ): Call<AuthResponse>

    companion object {
        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl("https://login-api-production-a877.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
