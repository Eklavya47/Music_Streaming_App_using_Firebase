package com.company.miniproject1.model

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bumptech.glide.Glide


@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val userId: String,
    val name: String,
    val imageUrl: String
){
    // Binding Adapter
// images to display into imageviews in custom views
    object DataBindingAdapter{
        @BindingAdapter("imageUrl")
        @JvmStatic
        fun setImageByRes(imageView: ImageView, imageUrl: String){
            Glide.with(imageView.context)
                .load(imageUrl)
                .into(imageView)

        }
    }
}


