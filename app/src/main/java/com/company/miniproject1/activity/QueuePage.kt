package com.company.miniproject1.activity

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.miniproject1.MediaPlayerSingleton
import com.company.miniproject1.QueueManager
import com.company.miniproject1.R
import com.company.miniproject1.adapter.QueueSongRecyclerViewAdapter
import com.company.miniproject1.databinding.ActivityQueuePageBinding
import com.company.miniproject1.model.Song
import java.util.concurrent.TimeUnit

class QueuePage : AppCompatActivity() {
    private lateinit var adapter: QueueSongRecyclerViewAdapter
    private var binding: ActivityQueuePageBinding? = null
    private var oneTimeOnly: Int? = null
    private var currentTime: Double = 0.0
    private var finalTime: Double = 0.0
    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler = Handler()
    lateinit var songList: MutableList<Song>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQueuePageBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        songList = arrayListOf<Song>()

        mediaPlayer = MediaPlayerSingleton.getInstance()

        songList = QueueManager.getQueue().toMutableList()
        adapter = QueueSongRecyclerViewAdapter(this, songList)
        binding?.queuePageRecyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.queuePageRecyclerView?.adapter = adapter
        adapter.notifyDataSetChanged()

        // receiving data
        val receivedSong = intent.getParcelableExtra<Song>("currentSong")
        currentTime = mediaPlayer!!.currentPosition.toDouble()
        finalTime = intent.getDoubleExtra("finalTime", 0.0)

        // binding data
        binding?.currentSong = receivedSong
        binding?.currentTimeTv?.text = currentTime.toString()
        binding?.songDurationTv?.text = "" +
                String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(
                        finalTime.toLong())
                            - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            finalTime.toLong()
                        )
                    )
                )

        // setting max limit for accessing the seekbar and making seekbar clickable
        binding?.queuePageSeekBar?.isClickable =true
        if (oneTimeOnly == 0){
            binding?.queuePageSeekBar?.max = finalTime.toInt()
            oneTimeOnly = 1
        }

        binding?.queuePageSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Calculate the new position based on the progress
                    val newPosition = (progress / 100.0) * finalTime
                    mediaPlayer?.seekTo(newPosition.toInt())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        binding?.queuePagePauseButton?.setOnClickListener {
            binding?.queuePagePauseButton?.visibility = View.GONE
            binding?.queuePagePlayButton?.visibility = View.VISIBLE
            mediaPlayer?.pause()
        }
        binding?.queuePagePlayButton?.setOnClickListener {
            binding?.queuePagePlayButton?.visibility = View.GONE
            binding?.queuePagePauseButton?.visibility = View.VISIBLE
            mediaPlayer?.start()
        }

        // for updating current song time
        handler.postDelayed(UpdateSongTime, 100)

        mediaPlayer?.setOnCompletionListener {
            if(mediaPlayer != null && mediaPlayer!!.isPlaying){
                binding?.queuePagePauseButton?.visibility = View.VISIBLE
                binding?.queuePagePlayButton?.visibility = View.GONE
            }else{
                binding?.queuePagePauseButton?.visibility = View.GONE
                binding?.queuePagePlayButton?.visibility = View.VISIBLE
            }
        }
    }

    val UpdateSongTime: Runnable = object : Runnable {
        override fun run() {
            currentTime = mediaPlayer?.currentPosition?.toDouble() ?: 0.0
            binding?.currentTimeTv?.text = "" +
                    String.format(
                        "%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(currentTime.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(
                            currentTime.toLong())
                                - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                currentTime.toLong()
                            )
                        )
                    )

            binding?.queuePageSeekBar?.progress = (currentTime / finalTime * 100).toInt()
            //Log.i("TAGY", currentTime.toInt().toString())
            handler.postDelayed(this, 100)

        }
    }

    override fun onStart() {
        super.onStart()
        // setting visibility of play pause button
        if(mediaPlayer != null && mediaPlayer!!.isPlaying){
            binding?.queuePagePauseButton?.visibility = View.VISIBLE
            binding?.queuePagePlayButton?.visibility = View.GONE
        }else{
            binding?.queuePagePauseButton?.visibility = View.GONE
            binding?.queuePagePlayButton?.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(UpdateSongTime)
    }

}
