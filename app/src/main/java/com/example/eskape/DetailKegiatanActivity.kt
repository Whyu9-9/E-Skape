package com.example.eskape

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.room.Room
import com.example.eskape.data.skpData
import com.example.eskape.database.setup.skpDB
import com.example.eskape.databinding.ActivityDetailKegiatanBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailKegiatanActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private lateinit var binding: ActivityDetailKegiatanBinding
    private lateinit var db : skpDB
    private var stats : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityDetailKegiatanBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = Room.databaseBuilder(applicationContext, skpDB::class.java, "skps.db").build()
        val bundle :Bundle ?= intent.extras
        val id           = bundle?.getInt("id")
        val poin         = bundle?.getInt("poin")
        val namakegiatan = bundle?.getString("namakegiatan")
        val posisi       = bundle?.getString("posisi")
        val status       = bundle?.getString("status")
        setupSpinner(status)

        binding.editJumlahSKP.setText(poin!!.toString())
        binding.editNamaKegiatan.setText(namakegiatan!!)
        binding.editPosisiKegiatan.setText(posisi!!)

        binding.delete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Hapus Data")
                .setMessage("Apakah anda yakin ingin menghapus data ini?")
                .setCancelable(true)
                .setPositiveButton("Iya") { _, _ ->
                    deleteKegiatan(id)
                    switchToHome()
                }.setNegativeButton("Batal") { dialogInterface, _ ->
                    dialogInterface.cancel()
                }.show()
        }

        binding.edit.setOnClickListener {
            if(validateInput()){
                val namakegiatanForm = binding.editNamaKegiatan.text.toString()
                val posisiForm       = binding.editPosisiKegiatan.text.toString()
                val poinForm         = binding.editJumlahSKP.text.toString()
                val statusForm       = stats!!
                editKegiatan(id!!, namakegiatanForm, posisiForm, poinForm, statusForm)
            }
        }


    }

    private fun deleteKegiatan(id: Int?) {
        CoroutineScope(Dispatchers.IO).launch {
            db.skpDao().deleteById(id!!)
        }
    }

    private fun editKegiatan(id: Int, namakegiatanForm: String, posisiForm: String, poinForm: String, statusForm: String) {
        CoroutineScope(Dispatchers.IO).launch {
            db.skpDao().updateSKP(
                skpData(id, namakegiatanForm, posisiForm, poinForm.toInt(), statusForm)
            )
            withContext(Dispatchers.Main){
                Toast.makeText(getApplication(), "Berhasil Mengupdate $namakegiatanForm", Toast.LENGTH_SHORT).show()
                switchToHome()
            }
        }
    }

    private fun switchToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateInput(): Boolean {
        if(binding.editNamaKegiatan.text.toString().isEmpty()){
            binding.layoutEditNamaKegiatan.isErrorEnabled = true
            binding.layoutEditNamaKegiatan.error = "Kolom Nama Kegiatan Harus Diisi!"
            return false
        }

        if(binding.editPosisiKegiatan.text.toString().isEmpty()){
            binding.layoutEditPosisi.isErrorEnabled = true
            binding.layoutEditPosisi.error = "Kolom Posisi tidak boleh kosong!"
            return false
        }

        if(binding.editJumlahSKP.text.toString().isEmpty()){
            binding.layoutEditPoin.isErrorEnabled = true
            binding.layoutEditPoin.error = "Kolom Jumlah poin tidak boleh kosong!"
            return false
        }

        if(binding.editJumlahSKP.text.toString().toInt() > 100){
            binding.layoutEditPoin.isErrorEnabled = true
            binding.layoutEditPoin.error = "Kolom Jumlah poin tidak boleh melebihi 100 poin!"
            return false
        }

        if(stats == null){
            binding.layoutEditStatus.isErrorEnabled = true
            binding.layoutEditStatus.error = "Kolom Status tidak boleh kosong!"
            return false
        }

        return true
    }
    private fun setupSpinner(status: String?) {
        val options = listOf("Sudah Selesai", "Sedang Berjalan")
        val adapter = ArrayAdapter(this, R.layout.list_item, options)
        with(binding.editStatusKepanitiaan){
            setText(status.toString(), false)
            if(binding.editStatusKepanitiaan.getText().toString().isNotEmpty())
                stats = binding.editStatusKepanitiaan.getText().toString()
            onItemClickListener = this@DetailKegiatanActivity
            setAdapter(adapter)
        }
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val item = p0?.getItemAtPosition(p2).toString()
        stats    = item
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}