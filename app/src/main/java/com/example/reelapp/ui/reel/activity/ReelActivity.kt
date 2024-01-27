package com.example.reelapp.ui.reel.activity

import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.reelapp.R
import com.example.reelapp.databinding.ReelActivityBinding
import com.example.reelapp.db.DatabaseHelper
import com.example.reelapp.gone
import com.example.reelapp.ui.reel.adapter.ReelAdapter
import com.example.reelapp.ui.reel.data.ReelData
import com.example.reelapp.visible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ReelActivity : AppCompatActivity() {

    private lateinit var binding: ReelActivityBinding
    private val db = DatabaseHelper(this)
    private var firstVideo = 0

    private val reelAdapter by lazy {
        ReelAdapter(
            endVideoListener = { count, adapterPos ->
                onEndVideoListener(count, adapterPos)
            }
        )
    }

    private fun onEndVideoListener(count: Int, adapterPos: Int) {
        db.updateViewCount(reelAdapter.mList[adapterPos].id.toString(), count)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReelActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clickListeners()
        setAdapter()
    }

    private fun clickListeners() = with(binding) {
        imageViewAddReel.setOnClickListener { showDialog() }
    }

    private fun showDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.add_video_bottom_sheet, null)
        dialog.setCancelable(true)
        dialog.setContentView(view)

        val buttonAdd = view.findViewById<MaterialButton>(R.id.buttonAdd)
        val textInputEditTextName = view.findViewById<TextInputEditText>(R.id.TextInputEditTextName)
        val radioGroupVideoSelect =
            view.findViewById<RadioGroup>(R.id.radioGroupVideoSelect)
        val radioButtonVideo1 =
            view.findViewById<RadioButton>(R.id.radioButtonVideo1)

        var videoUrl = radioButtonVideo1.text.toString()

        radioGroupVideoSelect.setOnCheckedChangeListener { _, checkedId ->
            val rb = view.findViewById<RadioButton>(checkedId)
            videoUrl = rb.text.toString()
        }

        buttonAdd.setOnClickListener {
            val name = textInputEditTextName.text.toString().trim()
            db.insertReelData(name, videoUrl, "0")

            reelAdapter.mList.add(ReelData(name = name, videoUrl = videoUrl, viewCounts = 0))
            reelAdapter.notifyItemInserted(reelAdapter.mList.size)
            visibleShowNoDataFound()

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setAdapter() = with(binding) {
        recycleViewReel.adapter = reelAdapter
        reelAdapter.mList.clear()
        reelAdapter.mList.addAll(db.getAllData())
        reelAdapter.notifyDataSetChanged()

        visibleShowNoDataFound()

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recycleViewReel)

        recycleViewReel.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                reelAdapter.pauseCurrentPlayingVideo()
                val firstCompletedVisibleItem =
                    (binding.recycleViewReel.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                if (firstCompletedVisibleItem != -1) {
                    firstVideo = firstCompletedVisibleItem
                    reelAdapter.playIndexThenPausePreviousPlayer(firstCompletedVisibleItem)
                }
            }
        })
    }

    private fun visibleShowNoDataFound() = with(binding) {
        if (reelAdapter.mList.size == 0) {
            recycleViewReel.gone()
            textViewNoDataFound.visible()
        } else {
            textViewNoDataFound.gone()
            recycleViewReel.visible()
        }
    }

    override fun onPause() {
        super.onPause()
        reelAdapter.pauseCurrentPlayingVideo()
    }

    override fun onResume() {
        super.onResume()
        reelAdapter.playIndexThenPausePreviousPlayer(firstVideo)
    }

}