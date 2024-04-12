package com.company.miniproject1.model

import android.os.Parcel
import android.os.Parcelable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

data class Song(
    val title: String,
    val imageUrl: String,
    val songUrl: String,
    val userId: String,
    val lyrics: String,
    val singer: String,
    val genre: String

): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(imageUrl)
        parcel.writeString(songUrl)
        parcel.writeString(userId)
        parcel.writeString(lyrics)
        parcel.writeString(singer)
        parcel.writeString(genre)
    }
    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }
        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }

    fun toPlaylistSong(playlistId: Long, song: Song): PlaylistSong {
        return PlaylistSong(
            playlistId = playlistId,
            title = song.title,
            imageUrl = song.imageUrl,
            songUrl = song.songUrl,
            userId = song.userId,
            lyrics = song.lyrics,
            singer = song.singer,
            genre = song.genre
        )
    }
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
