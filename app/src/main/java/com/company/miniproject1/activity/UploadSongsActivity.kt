package com.company.miniproject1.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.company.miniproject1.databinding.ActivityUploadSongsBinding
import com.company.miniproject1.model.Song
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UploadSongsActivity : BaseActivity() {
    private var binding: ActivityUploadSongsBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var imageURI: Uri
    private lateinit var songURI: Uri
    lateinit var songList: MutableList<Song>
    private var db = FirebaseFirestore.getInstance()
    private var collectionReference: CollectionReference = db.collection("songs")
    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadSongsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = Firebase.auth
        storageReference = FirebaseStorage.getInstance().reference
        songList = arrayListOf<Song>()

        // functionality of camera button for uploading image of song
        binding?.postSongImageButton?.setOnClickListener{
            var i = Intent(Intent.ACTION_GET_CONTENT)
            i.type = "image/*"
            startActivityForResult(i,1)
        }

        // for uploading the song from phone to app
        binding?.btnGetSong?.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/*"  // Set the MIME type to audio
            startActivityForResult(intent, 2)  // Use a different requestCode (e.g., 2)
        }

        // for uploading the song to firebase
        binding?.btnUploadSong?.setOnClickListener{
            saveSong()
        }

    }

    private fun saveSong() {
        var userId = auth.currentUser?.uid.toString()
        val title = binding?.etSongName?.text.toString()
        val singer = binding?.etArtistName?.text.toString()
        val genre = binding?.etSongGenre?.text.toString()
        val lyrics = binding?.etSongLyrics?.text.toString()
        if(validateForm(title, singer, genre, lyrics)){
            showProgressBar()
            // uploading song file to database
            val songFilePath: StorageReference = storageReference.child("song").child("my_song_"+System.currentTimeMillis())
            songFilePath.putFile(songURI).addOnSuccessListener {
                songFilePath.downloadUrl.addOnSuccessListener {songDownloadUrl ->
                    val songUrl = songDownloadUrl.toString()

                    // uploading image file to database
                    val imageFilePath: StorageReference = storageReference.child("song_poster").child("my_images_"+System.currentTimeMillis())
                    imageFilePath.putFile(imageURI).addOnSuccessListener {
                        imageFilePath.downloadUrl.addOnSuccessListener {imageDownloadUrl ->
                            val imageUrl = imageDownloadUrl.toString()
                            val song = Song(title, imageUrl, songUrl, userId, lyrics, singer, genre)
                            collectionReference.add(song).addOnSuccessListener {
                                hideProgressBar()
                                //startActivity(Intent(this, ProfilePageActivity::class.java))
                                finish()
                            }.addOnFailureListener{
                                Toast.makeText(this, "Failed to upload song", Toast.LENGTH_SHORT).show()
                                hideProgressBar()
                            }
                        }
                    }.addOnFailureListener{
                        Toast.makeText(this, "Failed to upload song", Toast.LENGTH_SHORT).show()
                        hideProgressBar()
                    }
                }
            }.addOnFailureListener{
                Toast.makeText(this, "Failed to upload song", Toast.LENGTH_SHORT).show()
                hideProgressBar()
            }
        }
    }

    private fun validateForm(title: String, singer: String, genre: String, lyrics: String): Boolean {
        return when{
            TextUtils.isEmpty(title) ->{
                binding?.tilSongName?.error = "Enter title of song"
                false
            }
            TextUtils.isEmpty(singer) ->{
                binding?.tilArtistName?.error = "Enter artist name"
                binding?.tilSongName?.error = null
                false
            }
            TextUtils.isEmpty(genre) ->{
                binding?.tilSongGenre?.error = "Enter Song Genre"
                binding?.tilSongName?.error = null
                binding?.tilArtistName?.error = null
                false
            }
            TextUtils.isEmpty(lyrics) ->{
                binding?.tilSongLyrics?.error = "Enter lyrics"
                binding?.tilSongName?.error = null
                binding?.tilArtistName?.error = null
                binding?.tilSongGenre?.error = null
                false
            }
            else ->{
                binding?.tilSongName?.error = null
                binding?.tilArtistName?.error = null
                binding?.tilSongGenre?.error = null
                binding?.tilSongLyrics?.error = null
                true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            1 -> {
                // Handle image selection
                if (resultCode == RESULT_OK && data != null) {
                    imageURI = data.data!!
                    binding?.postImageView?.visibility = View.VISIBLE
                    binding?.postSongImageButton?.visibility = View.GONE
                    binding?.postImageView?.setImageURI(imageURI)
                }
            }
            2 -> {
                // Handle song selection
                if (resultCode == Activity.RESULT_OK && data != null) {
                    songURI = data.data!!
                    binding?.btnGetSong?.visibility = View.GONE
                    binding?.songSelected?.visibility = View.VISIBLE

                }
            }
        }
    }


}