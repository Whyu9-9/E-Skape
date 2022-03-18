package com.example.eskape.database.dao

import androidx.room.*
import com.example.eskape.data.skpData

@Dao
interface SkpDao {
    @Query("Select * from skps order by id DESC")
    suspend fun getAll() : List<skpData>

    @Query("Select sum(poin_skp) from skps where status ='Sudah Selesai' ")
    suspend fun getCountSKP() : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSKP(skp: skpData)

    @Update
    suspend fun updateSKP(skp: skpData)

    @Query("DELETE FROM skps WHERE id = :id")
    suspend fun deleteById(id: Int)

}