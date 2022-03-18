package com.example.eskape

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
import com.example.eskape.databinding.ActivityAddKegiatanBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddKegiatanActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private lateinit var db : skpDB
    private lateinit var binding : ActivityAddKegiatanBinding
    private var stats : String? = null
    private var count : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddKegiatanBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupSpinner()

        db = Room.databaseBuilder(applicationContext, skpDB::class.java, "skps.db").build()
        CoroutineScope(Dispatchers.IO).launch {
            if(db.skpDao().getCountSKP() != null){
                count = db.skpDao().getCountSKP()
            }
            withContext(Dispatchers.Main){
                binding.submit.setOnClickListener {
                    if(count >= 100){
                        Toast.makeText(this@AddKegiatanActivity, "Poin Sudah maksimal!", Toast.LENGTH_SHORT).show()
                        switchToHome()
                    }else {
                        if(validateInput()) {
                            val namakegiatan = binding.namaKegiatan.text.toString()
                            val posisi       = binding.posisiKegiatan.text.toString()
                            val poin         = binding.jumlahSKP.text.toString()
                            val status       = stats!!
                            addKegiatan(namakegiatan, posisi, poin, status)
                        }
                    }
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        if(binding.namaKegiatan.text.toString().isEmpty()){
            binding.layoutNamaKegiatan.isErrorEnabled = true
            binding.layoutNamaKegiatan.error = "Kolom Nama Kegiatan Harus Diisi!"
            return false
        }

        if(binding.posisiKegiatan.text.toString().isEmpty()){
            binding.layoutPosisi.isErrorEnabled = true
            binding.layoutPosisi.error = "Kolom Posisi tidak boleh kosong!"
            return false
        }

        if(binding.jumlahSKP.text.toString().isEmpty()){
            binding.layoutPoin.isErrorEnabled = true
            binding.layoutPoin.error = "Kolom Jumlah poin tidak boleh kosong!"
            return false
        }

        if(binding.jumlahSKP.text.toString().toInt() > 100){
            binding.layoutPoin.isErrorEnabled = true
            binding.layoutPoin.error = "Kolom Jumlah poin tidak boleh melebihi 100 poin!"
            return false
        }

        if(stats == null){
            binding.layoutStatus.isErrorEnabled = true
            binding.layoutStatus.error = "Kolom Status tidak boleh kosong!"
            return false
        }

        return true
    }

    private fun addKegiatan(namakegiatan: String, posisi: String, poin: String, status: String) {
        CoroutineScope(Dispatchers.IO).launch {
            db.skpDao().addSKP(
                skpData(0, namakegiatan, posisi, poin.toInt(), status)
            )
            withContext(Dispatchers.Main){
                Toast.makeText(getApplication(), "Berhasil Menambahkan $namakegiatan", Toast.LENGTH_SHORT).show()
                switchToHome()
            }
        }
    }

    private fun switchToHome() {
        val intent = Intent(this@AddKegiatanActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupSpinner() {
        val options = listOf("Sudah Selesai", "Sedang Berjalan")
        val adapter = ArrayAdapter(this, R.layout.list_item, options)
        with(binding.statusKepanitiaan){
            setText("Sedang Berjalan", false)
            if(binding.statusKepanitiaan.getText().toString().isNotEmpty())
                stats = binding.statusKepanitiaan.getText().toString()
            onItemClickListener = this@AddKegiatanActivity
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