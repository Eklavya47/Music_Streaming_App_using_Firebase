package com.company.miniproject1.activity

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.company.miniproject1.CurrentSongManager
import com.company.miniproject1.MediaPlayerSingleton
import com.company.miniproject1.QueueManager
import com.company.miniproject1.databinding.ActivityMusicPageBinding
import com.company.miniproject1.model.RecentlyPlayedSong
import com.company.miniproject1.model.Song
import java.util.concurrent.TimeUnit


class Music_Page_Activity : AppCompatActivity() {
    private var binding: ActivityMusicPageBinding? = null
    private var oneTimeOnly: Int? = null
    private var currentTime: Double = 0.0
    private var finalTime: Double = 0.0
    private var handler: Handler = Handler()
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPageBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        mediaPlayer = MediaPlayerSingleton.getInstance()

        // receiving data from main activity
        val receivedSong = intent.getParcelableExtra<Song>("currentSong")
        val receivedRecentSong = intent.getParcelableExtra<RecentlyPlayedSong>("currentRecentSong")

        val currentSong = receivedSong ?: receivedRecentSong?.toSong()

        //startTime = intent.getDoubleExtra("startTime", 0.0)
        currentTime = mediaPlayer!!.currentPosition.toDouble()
        finalTime = intent.getDoubleExtra("finalTime", 0.0)

        // binding received data
        binding?.currentSong = currentSong
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
        binding?.songPageSeekBar?.isClickable =true
        if (oneTimeOnly == 0){
            binding?.songPageSeekBar?.max = finalTime.toInt()
            oneTimeOnly = 1
        }

        // for adding seekbar functionality
        binding?.songPageSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

        binding?.songPagePauseButton?.setOnClickListener {
            binding?.songPagePauseButton?.visibility = View.GONE
            binding?.songPagePlayButton?.visibility = View.VISIBLE
            mediaPlayer?.pause()
        }
        binding?.songPagePlayButton?.setOnClickListener {
            binding?.songPagePlayButton?.visibility = View.GONE
            binding?.songPagePauseButton?.visibility = View.VISIBLE
            mediaPlayer?.start()
        }

        // for updating current song time
        handler.postDelayed(UpdateSongTime, 100)

        mediaPlayer?.setOnCompletionListener {
            if(mediaPlayer != null && mediaPlayer!!.isPlaying){
                binding?.songPagePauseButton?.visibility = View.VISIBLE
                binding?.songPagePlayButton?.visibility = View.GONE
            }else{
                binding?.songPagePauseButton?.visibility = View.GONE
                binding?.songPagePlayButton?.visibility = View.VISIBLE
            }
        }

        // for going to lyrics page
        binding?.lyricsCardView?.setOnClickListener{
            val intent = Intent(this, LyricsPageActivity::class.java)
            intent.putExtra("currentSong", currentSong)
            intent.putExtra("finalTime", mediaPlayer?.duration?.toDouble())
            startActivity(intent)
        }
        binding?.musicPageQueueButton?.setOnClickListener{
            val intent = Intent(this, QueuePage::class.java)
            intent.putExtra("currentSong", currentSong)
            intent.putExtra("finalTime", mediaPlayer?.duration?.toDouble())
            startActivity(intent)
        }
        binding?.nextSong?.setOnClickListener{
            playNextSong()
        }
    }

    private fun playNextSong() {
        val queue = QueueManager.getQueue()
        if (queue.isNotEmpty()) {
            val nextSong = queue[0]
            QueueManager.removeFromQueue(queue[0])
            mediaPlayer?.stop()
            mediaPlayer?.reset()

            // Set the data source to the next song and start playing
            try {
                mediaPlayer?.setDataSource(nextSong.songUrl)
                mediaPlayer?.prepare()
                mediaPlayer?.start()

                // Update UI with the new song details
                binding?.currentSong = nextSong
                CurrentSongManager.setCurrentSong(nextSong)
                binding?.songPagePauseButton?.visibility = View.VISIBLE
                binding?.songPagePlayButton?.visibility = View.GONE
                binding?.songPageSeekBar?.progress = 0
                finalTime = mediaPlayer?.duration?.toDouble() ?: 0.0
                binding?.songDurationTv?.text = "" +
                        String.format(
                            "%d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                            TimeUnit.MILLISECONDS.toSeconds(
                                finalTime.toLong()
                            ) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    finalTime.toLong()
                                )
                            )
                        )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {

            Toast.makeText(this,"Queue is Empty",Toast.LENGTH_SHORT).show()
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

            binding?.songPageSeekBar?.progress = (currentTime / finalTime * 100).toInt()
            //Log.i("TAGY", currentTime.toInt().toString())
            handler.postDelayed(this, 100)

        }
    }

    override fun onStart() {
        super.onStart()
        // setting visibility of play pause button
        if(mediaPlayer != null && mediaPlayer!!.isPlaying){
            binding?.songPagePauseButton?.visibility = View.VISIBLE
            binding?.songPagePlayButton?.visibility = View.GONE
        }else{
            binding?.songPagePauseButton?.visibility = View.GONE
            binding?.songPagePlayButton?.visibility = View.VISIBLE
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(UpdateSongTime)
    }
}

