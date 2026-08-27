package com.kelompok2.portalsiswa

import com.google.gson.annotations.SerializedName

// Model Request JSON untuk Login
data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

// Model Request JSON untuk Register
data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String = "user"
)

// Model Data User dari Response PHP
data class UserData(
    @SerializedName("id") val id: Int?,
    @SerializedName("username") val username: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("role") val role: String?
)

// Model Response JSON dari Backend PHP
data class AuthResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: UserData?
)

// Model Data Siswa dari Response PHP SQLite
data class Siswa(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("nis") val nis: String? = "",
    @SerializedName("nama") val nama: String? = "",
    @SerializedName("tempat_lahir") val tempatLahir: String? = "",
    @SerializedName("tanggal_lahir") val tanggalLahir: String? = "",
    @SerializedName("alamat") val alamat: String? = "",
    @SerializedName("hobi") val hobi: String? = "",
    @SerializedName("cita_cita") val citaCita: String? = "",
    @SerializedName("foto") val foto: String? = null
) {
    val ttlDisplay: String
        get() {
            val tmpt = tempatLahir.orEmpty()
            val tgl = tanggalLahir.orEmpty()
            return when {
                tmpt.isNotEmpty() && tgl.isNotEmpty() -> "$tmpt, $tgl"
                tmpt.isNotEmpty() -> tmpt
                else -> tgl
            }
        }
}

// Model Response List Siswa dari Backend PHP
data class SiswaResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: List<Siswa>?
)

// Model Response Umum (Simpan, Update, Hapus)
data class GeneralResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String?
)