package com.company.miniproject1.activity

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.company.miniproject1.MediaPlayerSingleton
import com.company.miniproject1.R
import com.company.miniproject1.adapter.searchSongRecyclerViewAdapter
import com.company.miniproject1.databinding.ActivitySearchPageBinding
import com.company.miniproject1.model.Song

class SearchPageActivity : AppCompatActivity() {
    private var binding: ActivitySearchPageBinding? = null
    private lateinit var songList: List<Song>
    private lateinit var filteredSongs: List<Song>
    private var song: Song? = null
    private lateinit var searchSongadapter: searchSongRecyclerViewAdapter
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchPageBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        mediaPlayer = MediaPlayerSingleton.getInstance()

        // receiving data
        val songArray = intent.getParcelableArrayExtra("songList")
        songList = songArray?.map { it as Song } ?: emptyList()

        // set up recycler view
        searchSongadapter = searchSongRecyclerViewAdapter(this, emptyList())
        binding?.searchSongRecyclerView?.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding?.searchSongRecyclerView?.setHasFixedSize(true)
        binding?.searchSongRecyclerView?.adapter = searchSongadapter


        filteredSongs = songList

        val transparentOverlay: LinearLayout = findViewById(R.id.transparentOverlay)
        transparentOverlay.setOnClickListener {
            // Clicking on the overlay, focus the search view
            binding?.searchView?.isIconified = false

        }

        // Set up a query listener for the SearchView
        val searchView: SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
                if (!query.isNullOrBlank()) {
                    performSearch(query)
                }
                else {
                    binding?.cardView?.visibility = View.VISIBLE
                    searchSongadapter.updateList(emptyList()) // Clear the list when query is empty
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query changes
                // You can implement live search here if needed
                if (!newText.isNullOrBlank()) {
                    performSearch(newText)
                }
                else {
                    binding?.cardView?.visibility = View.VISIBLE
                    searchSongadapter.updateList(emptyList()) // Clear the list when query is empty
                }

                return true
            }
        })

        searchSongadapter.setOnItemClickListener(object : searchSongRecyclerViewAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                binding?.searchPageSongCardView?.visibility = View.VISIBLE
                binding?.searchPagePlayButton?.visibility = View.GONE
                binding?.searchPagePauseButton?.visibility = View.VISIBLE
                song = filteredSongs[position]
                binding?.song = song

                if (mediaPlayer == null) {
                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()

                    mediaPlayer = MediaPlayerSingleton.getInstance()
                    mediaPlayer?.setAudioAttributes(audioAttributes)
                    try {
                        // Set the data source to the song URL
                        mediaPlayer?.setDataSource(filteredSongs[position].songUrl)
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
                        mediaPlayer?.setDataSource(filteredSongs[position].songUrl)
                        mediaPlayer?.prepare()
                        mediaPlayer?.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                binding?.searchPagePauseButton?.setOnClickListener {
                    binding?.searchPagePauseButton?.visibility = View.GONE
                    binding?.searchPagePlayButton?.visibility = View.VISIBLE
                    mediaPlayer?.pause()
                }
                binding?.searchPagePlayButton?.setOnClickListener {
                    binding?.searchPagePlayButton?.visibility = View.GONE
                    binding?.searchPagePauseButton?.visibility = View.VISIBLE
                    mediaPlayer?.start()
                }
                binding?.searchPageSongCardView?.setOnClickListener{
                    val intent = Intent(this@SearchPageActivity,Music_Page_Activity::class.java)
                    intent.putExtra("currentSong", filteredSongs[position])
                    //intent.putExtra("startTime", mediaPlayer?.currentPosition?.toDouble())
                    intent.putExtra("finalTime", mediaPlayer?.duration?.toDouble())
                    startActivity(intent)
                }
            }
        })


    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@SearchPageActivity, MainActivity::class.java)
        intent.putExtra("currentPlayingSong", song)
        startActivity(intent)
        finish()
    }

    private fun performSearch(query: String) {
        binding?.cardView?.visibility = View.GONE
        filteredSongs = songList.filter { song ->
            song.title.contains(query, ignoreCase = true) ||
                    song.singer.contains(query, ignoreCase = true) ||
                    song.genre.contains(query, ignoreCase = true)
        }

        // Update the songRecyclerView with the filtered list
        searchSongadapter.updateList(filteredSongs)
    }

}
