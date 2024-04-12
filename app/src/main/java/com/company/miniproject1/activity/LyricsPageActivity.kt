package com.company.miniproject1.activity

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import android.window.OnBackInvokedDispatcher
import com.company.miniproject1.MediaPlayerSingleton
import com.company.miniproject1.R
import com.company.miniproject1.databinding.ActivityLyricsPageBinding
import com.company.miniproject1.model.Song
import java.util.concurrent.TimeUnit

class LyricsPageActivity : AppCompatActivity() {
    private var binding: ActivityLyricsPageBinding? = null
    private var oneTimeOnly: Int? = null
    private var currentTime: Double = 0.0
    private var finalTime: Double = 0.0
    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLyricsPageBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        mediaPlayer = MediaPlayerSingleton.getInstance()

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
        binding?.lyricsPageSeekBar?.isClickable =true
        if (oneTimeOnly == 0){
            binding?.lyricsPageSeekBar?.max = finalTime.toInt()
            oneTimeOnly = 1
        }

        // for adding seekbar functionality
        binding?.lyricsPageSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Calculate the new position based on the progress
                    val newPosition = (progress / 100.0) * finalTime
                    mediaPlayer?.seekTo(newPosition.toInt())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // This method is called when the user starts touching the seekbar
                // You can add any additional actions if needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // This method is called when the user stops touching the seekbar
                // You can add any additional actions if needed
            }
        })

        binding?.lyricsPagePauseButton?.setOnClickListener {
            binding?.lyricsPagePauseButton?.visibility = View.GONE
            binding?.lyricsPagePlayButton?.visibility = View.VISIBLE
            mediaPlayer?.pause()
        }
        binding?.lyricsPagePlayButton?.setOnClickListener {
            binding?.lyricsPagePlayButton?.visibility = View.GONE
            binding?.lyricsPagePauseButton?.visibility = View.VISIBLE
            mediaPlayer?.start()
        }

        // for updating current song time
        handler.postDelayed(UpdateSongTime, 100)

        mediaPlayer?.setOnCompletionListener {
            if(mediaPlayer != null && mediaPlayer!!.isPlaying){
                binding?.lyricsPagePauseButton?.visibility = View.VISIBLE
                binding?.lyricsPagePlayButton?.visibility = View.GONE
            }else{
                binding?.lyricsPagePauseButton?.visibility = View.GONE
                binding?.lyricsPagePlayButton?.visibility = View.VISIBLE
            }
        }
    }

    // Creating the Runnable
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

            binding?.lyricsPageSeekBar?.progress = (currentTime / finalTime * 100).toInt()
            //Log.i("TAGY", currentTime.toInt().toString())
            handler.postDelayed(this, 100)

        }
    }

    override fun onStart() {
        super.onStart()
        // setting visibility of play pause button
        if(mediaPlayer != null && mediaPlayer!!.isPlaying){
            binding?.lyricsPagePauseButton?.visibility = View.VISIBLE
            binding?.lyricsPagePlayButton?.visibility = View.GONE
        }else{
            binding?.lyricsPagePauseButton?.visibility = View.GONE
            binding?.lyricsPagePlayButton?.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(UpdateSongTime)
    }

}