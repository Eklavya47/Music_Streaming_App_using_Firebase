package com.company.miniproject1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.miniproject1.databinding.SearchPageSongsBinding
import com.company.miniproject1.model.Song

class searchSongRecyclerViewAdapter(val context: android.content.Context, var songList: List<Song>): RecyclerView.Adapter<searchSongRecyclerViewAdapter.MyViewHolder>() {
    private lateinit var binding: SearchPageSongsBinding
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = SearchPageSongsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, mListener)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val song = songList[position]
        holder.bind(song)
    }

    class MyViewHolder(var binding: SearchPageSongsBinding, listener: onItemClickListener): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.songCardView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
        fun bind(song: Song) {
            binding.song = song
        }
    }
    fun updateList(newList: List<Song>) {
        songList = newList
        notifyDataSetChanged()
    }

}