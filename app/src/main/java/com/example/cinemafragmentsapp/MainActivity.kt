package com.example.cinemafragmentsapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.SparseArray
import android.widget.Toast
import at.huber.youtubeExtractor.YouTubeExtractor
import com.google.android.exoplayer2.source.ExtractorMediaSource

import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.ExoPlayerFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val exoPlayer by lazy { ExoPlayerFactory.newSimpleInstance(this) }
    private var mediaSource: ExtractorMediaSource? = null
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
        object : YouTubeExtractor(this!!) {
            override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta) {
                val iTag = 18

                if (ytFiles?.get(iTag) != null && ytFiles?.get(iTag).url != null) {

                    val downloadUrl = ytFiles.get(iTag).url

                    val dataSourceFactory = DefaultDataSourceFactory(
                        this@MainActivity,
                        Util.getUserAgent(this@MainActivity, "cansu")
                    )

                    mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(downloadUrl))


                    exoPlayer.prepare(mediaSource, true, false)
                    exoPlayer.playWhenReady = true


                } else {

                    val toast =
                        Toast.makeText(this@MainActivity, "Video Başlatılamadı Tekrar Deneyin", Toast.LENGTH_LONG)
                    toast.show()
                }
            }
        }.extract("https://www.youtube.com/watch?v=$videoCode", true, true)
    }

    override fun onPause() {
        super.onPause()
        if (exoPlayer != null)
            exoPlayer.playWhenReady = false

    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }


}
