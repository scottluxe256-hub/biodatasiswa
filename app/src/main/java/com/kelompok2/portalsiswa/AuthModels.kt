package com.kelompok2.portalsiswa

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("status") val status: Any?, // Menangani response boolean atau string
    @SerializedName("message") val message: String?
)

data class Siswa(
    val nis: String,
    val nama: String,
    val ttl: String,
    val alamat: String,
    val hobi: String,
    val citaCita: String
)