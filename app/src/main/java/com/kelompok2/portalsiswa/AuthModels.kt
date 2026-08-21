package com.kelompok2.portalsiswa

import com.google.gson.annotations.SerializedName

// Model Request JSON untuk Login & Register
data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    @SerializedName("status") val status: Any?,
    @SerializedName("message") val message: String?,
    @SerializedName("username") val username: String?
)

data class Siswa(
    val nis: String,
    val nama: String,
    val ttl: String,
    val alamat: String,
    val hobi: String,
    val citaCita: String,
    val fotoResId: Int
)
