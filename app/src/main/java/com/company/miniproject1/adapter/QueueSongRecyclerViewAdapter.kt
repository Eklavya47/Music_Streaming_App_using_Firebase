package com.company.miniproject1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.miniproject1.databinding.QueueSongsRowBinding
import com.company.miniproject1.model.Song

class QueueSongRecyclerViewAdapter(val context: android.content.Context, var songList: List<Song>):
    RecyclerView.Adapter<QueueSongRecyclerViewAdapter.MyViewHolder>() {
    private lateinit var binding: QueueSongsRowBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = QueueSongsRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val song = songList[position]
        holder.bind(song)
    }
    class MyViewHolder(var binding: QueueSongsRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.song = song
        }
    }
}