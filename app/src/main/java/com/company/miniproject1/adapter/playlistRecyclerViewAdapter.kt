package com.company.miniproject1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.miniproject1.activity.MainActivity
import com.company.miniproject1.databinding.PlaylistRowBinding
import com.company.miniproject1.model.Playlist

class playlistRecyclerViewAdapter(val context: Context, var playList: List<Playlist>): RecyclerView.Adapter<playlistRecyclerViewAdapter.MyViewHolder>() {
    private lateinit var binding: PlaylistRowBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = PlaylistRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return playList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val playList = playList[position]
        holder.bind(playList)
    }

    class MyViewHolder(var binding: PlaylistRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(playList: Playlist){
            binding.playList = playList
        }
    }
}