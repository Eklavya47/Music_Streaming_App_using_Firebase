package com.company.miniproject1.model

import android.os.Parcel
import android.os.Parcelable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bumptech.glide.Glide

@Entity(tableName = "recently_played_songs")
data class RecentlyPlayedSong(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val songTitle: String,
    val imageUrl: String,
    val songUrl: String,
    val userId: String,
    val lyrics: String,
    val genre: String,
    val artistName: String,
    val playTime: Long
): Parcelable { // The parcelabe thing and all the code below is used to transfer a list containing all attributes of song to another activity and its was given by chatgpt
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(songTitle)
        parcel.writeString(imageUrl)
        parcel.writeString(songUrl)
        parcel.writeString(userId)
        parcel.writeString(lyrics)
        parcel.writeString(genre)
        parcel.writeString(artistName)
        parcel.writeString(playTime.toString())
    }

    override fun describeContents(): Int {
        return 0
    }

    fun toSong(): Song {
        return Song(
            songTitle,
            imageUrl,
            songUrl,
            userId,
            lyrics,
            artistName,
            genre
        )
    }

    companion object CREATOR : Parcelable.Creator<RecentlyPlayedSong> {
        override fun createFromParcel(parcel: Parcel): RecentlyPlayedSong {
            return RecentlyPlayedSong(parcel)
        }

        override fun newArray(size: Int): Array<RecentlyPlayedSong?> {
            return arrayOfNulls(size)
        }
    }
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
