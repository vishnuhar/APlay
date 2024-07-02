package com.vishnu.aplay.videoplayer

import android.content.Context
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

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playerView: PlayerView = itemView.findViewById(R.id.player_view)
        private val thumbnailImage: ImageView = itemView.findViewById(R.id.thumbnail_image)

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
    }

    private fun playNextVideo() {
        playbackJob?.cancel() // Cancel any ongoing playback job

        playbackJob = coroutineScope.launch {
            delay(1000) // Optional: Add delay if needed between videos
            currentPlayerIndex++
            if (currentPlayerIndex < videoList.size) {
                notifyItemChanged(currentPlayerIndex - 1) // Update previous video item to show thumbnail
                notifyItemChanged(currentPlayerIndex) // Update next video item to play video
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
