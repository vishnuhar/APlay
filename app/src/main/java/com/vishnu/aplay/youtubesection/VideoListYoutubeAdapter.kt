package com.vishnu.aplay.youtubesection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vishnu.aplay.R
import com.vishnu.aplay.youtubesection.model.YouTubeItemModel

class VideoListYoutubeAdapter (private val context: Context, private val videoList: List<YouTubeItemModel>) :
    RecyclerView.Adapter<VideoListYoutubeAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_youtube_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount(): Int = videoList.size

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnailImage: ImageView = itemView.findViewById(R.id.thumbnail_image)
        private val webView: WebView = itemView.findViewById(R.id.webView1)


        fun bind(videoItem: YouTubeItemModel) {
            Glide.with(context).load(videoItem.thumbnailUrl).into(thumbnailImage)
            val webSettings: WebSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webView.webChromeClient = WebChromeClient()
            webView.webViewClient = WebViewClient()
            webView.loadUrl(videoItem.videoUrl)
        }
    }
}