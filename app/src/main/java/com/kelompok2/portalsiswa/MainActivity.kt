package com.kelompok2.portalsiswa

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kelompok2.portalsiswa.databinding.ActivityMainBinding
import com.kelompok2.portalsiswa.databinding.DialogDetailBinding
import com.kelompok2.portalsiswa.databinding.DialogFormSiswaBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var siswaAdapter: SiswaAdapter
    private var userRole: String = "user"

    private var selectedPhotoUri: Uri? = null
    private var onPhotoSelectedCallback: ((Uri, String) -> Unit)? = null

    // Register ActivityResultLauncher untuk 'Choose File' foto dari galeri HP
    private val getContentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
            val fileName = getFileName(uri)
            onPhotoSelectedCallback?.invoke(uri, fileName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Ambil data USER_ROLE dari Intent (Login)
        userRole = intent.getStringExtra("USER_ROLE") ?: "user"
        val isAdmin = userRole.equals("admin", ignoreCase = true)

        // Tampilkan/Sembunyikan Tombol Tambah berdasarkan role
        binding.btnTambah.visibility = if (isAdmin) View.VISIBLE else View.GONE

        // Setup RecyclerView & Adapter
        siswaAdapter = SiswaAdapter(emptyList()) { siswa ->
            showDetailDialog(siswa)
        }
        binding.rvSiswa.adapter = siswaAdapter

        // Tombol Logout
        binding.btnLogout.setOnClickListener {
            Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Tombol Tambah Siswa (Hanya tampil untuk Admin)
        binding.btnTambah.setOnClickListener {
            showFormDialog(null)
        }

        // Muat Data Siswa dari Server Railway
        loadDataSiswa()
    }

    private fun loadDataSiswa() {
        ApiService.create().getSiswa().enqueue(object : Callback<SiswaResponse> {
            override fun onResponse(call: Call<SiswaResponse>, response: Response<SiswaResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val siswaList = response.body()!!.data.orEmpty()
                    if (siswaList.isNotEmpty()) {
                        siswaAdapter.updateData(siswaList)
                        binding.rvSiswa.visibility = View.VISIBLE
                        binding.tvEmptyState.visibility = View.GONE
                    } else {
                        siswaAdapter.updateData(emptyList())
                        binding.rvSiswa.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Gagal mengambil data dari server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SiswaResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error Koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDetailDialog(siswa: Siswa) {
        val dialog = Dialog(this)
        val bindingDialog = DialogDetailBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Dimuat menggunakan Glide
        val fotoUrl = if (!siswa.foto.isNullOrEmpty()) {
            "https://apikotlin-production.up.railway.app/uploads/${siswa.foto}"
        } else ""

        Glide.with(this)
            .load(fotoUrl)
            .placeholder(R.drawable.logo_smk)
            .error(R.drawable.logo_smk)
            .into(bindingDialog.imgDetailFoto)

        bindingDialog.tvNis.text = "NIS : ${siswa.nis ?: "-"}"
        bindingDialog.tvNama.text = "NAMA : ${siswa.nama ?: "-"}"
        bindingDialog.tvTtl.text = "TTL : ${siswa.ttlDisplay}"
        bindingDialog.tvAlamat.text = "ALAMAT : ${siswa.alamat ?: "-"}"
        bindingDialog.tvHobi.text = "HOBI : ${siswa.hobi ?: "-"}"
        bindingDialog.tvCitaCita.text = "CITA-CITA : ${siswa.citaCita ?: "-"}"

        val isAdmin = userRole.equals("admin", ignoreCase = true)

        // Aturan Hak Akses Role di Dialog Detail
        bindingDialog.btnDelete.visibility = if (isAdmin) View.VISIBLE else View.GONE
        bindingDialog.btnEdit.visibility = if (isAdmin) View.VISIBLE else View.GONE
        bindingDialog.btnTutup.visibility = View.VISIBLE

        bindingDialog.btnTutup.setOnClickListener { dialog.dismiss() }

        bindingDialog.btnEdit.setOnClickListener {
            dialog.dismiss()
            showFormDialog(siswa)
        }

        bindingDialog.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(siswa, dialog)
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(siswa: Siswa, detailDialog: Dialog) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah anda yakin ingin menghapus data ini?")
            .setPositiveButton("Ya, Hapus") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val siswaId = siswa.id
                if (siswaId != null) {
                    ApiService.create().deleteSiswa(siswaId).enqueue(object : Callback<GeneralResponse> {
                        override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                            if (response.isSuccessful && response.body()?.status == true) {
                                Toast.makeText(this@MainActivity, "Data berhasil dihapus!", Toast.LENGTH_SHORT).show()
                                detailDialog.dismiss()
                                loadDataSiswa()
                            } else {
                                Toast.makeText(this@MainActivity, response.body()?.message ?: "Gagal menghapus data!", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                            Toast.makeText(this@MainActivity, "Error Koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this, "ID Siswa tidak valid", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showFormDialog(siswaToEdit: Siswa?) {
        val dialog = Dialog(this)
        val bindingForm = DialogFormSiswaBinding.inflate(layoutInflater)
        dialog.setContentView(bindingForm.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        selectedPhotoUri = null
        val isEditMode = siswaToEdit != null

        bindingForm.tvFormTitle.text = if (isEditMode) "Edit Data Siswa" else "Tambah Data Siswa"

        if (isEditMode && siswaToEdit != null) {
            bindingForm.etNis.setText(siswaToEdit.nis)
            bindingForm.etNama.setText(siswaToEdit.nama)
            bindingForm.etTempatLahir.setText(siswaToEdit.tempatLahir)
            bindingForm.etTanggalLahir.setText(siswaToEdit.tanggalLahir)
            bindingForm.etAlamat.setText(siswaToEdit.alamat)
            bindingForm.etHobi.setText(siswaToEdit.hobi)
            bindingForm.etCitaCita.setText(siswaToEdit.citaCita)
            bindingForm.tvSelectedFileName.text = siswaToEdit.foto ?: "Foto tersimpan di server"
        }

        // DatePicker untuk Tanggal Lahir (Focusable=false, Clickable=true)
        bindingForm.etTanggalLahir.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    bindingForm.etTanggalLahir.setText(formattedDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        // File Picker untuk Foto Siswa
        bindingForm.btnChooseFile.setOnClickListener {
            onPhotoSelectedCallback = { _, fileName ->
                bindingForm.tvSelectedFileName.text = fileName
            }
            getContentLauncher.launch("image/*")
        }

        bindingForm.btnBatal.setOnClickListener {
            dialog.dismiss()
        }

        bindingForm.btnSimpan.setOnClickListener {
            val nis = bindingForm.etNis.text.toString().trim()
            val nama = bindingForm.etNama.text.toString().trim()
            val tempatLahir = bindingForm.etTempatLahir.text.toString().trim()
            val tanggalLahir = bindingForm.etTanggalLahir.text.toString().trim()
            val alamat = bindingForm.etAlamat.text.toString().trim()
            val hobi = bindingForm.etHobi.text.toString().trim()
            val citaCita = bindingForm.etCitaCita.text.toString().trim()

            if (nis.isEmpty() || nama.isEmpty()) {
                Toast.makeText(this, "NIS dan Nama wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nisBody = nis.toTextRequestBody()
            val namaBody = nama.toTextRequestBody()
            val tempatLahirBody = tempatLahir.toTextRequestBody()
            val tanggalLahirBody = tanggalLahir.toTextRequestBody()
            val alamatBody = alamat.toTextRequestBody()
            val hobiBody = hobi.toTextRequestBody()
            val citaCitaBody = citaCita.toTextRequestBody()
            val fotoPart = prepareFilePart("foto", selectedPhotoUri)

            bindingForm.btnSimpan.isEnabled = false

            if (isEditMode && siswaToEdit?.id != null) {
                val idBody = siswaToEdit.id.toString().toTextRequestBody()
                ApiService.create().updateSiswa(
                    id = idBody,
                    nis = nisBody,
                    nama = namaBody,
                    tempatLahir = tempatLahirBody,
                    tanggalLahir = tanggalLahirBody,
                    alamat = alamatBody,
                    hobi = hobiBody,
                    citaCita = citaCitaBody,
                    foto = fotoPart
                ).enqueue(object : Callback<GeneralResponse> {
                    override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                        bindingForm.btnSimpan.isEnabled = true
                        if (response.isSuccessful && response.body()?.status == true) {
                            Toast.makeText(this@MainActivity, "Berhasil memperbarui data!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            loadDataSiswa()
                        } else {
                            Toast.makeText(this@MainActivity, response.body()?.message ?: "Gagal update data!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                        bindingForm.btnSimpan.isEnabled = true
                        Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                ApiService.create().createSiswa(
                    nis = nisBody,
                    nama = namaBody,
                    tempatLahir = tempatLahirBody,
                    tanggalLahir = tanggalLahirBody,
                    alamat = alamatBody,
                    hobi = hobiBody,
                    citaCita = citaCitaBody,
                    foto = fotoPart
                ).enqueue(object : Callback<GeneralResponse> {
                    override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                        bindingForm.btnSimpan.isEnabled = true
                        if (response.isSuccessful && response.body()?.status == true) {
                            Toast.makeText(this@MainActivity, "Berhasil menambah data siswa!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            loadDataSiswa()
                        } else {
                            Toast.makeText(this@MainActivity, response.body()?.message ?: "Gagal simpan data!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                        bindingForm.btnSimpan.isEnabled = true
                        Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        dialog.show()
    }

    private fun String.toTextRequestBody(): RequestBody {
        val mediaType = MediaType.parse("text/plain")
        return RequestBody.create(mediaType, this)
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "foto_siswa.jpg"
    }

    private fun prepareFilePart(partName: String, fileUri: Uri?): MultipartBody.Part? {
        if (fileUri == null) return null
        return try {
            val inputStream = contentResolver.openInputStream(fileUri) ?: return null
            val bytes = inputStream.readBytes()
            inputStream.close()
            val mimeType = contentResolver.getType(fileUri) ?: "image/*"
            val fileName = getFileName(fileUri)
            val mediaType = MediaType.parse(mimeType)
            val requestFile = RequestBody.create(mediaType, bytes)
            MultipartBody.Part.createFormData(partName, fileName, requestFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
