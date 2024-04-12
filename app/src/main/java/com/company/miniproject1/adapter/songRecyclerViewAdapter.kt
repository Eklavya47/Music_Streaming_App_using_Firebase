package com.company.miniproject1.adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.miniproject1.R
import com.company.miniproject1.databinding.SongsRowBinding
import com.company.miniproject1.model.Song

class songRecyclerViewAdapter(val context: android.content.Context, var songList: List<Song>):
    RecyclerView.Adapter<songRecyclerViewAdapter.MyViewHolder>() {
    private lateinit var binding: SongsRowBinding
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
        fun onItemMenuClick(position: Int, view: View)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = SongsRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding, mListener, binding.root)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val song = songList[position]
        holder.bind(song)

    }

    class MyViewHolder(var binding: SongsRowBinding, listener: onItemClickListener, itemView: View): RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener{
        init {
            binding.menuIcon.setOnClickListener {
                listener.onItemMenuClick(adapterPosition, it)
            }
            binding.songCardView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
        fun bind(song: Song) {
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