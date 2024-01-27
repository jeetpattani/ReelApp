package com.example.reelapp.ui.reel.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reelapp.R
import com.example.reelapp.databinding.RowReelBinding
import com.example.reelapp.ui.reel.data.ReelData
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util

class ReelAdapter(
    var endVideoListener: (count: Int, adapterPos: Int) -> Unit
) : RecyclerView.Adapter<ReelAdapter.ViewHolder>() {

    var mList = ArrayList<ReelData>()
    private var exoPlayerItemList: MutableMap<Int, ExoPlayer> = mutableMapOf()
    private var currentPlayingVideo: Pair<Int, ExoPlayer>? = null

    inner class ViewHolder(var binding: RowReelBinding) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var exoplayer: SimpleExoPlayer

        fun bind(item: ReelData) = with(binding) {
            textViewName.text = item.name
            textViewViews.text = "${item.viewCounts}\nViews"

            handleExoplayer(item)
        }

        private fun handleExoplayer(item: ReelData) {
            exoplayer = SimpleExoPlayer.Builder(binding.videoPlayer.context).build()
            exoplayer.playWhenReady = false
            exoplayer.repeatMode = Player.REPEAT_MODE_ALL
            binding.videoPlayer.player = exoplayer

            if (exoPlayerItemList.containsKey(absoluteAdapterPosition)) exoPlayerItemList.remove(
                absoluteAdapterPosition
            )
            exoPlayerItemList[absoluteAdapterPosition] = exoplayer

            val videoSource: MediaSource =
                ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                    .createMediaSource(MediaItem.fromUri(Uri.parse(item.videoUrl)))

            exoplayer.prepare(videoSource, true, false)

            exoplayer.addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT) {
                        val currentItem = mList[absoluteAdapterPosition]
                        currentItem.viewCounts = currentItem.viewCounts!! + 1
                        binding.textViewViews.text = "${currentItem.viewCounts.toString()}\nViews"
                        endVideoListener.invoke(currentItem.viewCounts!!, absoluteAdapterPosition)
                    }
                }
            })

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RowReelBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.binding.videoPlayer.player?.release()
        holder.binding.videoPlayer.player = null
        super.onViewRecycled(holder)
    }

    fun playIndexThenPausePreviousPlayer(index: Int) {
        pauseCurrentPlayingVideo()
        if (exoPlayerItemList[index]?.playWhenReady == false) {
            exoPlayerItemList[index]?.playWhenReady = true
            currentPlayingVideo = Pair(index, exoPlayerItemList[index]!!)
        }
    }

    fun pauseCurrentPlayingVideo() {
        if (currentPlayingVideo != null) {
            currentPlayingVideo?.second?.playWhenReady = false
        }
    }

}