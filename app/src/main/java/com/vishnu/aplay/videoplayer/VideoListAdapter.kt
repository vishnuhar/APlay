package com.vishnu.aplay.videoplayer

import android.content.Context
import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.vishnu.aplay.R
import com.vishnu.aplay.videoplayer.model.VideoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoListAdapter(private val context: Context, private val videoList: List<VideoItem>) :
    RecyclerView.Adapter<VideoListAdapter.VideoViewHolder>() {

    private var currentPlayerIndex = -1
    private val exoPlayers = mutableMapOf<Int, ExoPlayer>()
    private var playbackJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var isAppVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount(): Int = videoList.size

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnFocusChangeListener, View.OnKeyListener {
        private val playerView: PlayerView = itemView.findViewById(R.id.player_view)
        private val thumbnailImage: ImageView = itemView.findViewById(R.id.thumbnail_image)

        init {
            itemView.isFocusable = true
            itemView.isFocusableInTouchMode = true
            itemView.onFocusChangeListener = this
            itemView.setOnKeyListener(this)
        }



        fun bind(videoItem: VideoItem) {
            Glide.with(context).load(videoItem.thumbnailUrl).into(thumbnailImage)

            var exoPlayer = exoPlayers[bindingAdapterPosition]
            if (exoPlayer == null) {
                exoPlayer = ExoPlayer.Builder(context).build()
                exoPlayers[bindingAdapterPosition] = exoPlayer
                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == Player.STATE_ENDED) {
                            playNextVideo()
                        }
                    }
                })
            }

            playerView.player = exoPlayer

            if (bindingAdapterPosition == currentPlayerIndex) {
                val mediaItem = MediaItem.fromUri(videoItem.videoUrl)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = isAppVisible
                thumbnailImage.visibility = View.GONE
            } else {
                thumbnailImage.visibility = View.VISIBLE
                exoPlayer.stop()
            }
        }

        override fun onFocusChange(view: View?, hasFocus: Boolean) {
            if (hasFocus) {


                view?.let {
                    it.scaleX = 1.1f
                    it.scaleY = 1.1f
                }
            } else {
                view?.let {
                    it.scaleX = 1.0f
                    it.scaleY = 1.0f
                }
            }
        }

        override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event?.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                        playSelectedVideo(bindingAdapterPosition)
                        return true
                    }
                }
            }
            return false
        }
    }

    private fun playSelectedVideo(position: Int) {
        playbackJob?.cancel()
        currentPlayerIndex = position
        notifyItemChanged(currentPlayerIndex)
        val videoItem = videoList[position]
        val exoPlayer = exoPlayers[position]
        val mediaItem = MediaItem.fromUri(videoItem.videoUrl)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true
    }

    private fun playNextVideo() {
        playbackJob?.cancel()

        playbackJob = coroutineScope.launch {
            delay(1000)
            currentPlayerIndex++
            if (currentPlayerIndex < videoList.size) {
                notifyItemChanged(currentPlayerIndex - 1)
                notifyItemChanged(currentPlayerIndex)
                val nextVideoItem = videoList[currentPlayerIndex]
                val exoPlayer = exoPlayers[currentPlayerIndex]
                val mediaItem = MediaItem.fromUri(nextVideoItem.videoUrl)
                exoPlayer?.setMediaItem(mediaItem)
                exoPlayer?.prepare()
                exoPlayer?.playWhenReady = true
            }
        }
    }

    fun setAppVisibility(visible: Boolean) {
        isAppVisible = visible
        if (currentPlayerIndex == -1 && videoList.isNotEmpty()) {
            currentPlayerIndex = 0
            notifyItemChanged(currentPlayerIndex)
        }
        if (currentPlayerIndex != -1) {
            val exoPlayer = exoPlayers[currentPlayerIndex]
            exoPlayer?.playWhenReady = visible
        }
    }

    fun releaseAllPlayers() {
        playbackJob?.cancel()
        exoPlayers.values.forEach { it.release() }
    }
}
