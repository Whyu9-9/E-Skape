package com.example.eskape

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.eskape.adapter.SkpAdapter
import com.example.eskape.data.skpData
import com.example.eskape.database.setup.skpDB
import com.example.eskape.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    private lateinit var binding : ActivityMainBinding
    private lateinit var skpAdapter : SkpAdapter
    private lateinit var db : skpDB
    private var count : Int = 0

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        db = Room.databaseBuilder(applicationContext, skpDB::class.java, "skps.db").build()

        CoroutineScope(Dispatchers.IO).launch {
            if(db.skpDao().getCountSKP() != null){
                count = db.skpDao().getCountSKP()
            }

            withContext(Dispatchers.Main){
                if(count >= 100){
                    binding.totalpoin.setTextColor(Color.parseColor("#5cb85c"))
                }
                if (count > 0)
                    binding.totalpoin.text = count.toString()
            }
        }

        getAllSkpData()
        setupRecyclerviewSkp()

        binding.fab.setOnClickListener {
            val intent = Intent(this, AddKegiatanActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun setupRecyclerviewSkp() {
        skpAdapter = SkpAdapter(arrayListOf(), object : SkpAdapter.OnAdapterListener{
            override fun onClick(result: skpData) {
                val bundle = Bundle()
                val intent = Intent(this@MainActivity, DetailKegiatanActivity::class.java)
                bundle.putInt("id", result.id)
                bundle.putInt("poin", result.poin_skp)
                bundle.putString("posisi", result.posisi)
                bundle.putString("status", result.status)
                bundle.putString("namakegiatan", result.nama_kegiatan)
                intent.putExtras(bundle)
                startActivity(intent)
                finish()
            }
        })
        binding.listKegiatan.apply {
            layoutManager      = LinearLayoutManager(context)
            adapter            = skpAdapter
            setHasFixedSize(true)
        }
    }

    private fun getAllSkpData() {
        CoroutineScope(Dispatchers.IO).launch {
            val skp = db.skpDao().getAll()
            withContext(Dispatchers.Main){
                if(skp.isNotEmpty()){
                    binding.nodata.visibility   = View.GONE
                    binding.rvLayout.visibility = View.VISIBLE
                }else{
                    binding.nodata.visibility   = View.VISIBLE
                    binding.rvLayout.visibility = View.GONE
                }
                showData(skp)
            }
        }
    }

    private fun showData(skp: List<skpData>) {
        skpAdapter.setData(skp)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}