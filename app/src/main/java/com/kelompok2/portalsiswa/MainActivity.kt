package com.kelompok2.portalsiswa

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kelompok2.portalsiswa.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val dataSiswa = listOf(
        Siswa("1001", "Syahril Nursidik", "Sumedang, 02 Maret 2009", "Jl. 11 April", "Coding", "Developer", R.drawable.foto_anggota1),
        Siswa("1002", "Hildan Hasan Fadilah", "Sumedang, 22 Januari 2009", "Jl. Cijati", "Desain", "UI/UX", R.drawable.foto_anggota2),
        Siswa("1003", "Muhammad Al Fathira", "Subang, 24 Mei 2009", "Jl. Baginda", "Gamer", "Data Analyst", R.drawable.foto_anggota3)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fitur Logout Kembali ke SplashActivity
        binding.btnLogout.setOnClickListener {
            Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        binding.btnDetail1.setOnClickListener { showDetailDialog(dataSiswa[0]) }
        binding.btnDetail2.setOnClickListener { showDetailDialog(dataSiswa[1]) }
        binding.btnDetail3.setOnClickListener { showDetailDialog(dataSiswa[2]) }
    }

    private fun showDetailDialog(siswa: Siswa) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_detail)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.findViewById<ImageView>(R.id.imgDetailFoto).setImageResource(siswa.fotoResId)
        dialog.findViewById<TextView>(R.id.tvNis).text = "NIS : ${siswa.nis}"
        dialog.findViewById<TextView>(R.id.tvNama).text = "NAMA : ${siswa.nama}"
        dialog.findViewById<TextView>(R.id.tvTtl).text = "TTL : ${siswa.ttl}"
        dialog.findViewById<TextView>(R.id.tvAlamat).text = "ALAMAT : ${siswa.alamat}"
        dialog.findViewById<TextView>(R.id.tvHobi).text = "HOBI : ${siswa.hobi}"
        dialog.findViewById<TextView>(R.id.tvCitaCita).text = "CITA-CITA : ${siswa.citaCita}"

        dialog.findViewById<Button>(R.id.btnTutup).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
