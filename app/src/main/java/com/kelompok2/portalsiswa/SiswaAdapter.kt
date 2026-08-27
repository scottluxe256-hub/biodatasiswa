package com.kelompok2.portalsiswa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kelompok2.portalsiswa.databinding.ItemSiswaBinding

class SiswaAdapter(
    private var listSiswa: List<Siswa>,
    private val onDetailClick: (Siswa) -> Unit
) : RecyclerView.Adapter<SiswaAdapter.SiswaViewHolder>() {

    inner class SiswaViewHolder(val binding: ItemSiswaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiswaViewHolder {
        val binding = ItemSiswaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SiswaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SiswaViewHolder, position: Int) {
        val siswa = listSiswa[position]
        with(holder.binding) {
            tvNamaSiswa.text = siswa.nama ?: "-"
            tvNisSiswa.text = "NIS: ${siswa.nis ?: "-"}"

            val fotoUrl = if (!siswa.foto.isNullOrEmpty()) {
                "https://apikotlin-production.up.railway.app/uploads/${siswa.foto}"
            } else ""

            Glide.with(root.context)
                .load(fotoUrl)
                .placeholder(R.drawable.logo_smk)
                .error(R.drawable.logo_smk)
                .into(imgMember)

            btnDetail.setOnClickListener {
                onDetailClick(siswa)
            }
        }
    }

    override fun getItemCount(): Int = listSiswa.size

    fun updateData(newList: List<Siswa>) {
        listSiswa = newList
        notifyDataSetChanged()
    }
}
