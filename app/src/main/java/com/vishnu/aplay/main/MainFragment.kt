package com.vishnu.aplay.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vishnu.aplay.R
import com.vishnu.aplay.videoplayer.VideoListAdapter
import com.vishnu.aplay.youtubesection.VideoListYoutubeAdapter

class MainFragment : Fragment() {
    private lateinit var rootView: View
    private lateinit var recyclerViewYoutube: RecyclerView
    private lateinit var recyclerViewVideo: RecyclerView

    private lateinit var viewImage:ImageView
    private lateinit var videoAdapter: VideoListAdapter
    private lateinit var youtubeVideoListAdapter: VideoListYoutubeAdapter

    private lateinit var viewModel: MainViewModel
    private lateinit var handler: Handler
    private val autoNextDelay: Long = 3000
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_main, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewImage = rootView.findViewById(R.id.imageViewBanner)
        initRecyclerViews()
        observeViewModel()
    }

    private fun initRecyclerViews() {
        recyclerViewVideo = rootView.findViewById(R.id.recycler_view_video)
        recyclerViewVideo.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        recyclerViewYoutube = rootView.findViewById(R.id.recyclerViewYoutube)
        recyclerViewYoutube.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
    }

    private fun observeViewModel() {
        handler = Handler(Looper.getMainLooper())


        viewModel.currentImageUrl.observe(viewLifecycleOwner, Observer { imageUrl ->
            loadImage(imageUrl)
        })


        startAutoNext()

        viewModel.getVideoList().observe(viewLifecycleOwner, Observer { videoList ->
            videoAdapter = VideoListAdapter(requireContext(), videoList)
            recyclerViewVideo.adapter = videoAdapter
        })

        viewModel.getYoutubeVideoList().observe(viewLifecycleOwner, Observer { youtubeVideoList ->
            youtubeVideoListAdapter = VideoListYoutubeAdapter(requireContext(), youtubeVideoList)
            recyclerViewYoutube.adapter = youtubeVideoListAdapter
        })
    }

    override fun onResume() {
        super.onResume()

        videoAdapter.setAppVisibility(true)
    }

    override fun onPause() {
        super.onPause()
        videoAdapter.setAppVisibility(false)
    }

    private fun loadImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .into(viewImage)
    }

    private fun startAutoNext() {
        handler.postDelayed({
            viewModel.getNextImageUrl()
            startAutoNext()
        }, autoNextDelay)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        videoAdapter.releaseAllPlayers()
        handler.removeCallbacksAndMessages(null)

    }
}