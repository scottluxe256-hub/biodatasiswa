package com.kelompok2.portalsiswa

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kelompok2.portalsiswa.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Spinner Role
        val roles = arrayOf("user", "admin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        binding.spRoleLogin.adapter = adapter

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val user = binding.etUsername.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            val selectedRole = binding.spRoleLogin.selectedItem.toString()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Isi username dan password!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(username = user, password = pass)

            ApiService.create().login(request).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val authResponse = response.body()!!

                        if (authResponse.status) {
                            val userRole = authResponse.data?.role ?: "user"
                            
                            // Validasi apakah role yang dipilih sesuai dengan role di database
                            if (userRole.equals(selectedRole, ignoreCase = true)) {
                                Toast.makeText(this@LoginActivity, "Login Berhasil! Role: $userRole", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.putExtra("USER_ROLE", userRole)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "Role tidak sesuai dengan akun ini!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, authResponse.message ?: "Login Gagal!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Gagal terhubung ke server!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error Koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.tvToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
