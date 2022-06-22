package com.example.cinemafragmentsapp

import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.SparseArray
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val exoPlayer by lazy { ExoPlayer.Builder(this).build() }
    private var mediaSource: ProgressiveMediaSource? = null
    private var videoCode = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoCode = "td3Kd7fOROw"

        connectWithExtractor()

        playerView.player = exoPlayer
        playerControllerView.player = exoPlayer

        playerView.setOnTouchListener { _, _ ->

            if (playerControllerView.isVisible) {
                playerControllerView.hide()

            } else {
                playerControllerView.show()
            }
            false
        }

        val displayMetrics = DisplayMetrics()
        this.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        playerView.layoutParams.height = screenWidth * 9 / 16
    }

    private fun connectWithExtractor() {

        object : YouTubeExtractor(this.applicationContext) {
            override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?,
                                               videoMeta: VideoMeta?) {
                val iTag = 18

                if (ytFiles?.get(iTag) != null && ytFiles?.get(iTag).url != null) {

                    val downloadUrl = ytFiles.get(iTag).url

                    val dataSourceFactory = DefaultDataSourceFactory(
                        this@MainActivity,
                        Util.getUserAgent(this@MainActivity, "cansu")
                    )

                    mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(Uri.parse(downloadUrl)))


                    exoPlayer.setMediaSource(mediaSource!!)
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = true


                } else {

                    val toast =
                        Toast.makeText(this@MainActivity, "Video cannot be started. Please try again.", Toast.LENGTH_LONG)
                    toast.show()
                }
            }
        }.extract("https://www.youtube.com/watch?v=$videoCode")
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.playWhenReady = false

    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }


}
