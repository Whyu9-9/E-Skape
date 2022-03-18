package com.example.eskape.adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.eskape.R
import com.example.eskape.data.skpData


class SkpAdapter(val results: ArrayList<skpData>, val listener: OnAdapterListener)
    : RecyclerView.Adapter<SkpAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.list_skp, parent, false)
    )

    override fun getItemCount() = results.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]
        holder.namakegiatan.setText(result.nama_kegiatan)
        holder.poin.setText(result.poin_skp.toString())
        holder.posisi.setText(result.posisi)
        holder.status.setText(result.status)
        if(result.status != "Sudah Selesai"){
            holder.status.setBackgroundColor(Color.parseColor("#d9534f"))
        }
        holder.view.setOnClickListener{
            listener.onClick(result)
        }
    }

    class ViewHolder (val view: View) : RecyclerView.ViewHolder(view){
        val namakegiatan: TextView   = view.findViewById(R.id.nama_kegiatan)
        val poin: TextView   = view.findViewById(R.id.poinskp)
        val posisi: TextView = view.findViewById(R.id.posisi)
        val status: TextView = view.findViewById(R.id.statusKeterangan)
    }

    fun setData (data: List<skpData>) {
        results.clear()
        results.addAll(data)
        notifyDataSetChanged()
    }

    interface OnAdapterListener{
        fun onClick(result: skpData)
    }
}