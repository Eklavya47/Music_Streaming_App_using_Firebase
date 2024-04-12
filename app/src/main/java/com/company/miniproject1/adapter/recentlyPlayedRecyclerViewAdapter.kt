package com.company.miniproject1.adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.miniproject1.databinding.RecentlyPlayedRowBinding
import com.company.miniproject1.model.RecentlyPlayedSong

class recentlyPlayedRecyclerViewAdapter(val context: android.content.Context, var recentPlayed: List<RecentlyPlayedSong>): RecyclerView.Adapter<recentlyPlayedRecyclerViewAdapter.MyViewHolder>() {
    private lateinit var binding: RecentlyPlayedRowBinding
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
        fun onItemMenuClick(position: Int, view: View)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = RecentlyPlayedRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding, mListener, binding.root)
    }

    override fun getItemCount(): Int {
        return recentPlayed.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val song = recentPlayed[position]
        holder.bind(song)
    }

    class MyViewHolder(var binding: RecentlyPlayedRowBinding, listener: onItemClickListener, itemView: View): RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {
        init {
            binding.menuIcon.setOnClickListener {
                listener.onItemMenuClick(adapterPosition, it)
            }
            binding.recentlyPlayedCardView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
        fun bind(song: RecentlyPlayedSong) {
            binding.song = song
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            TODO("Not yet implemented")
        }

    }
}