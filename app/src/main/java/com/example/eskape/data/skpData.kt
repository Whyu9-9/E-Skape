package com.example.eskape.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skps")
data class skpData (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nama_kegiatan: String,
    val posisi: String,
    val poin_skp: Int,
    val status: String
)
