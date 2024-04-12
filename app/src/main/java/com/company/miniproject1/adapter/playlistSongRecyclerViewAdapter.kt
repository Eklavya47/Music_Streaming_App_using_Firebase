package com.company.miniproject1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.miniproject1.databinding.CreatePlaylistRowBinding
import com.company.miniproject1.model.Song

class playlistSongRecyclerViewAdapter(val context: android.content.Context, var songList: List<Song>): RecyclerView.Adapter<playlistSongRecyclerViewAdapter.MyViewHolder>() {
    private lateinit var binding: CreatePlaylistRowBinding
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = CreatePlaylistRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding, mListener)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val song = songList[position]
        holder.bind(song)
    }
    class MyViewHolder(var binding: CreatePlaylistRowBinding, listener: onItemClickListener): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.cardView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
        fun bind(song: Song) {
            binding.song = song
        }

    }
}