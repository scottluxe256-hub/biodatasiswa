package com.kelompok2.portalsiswa

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kelompok2.portalsiswa.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Spinner Role
        val roles = arrayOf("user", "admin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        binding.spRoleRegister.adapter = adapter

        binding.btnBatal.setOnClickListener { finish() }

        binding.btnRegister.setOnClickListener {
            val user = binding.etRegUsername.text.toString().trim()
            val email = binding.etRegEmail.text.toString().trim()
            val pass = binding.etRegPassword.text.toString().trim()
            val role = binding.spRoleRegister.selectedItem.toString()

            if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Isi semua kolom!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = RegisterRequest(username = user, email = email, password = pass, role = role)

            ApiService.create().register(request).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val authResponse = response.body()!!
                        if (authResponse.status) {
                            Toast.makeText(this@RegisterActivity, authResponse.message ?: "Berhasil Daftar!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, authResponse.message ?: "Gagal Daftar!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@RegisterActivity, "Gagal terhubung ke server!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Error Koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
