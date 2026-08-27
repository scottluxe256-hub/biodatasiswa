package com.kelompok2.portalsiswa

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

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

    // GET List Data Siswa
    @GET("get.php")
    fun getSiswa(): Call<SiswaResponse>

    // POST Tambah Data Siswa (Multipart)
    @Multipart
    @POST("create.php")
    fun createSiswa(
        @Part("nis") nis: RequestBody,
        @Part("nama") nama: RequestBody,
        @Part("tempat_lahir") tempatLahir: RequestBody,
        @Part("tanggal_lahir") tanggalLahir: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("hobi") hobi: RequestBody,
        @Part("cita_cita") citaCita: RequestBody,
        @Part foto: MultipartBody.Part?
    ): Call<GeneralResponse>

    // POST Edit Data Siswa (Multipart)
    @Multipart
    @POST("update.php")
    fun updateSiswa(
        @Part("id") id: RequestBody,
        @Part("nis") nis: RequestBody,
        @Part("nama") nama: RequestBody,
        @Part("tempat_lahir") tempatLahir: RequestBody,
        @Part("tanggal_lahir") tanggalLahir: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("hobi") hobi: RequestBody,
        @Part("cita_cita") citaCita: RequestBody,
        @Part foto: MultipartBody.Part?
    ): Call<GeneralResponse>

    // GET / POST Hapus Data Siswa dengan Query id
    @GET("delete.php")
    fun deleteSiswa(
        @Query("id") id: Int
    ): Call<GeneralResponse>

    companion object {
        private const val BASE_URL = "https://apikotlin-production.up.railway.app/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}