package com.vishnu.aplay.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vishnu.aplay.videoplayer.model.VideoItem
import com.vishnu.aplay.youtubesection.model.YouTubeItemModel

class MainViewModel : ViewModel() {

    private val imageUrls = arrayOf(
        "https://www.flipthetruck.com/wp-content/uploads/2014/05/X-Men-Days-of-Future-Past-banner.jpg",
        "https://dyn1.heritagestatic.com/lf?set=path%5B2%2F2%2F3%2F6%2F3%2F22363856%5D%2Csizedata%5B850x600%5D&call=url%5Bfile%3Aproduct.chain%5D",
        "https://thesecondtake.com/wp-content/uploads/2014/05/Wallpaper_Latino_Transformers4_Mxtrailers.jpg",
        "https://movirulz.info/wp-content/uploads/2024/03/MoviRulz-42.jpg",
        "https://cdn.daily-sun.com/public/news_images/2020/05/05/Daily-sun-25.jpg"
    )
    private val videoList = MutableLiveData<List<VideoItem>>()
    private val youtubeVideoList = MutableLiveData<List<YouTubeItemModel>>()

    init {



        videoList.value = listOf(
            VideoItem("1", "https://www.classicgaming.cc/classics/dragons-lair/files/videos/stranger-things-season-2-trailer.mp4", "https://cdn.daily-sun.com/public/news_images/2020/05/05/Daily-sun-25.jpg"),
            VideoItem("2", "https://previews.customer.envatousercontent.com/h264-video-previews/1c67bf17-b0b0-444d-be9a-7369313bda2a/599548.mp4?_=2", "https://cdn.daily-sun.com/public/news_images/2020/05/05/Daily-sun-25.jpg"),
            VideoItem("3", "https://www.classicgaming.cc/classics/dragons-lair/files/videos/Dragons-Lair-Intro.mp4", "https://cdn.daily-sun.com/public/news_images/2020/05/05/Daily-sun-25.jpg"),
            VideoItem("4", "https://previews.customer.envatousercontent.com/h264-video-previews/1c67bf17-b0b0-444d-be9a-7369313bda2a/599548.mp4?_=2", "https://cdn.daily-sun.com/public/news_images/2020/05/05/Daily-sun-25.jpg")
        )

        youtubeVideoList.value = listOf(
            YouTubeItemModel("Video 1", "https://www.youtube.com/watch?v=3pljWSJh_X8", "https://images.pexels.com/photos/788946/pexels-photo-788946.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            YouTubeItemModel("Video 2", "https://www.youtube.com/watch?v=ErMSHiQRnc8", "https://img.freepik.com/free-vector/different-science-equipments-white-background_1308-98328.jpg?t=st=1719851820~exp=1719855420~hmac=d5f609afba7e395653cdcba208e094f40ecfe6fac79f759b415bdeca7c9d21dc&w=900"),
            YouTubeItemModel("Video 3", "https://www.youtube.com/watch?v=3pljWSJh_X8", "https://images.pexels.com/photos/788946/pexels-photo-788946.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            YouTubeItemModel("Video 4", "https://www.youtube.com/watch?v=ErMSHiQRnc8", "https://img.freepik.com/free-vector/different-science-equipments-white-background_1308-98328.jpg?t=st=1719851820~exp=1719855420~hmac=d5f609afba7e395653cdcba208e094f40ecfe6fac79f759b415bdeca7c9d21dc&w=900")
        )
    }

    private val _currentImageUrl = MutableLiveData<String>()
    val currentImageUrl: LiveData<String>
        get() = _currentImageUrl


    private var currentImageIndex = 0

    init {

        _currentImageUrl.value = imageUrls[currentImageIndex]
    }


    fun getNextImageUrl() {
        currentImageIndex++
        if (currentImageIndex >= imageUrls.size) {
            currentImageIndex = 0
        }
        _currentImageUrl.value = imageUrls[currentImageIndex]
    }

    fun getVideoList(): LiveData<List<VideoItem>> {
        return videoList
    }

    fun getYoutubeVideoList(): LiveData<List<YouTubeItemModel>> {
        return youtubeVideoList
    }
}
