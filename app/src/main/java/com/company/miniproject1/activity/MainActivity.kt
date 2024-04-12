package com.company.miniproject1.activity

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.miniproject1.CurrentSongManager
import com.company.miniproject1.MediaPlayerSingleton
import com.company.miniproject1.QueueManager
import com.company.miniproject1.R
import com.company.miniproject1.Repository.SongRepository
import com.company.miniproject1.RoomDatabase.AppDatabase
import com.company.miniproject1.RoomDatabase.PlaylistDao
import com.company.miniproject1.RoomDatabase.RecentlyPlayedDatabase
import com.company.miniproject1.adapter.playlistRecyclerViewAdapter
import com.company.miniproject1.adapter.recentlyPlayedRecyclerViewAdapter
import com.company.miniproject1.adapter.songRecyclerViewAdapter
import com.company.miniproject1.databinding.ActivityMainBinding
import com.company.miniproject1.model.RecentlyPlayedSong
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
import java.util.Calendar

class MainActivity() : AppCompatActivity() {
    private val queue = QueueManager
    private val songRepository = SongRepository()
    private lateinit var recommendedSong: LiveData<Song>
    private lateinit var dataObject1: AppDatabase
    private lateinit var playlistDao: PlaylistDao
    private lateinit var dataObject: RecentlyPlayedDatabase
    private var binding: ActivityMainBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var songAdapter: songRecyclerViewAdapter
    private lateinit var playlistAdapter: playlistRecyclerViewAdapter
    private lateinit var recentAdapter: recentlyPlayedRecyclerViewAdapter
    lateinit var songList: MutableList<Song>
    private lateinit var loadedSongs: MutableSet<String>
    private var db = FirebaseFirestore.getInstance()
    private var mediaPlayer: MediaPlayer? = null
    private var currentSong: CurrentSongManager? = null
    private var collectionReference: CollectionReference = db.collection("songs")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth
        dataObject = RecentlyPlayedDatabase.getInstance(applicationContext)
        dataObject1 = AppDatabase.getInstance(applicationContext)
        playlistDao = dataObject1.playlistDao()









        songList = arrayListOf<Song>()
        loadedSongs = mutableSetOf()
        // showing songs which are in the database


        // to change the play pause image on song completion
        mediaPlayer?.setOnCompletionListener {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                binding?.homePagePauseButton?.visibility = View.VISIBLE
                binding?.homePagePlayButton?.visibility = View.GONE
            } else {
                binding?.homePagePauseButton?.visibility = View.GONE
                binding?.homePagePlayButton?.visibility = View.VISIBLE
            }
        }
        binding?.menuIcon?.setOnClickListener {
            showPopUpMenu(it)
        }

    }

    private fun showPopUpMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.playlist_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.playlist_create -> {
                    startActivity(Intent(this, createPlaylistPage::class.java))
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }


    private fun showSongsFromDatabase() {
        // Showing songs from database
        collectionReference.get().addOnSuccessListener {
            if (!it.isEmpty) {
                val newSongs = mutableListOf<Song>()
                for (document in it) {
                    val songId = document.id
                    if (!loadedSongs.contains(songId)) {
                        loadedSongs.add(songId)
                        var song = Song(
                            document.data.get("title").toString(),
                            document.data.get("imageUrl").toString(),
                            document.data.get("songUrl").toString(),
                            document.data.get("userId").toString(),
                            document.data.get("lyrics").toString(),
                            document.data.get("singer").toString(),
                            document.data.get("genre").toString()
                        )
                        newSongs.add(song)
                    }
                }
                if (newSongs.isNotEmpty()) {
                    songList.addAll(newSongs)
                }
                // recycler view
                songAdapter = songRecyclerViewAdapter(this, songList)
                binding?.songRecyclerView?.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding?.songRecyclerView?.adapter = songAdapter
                songAdapter.notifyDataSetChanged()


                GlobalScope.launch {
                    val recentlyPlayedSongs =
                        withContext(Dispatchers.IO) {
                            dataObject.recentlyPlayedSongDao().getRecentlyPlayedSongs(limit = 10)
                        }
                    withContext(Dispatchers.Main) {
                        // Update UI on the main thread with the recently played songs
                        recentAdapter = recentlyPlayedRecyclerViewAdapter(
                            this@MainActivity,
                            recentlyPlayedSongs
                        )

                        binding?.recentlyPlayedRecyclerView?.layoutManager = LinearLayoutManager(
                            this@MainActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        binding?.recentlyPlayedRecyclerView?.adapter = recentAdapter
                        recentAdapter.notifyDataSetChanged()

                        recentAdapter.setOnItemClickListener(object :
                            recentlyPlayedRecyclerViewAdapter.onItemClickListener {
                            override fun onItemClick(position: Int) {
                                binding?.homePageSongCardView?.visibility = View.VISIBLE
                                binding?.homePagePlayButton?.visibility = View.GONE
                                binding?.homePagePauseButton?.visibility = View.VISIBLE
                                val recentSong = recentlyPlayedSongs[position]
                                currentSong?.setCurrentSong(recentSong.toSong())
                                GlobalScope.launch(Dispatchers.Main) {
                                    addToQueue(recentSong.toSong())
                                }
                                binding?.homePageSongName?.text = recentSong.songTitle
                                binding?.homePageSongImage?.setImageURI(Uri.parse(recentSong.imageUrl))
                                if (mediaPlayer == null) {
                                    val audioAttributes = AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .build()

                                    mediaPlayer = MediaPlayerSingleton.getInstance()
                                    mediaPlayer?.setAudioAttributes(audioAttributes)
                                    try {
                                        // Set the data source to the song URL
                                        mediaPlayer?.setDataSource(recentlyPlayedSongs[position].songUrl)
                                        mediaPlayer?.prepare()
                                        mediaPlayer?.start()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    mediaPlayer?.stop()
                                    mediaPlayer?.reset()

                                    // Set the data source to the new song and start playing
                                    try {
                                        mediaPlayer?.setDataSource(recentlyPlayedSongs[position].songUrl)
                                        mediaPlayer?.prepare()
                                        mediaPlayer?.start()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                binding?.homePagePauseButton?.setOnClickListener {
                                    binding?.homePagePauseButton?.visibility = View.GONE
                                    binding?.homePagePlayButton?.visibility = View.VISIBLE
                                    mediaPlayer?.pause()
                                }
                                binding?.homePagePlayButton?.setOnClickListener {
                                    binding?.homePagePlayButton?.visibility = View.GONE
                                    binding?.homePagePauseButton?.visibility = View.VISIBLE
                                    mediaPlayer?.start()
                                }
                                binding?.homePageSongCardView?.setOnClickListener {
                                    val intent =
                                        Intent(this@MainActivity, Music_Page_Activity::class.java)
                                    intent.putExtra(
                                        "currentRecentSong",
                                        recentlyPlayedSongs[position]
                                    )
                                    //intent.putExtra("startTime", mediaPlayer?.currentPosition?.toDouble())
                                    intent.putExtra("finalTime", mediaPlayer?.duration?.toDouble())
                                    startActivity(intent)
                                }
                            }

                            override fun onItemMenuClick(position: Int, view: View) {
                                showSongPopupMenu(view, position)
                            }
                        })
                    }
                }


                songAdapter.setOnItemClickListener(object :
                    songRecyclerViewAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        binding?.homePageSongCardView?.visibility = View.VISIBLE
                        binding?.homePagePlayButton?.visibility = View.GONE
                        binding?.homePagePauseButton?.visibility = View.VISIBLE
                        val song = songList[position]
                        currentSong?.setCurrentSong(song)
                        binding?.song = song
                        GlobalScope.launch(Dispatchers.Main) {
                            addToQueue(song)
                        }

                        val recentlyPlayedSong = RecentlyPlayedSong(
                            0,
                            song.title,
                            song.imageUrl,
                            song.songUrl,
                            song.userId,
                            song.lyrics,
                            song.genre,
                            song.singer,
                            System.currentTimeMillis()
                        )
                        GlobalScope.launch(Dispatchers.IO) {
                            val isSongAlreadyInDatabase = dataObject.recentlyPlayedSongDao()
                                .isSongAlreadyInDatabase(song.songUrl)
                            if (!isSongAlreadyInDatabase) {
                                dataObject.recentlyPlayedSongDao().insert(recentlyPlayedSong)
                            }
                        }


                        if (mediaPlayer == null) {
                            val audioAttributes = AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()

                            mediaPlayer = MediaPlayerSingleton.getInstance()
                            mediaPlayer?.setAudioAttributes(audioAttributes)
                            try {
                                // Set the data source to the song URL
                                mediaPlayer?.setDataSource(songList[position].songUrl)
                                mediaPlayer?.prepare()
                                mediaPlayer?.start()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            mediaPlayer?.stop()
                            mediaPlayer?.reset()

                            // Set the data source to the new song and start playing
                            try {
                                mediaPlayer?.setDataSource(songList[position].songUrl)
                                mediaPlayer?.prepare()
                                mediaPlayer?.start()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        binding?.homePagePauseButton?.setOnClickListener {
                            binding?.homePagePauseButton?.visibility = View.GONE
                            binding?.homePagePlayButton?.visibility = View.VISIBLE
                            mediaPlayer?.pause()
                        }
                        binding?.homePagePlayButton?.setOnClickListener {
                            binding?.homePagePlayButton?.visibility = View.GONE
                            binding?.homePagePauseButton?.visibility = View.VISIBLE
                            mediaPlayer?.start()
                        }
                        binding?.homePageSongCardView?.setOnClickListener {
                            val intent = Intent(this@MainActivity, Music_Page_Activity::class.java)
                            intent.putExtra("currentSong", songList[position])
                            //intent.putExtra("startTime", mediaPlayer?.currentPosition?.toDouble())
                            intent.putExtra("finalTime", mediaPlayer?.duration?.toDouble())
                            startActivity(intent)
                        }
                    }

                    override fun onItemMenuClick(position: Int, view: View) {
                        showSongPopupMenu(view, position)
                    }
                })
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Something went Wrong! Please try again.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showSongPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.queue_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.addToQueue -> {
                    // Handle add to queue action for the item at the given position
                    QueueManager.addToQueue(songList[position])
                    Toast.makeText(this,"Added to Queue",Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }


    private fun setTitleAccordingToTime() {
        // Get the current time
        val calendar = Calendar.getInstance()
        val timeOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        val appTitle: String = when {
            timeOfDay in 0..11 -> getString(R.string.app_name_morning)
            timeOfDay in 12..17 -> getString(R.string.app_name_afternoon)
            else -> getString(R.string.app_name_evening)
        }

        supportActionBar?.title = appTitle

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_page_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_signout -> {
                if (auth.currentUser != null) {
                    auth.signOut()
                    startActivity(Intent(this, Log_in_activity::class.java))
                    finish()
                }
            }

            R.id.action_profile -> {
                startActivity(Intent(this, ProfilePageActivity::class.java))
            }

            R.id.action_search -> {
                val intent = Intent(this, SearchPageActivity::class.java)
                intent.putExtra("songList", songList.toTypedArray())
                startActivity(intent)
                finish()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        // Setting title of app according to time for greetings
        setTitleAccordingToTime()
        showSongsFromDatabase()
        showPlaylistsFromDatabase()


        if (intent.hasExtra("currentPlayingSong")) {
            mediaPlayer = MediaPlayerSingleton.getInstance()
            val currentPlayingSong = intent.getParcelableExtra<Song>("currentPlayingSong")

            if (currentPlayingSong != null) {
                binding?.homePageSongCardView?.visibility = View.VISIBLE
                binding?.homePagePlayButton?.visibility = View.GONE
                binding?.homePagePauseButton?.visibility = View.VISIBLE
                binding?.song = currentPlayingSong
            }

            // Rest of the code to handle the MediaPlayer and UI updates
            binding?.homePagePauseButton?.setOnClickListener {
                binding?.homePagePauseButton?.visibility = View.GONE
                binding?.homePagePlayButton?.visibility = View.VISIBLE
                mediaPlayer?.pause()
            }
            binding?.homePagePlayButton?.setOnClickListener {
                binding?.homePagePlayButton?.visibility = View.GONE
                binding?.homePagePauseButton?.visibility = View.VISIBLE
                mediaPlayer?.start()
            }
            binding?.homePageSongCardView?.setOnClickListener {
                val intent = Intent(this@MainActivity, Music_Page_Activity::class.java)
                intent.putExtra("currentSong", currentPlayingSong)
                //intent.putExtra("startTime", mediaPlayer?.currentPosition?.toDouble())
                intent.putExtra("finalTime", mediaPlayer?.duration?.toDouble())
                startActivity(intent)
            }

            // Remove the extra information to avoid processing it again on subsequent starts
            intent.removeExtra("currentPlayingSong")
        } else {
            if (currentSong?.getCurrentSong() != null) {
                binding?.homePageSongCardView?.visibility = View.VISIBLE
                binding?.homePagePlayButton?.visibility = View.GONE
                binding?.homePagePauseButton?.visibility = View.VISIBLE
                binding?.song = currentSong?.getCurrentSong()
            } else {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    binding?.homePagePauseButton?.visibility = View.VISIBLE
                    binding?.homePagePlayButton?.visibility = View.GONE
                } else {
                    binding?.homePagePauseButton?.visibility = View.GONE
                    binding?.homePagePlayButton?.visibility = View.VISIBLE
                }
            }
        }
    }

        suspend fun addToQueue(song: Song) {
            /*val recommendedSong = songRepository.getSongsFromApi(songName)
            Log.i("TAGY", recommendedSong.toString())
            recommendedSong?.let {
                val song = songList.find { it.title == recommendedSong.recommendedSong }
                if (song != null) {
                    QueueManager.addToQueue(song)
                }
                // Notify UI or perform other actions as needed
            } ?: run {
                // Handle the case where the API call fails or doesn't return a valid song
                Toast.makeText(this, "Failed to add song to the queue", Toast.LENGTH_SHORT).show()
            }*/
            // Get all songs with the same genre
            val songsWithSameGenre = songList.filter { it.genre == song.genre && it != song }

            // Shuffle the list to get a random order
            val shuffledSongs = songsWithSameGenre.shuffled()

            // Take the first three songs (or fewer if there are less than three)
            val selectedSongs = shuffledSongs.take(3)

            // Add the selected songs to the queue
            selectedSongs.forEach { QueueManager.addToQueue(it) }

            // Notify UI or perform other actions as needed
            Toast.makeText(this, "Added songs to the queue", Toast.LENGTH_SHORT).show()
        }

        private fun showPlaylistsFromDatabase() {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                GlobalScope.launch(Dispatchers.IO) {
                    val userPlaylists = playlistDao.getPlaylists(userId)
                    withContext(Dispatchers.Main) {
                        if (userPlaylists.isNotEmpty()) {
                            binding?.cardView?.visibility = View.GONE
                            playlistAdapter =
                                playlistRecyclerViewAdapter(this@MainActivity, userPlaylists)
                            binding?.playlistRecyclerView?.layoutManager = LinearLayoutManager(
                                this@MainActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            binding?.playlistRecyclerView?.adapter = playlistAdapter
                            playlistAdapter.notifyDataSetChanged()
                        } else {
                            binding?.cardView?.visibility = View.VISIBLE
                            playlistAdapter =
                                playlistRecyclerViewAdapter(this@MainActivity, emptyList())
                            binding?.playlistRecyclerView?.layoutManager = LinearLayoutManager(
                                this@MainActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            binding?.playlistRecyclerView?.adapter = playlistAdapter
                            playlistAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }