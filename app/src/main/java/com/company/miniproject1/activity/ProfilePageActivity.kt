package com.company.miniproject1.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.miniproject1.R
import com.company.miniproject1.adapter.uploadedSongsAdapter
import com.company.miniproject1.databinding.ActivityProfilePageBinding
import com.company.miniproject1.model.Song
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class ProfilePageActivity : AppCompatActivity() {
    private var binding: ActivityProfilePageBinding? = null
    private lateinit var uploadedSongsAdapter: uploadedSongsAdapter
    private lateinit var auth: FirebaseAuth
    lateinit var songList: MutableList<Song>
    private var db = FirebaseFirestore.getInstance()
    private var collectionReference: CollectionReference = db.collection("songs")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth
        val userName = auth.currentUser?.displayName
        supportActionBar?.title = "Welcome $userName"

        songList = arrayListOf<Song>()
        showUserUploadedSongs()
    }

    private fun showUserUploadedSongs() {
        var currentUserId = auth.currentUser?.uid.toString()
        collectionReference.whereEqualTo("userId", currentUserId).get().addOnSuccessListener {
            if (!it.isEmpty){
                binding?.noSongs?.visibility = View.GONE
                binding?.songUploadedRecyclerView?.visibility = View.VISIBLE
                for (document in it){
                    var song = Song(
                        document.data.get("title").toString(),
                        document.data.get("imageUrl").toString(),
                        document.data.get("songUrl").toString(),
                        document.data.get("userId").toString(),
                        document.data.get("lyrics").toString(),
                        document.data.get("singer").toString(),
                        document.data.get("genre").toString()
                    )
                    songList.add(song)
                }
                // recyclerView
                uploadedSongsAdapter = uploadedSongsAdapter(this, songList)
                binding?.songUploadedRecyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding?.songUploadedRecyclerView?.adapter = uploadedSongsAdapter
                uploadedSongsAdapter.notifyDataSetChanged()
            }
        }.addOnFailureListener{
            Toast.makeText(this,"Something went Wrong! Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_page_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_uploadSong -> {
                startActivity(Intent(this, UploadSongsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}