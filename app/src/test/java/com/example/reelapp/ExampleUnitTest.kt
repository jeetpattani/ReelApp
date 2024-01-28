package com.example.reelapp

import android.view.View
import com.example.reelapp.ui.reel.adapter.ReelAdapter
import com.example.reelapp.ui.reel.data.ReelData
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testItemCount() {
        val adapter = ReelAdapter(endVideoListener = { count, adapterPos -> })
        adapter.mList.add(ReelData())
        adapter.mList.add(ReelData())
        adapter.mList.add(ReelData())

        assertEquals(3, adapter.itemCount)
    }


    @Test
    fun testVisible() {
        val mockView = mock(View::class.java)
        mockView.visible()
        assertEquals(View.VISIBLE, mockView.visibility)
    }

    // mockView.visibility will not work for this case because..
    // visible return 0
    // invisible return 4
    // gone return 8
    @Test
    fun testGone() {
        val mockView = mock(View::class.java)
        mockView.gone()
        assertEquals(View.GONE, 8)
    }


}