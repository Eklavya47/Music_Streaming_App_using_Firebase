package com.company.miniproject1.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.miniproject1.RoomDatabase.AppDatabase
import com.company.miniproject1.RoomDatabase.PlaylistDao
import com.company.miniproject1.RoomDatabase.PlaylistSongDao
import com.company.miniproject1.adapter.playlistSongRecyclerViewAdapter
import com.company.miniproject1.databinding.ActivityCreatePlaylistPageBinding
import com.company.miniproject1.databinding.CreatePlaylistRowBinding
import com.company.miniproject1.model.Playlist
import com.company.miniproject1.model.PlaylistSong
import com.company.miniproject1.model.Song
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class createPlaylistPage : AppCompatActivity() {
    private lateinit var dataObject: AppDatabase
    private lateinit var playlistDao: PlaylistDao
    private lateinit var playlistSongDao: PlaylistSongDao
    private var binding: ActivityCreatePlaylistPageBinding? = null
    private var playlistSongBinding: CreatePlaylistRowBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: playlistSongRecyclerViewAdapter
    lateinit var songList: MutableList<Song>
    lateinit var selectedSongsList: MutableList<PlaylistSong>
    lateinit var selectedSongs: MutableList<Song>
    private var db = FirebaseFirestore.getInstance()
    private var collectionReference: CollectionReference = db.collection("songs")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePlaylistPageBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth
        songList = arrayListOf<Song>()
        selectedSongs = arrayListOf<Song>()
        selectedSongsList = arrayListOf<PlaylistSong>()

        dataObject = AppDatabase.getInstance(applicationContext)
        playlistDao = dataObject.playlistDao()
        playlistSongDao = dataObject.playlistSongDao()

        showSongs()

        binding?.createPlaylistButton?.setOnClickListener{
            var playlistName = binding?.etPlaylistName?.text.toString()
            if (playlistName.isNotEmpty()) {
                createPlaylist(playlistName)
            } else {
                Toast.makeText(this, "Enter a valid playlist name", Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun createPlaylist(playlistName: String) {
        val userId = auth.currentUser?.uid.toString()
        GlobalScope.launch(Dispatchers.IO) {
            val randomSong = selectedSongs.random()
            val newPlaylistId = playlistDao.insert(Playlist(userId = userId, name = playlistName, imageUrl = randomSong.imageUrl))
            for (selectedSong in selectedSongs){
                val playlistSong = selectedSong.toPlaylistSong(newPlaylistId, selectedSong)
                playlistSongDao.insert(playlistSong)
            }
            withContext(Dispatchers.IO){
                Toast.makeText(this@createPlaylistPage, "Playlist created successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showSongs() {
        collectionReference.get().addOnSuccessListener{
            if (!it.isEmpty){
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
                adapter = playlistSongRecyclerViewAdapter(this, songList)
                binding?.createPlaylistSongsRecyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding?.createPlaylistSongsRecyclerView?.adapter = adapter
                adapter.notifyDataSetChanged()

                adapter.setOnItemClickListener(object : playlistSongRecyclerViewAdapter.onItemClickListener{
                    override fun onItemClick(position: Int) {
                        playlistSongBinding?.playlistSongAdd?.visibility = View.GONE
                        playlistSongBinding?.playlistSongAdded?.visibility = View.VISIBLE
                        val song = songList[position]
                        selectedSongs.add(song)

                    }
                })
            }
        }
    }
}