package com.company.miniproject1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.miniproject1.databinding.UserUploadedSongsBinding
import com.company.miniproject1.model.Song

class uploadedSongsAdapter(val context: android.content.Context, var songList: List<Song>): RecyclerView.Adapter<uploadedSongsAdapter.MyViewHolder>() {
    private lateinit var binding: UserUploadedSongsBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = UserUploadedSongsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val song = songList[position]
        holder.bind(song)
    }
    class MyViewHolder(var binding: UserUploadedSongsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.song = song
        }
    }
}