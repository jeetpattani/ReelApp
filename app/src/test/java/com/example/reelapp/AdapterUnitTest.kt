package com.example.reelapp

import androidx.recyclerview.widget.RecyclerView
import com.example.reelapp.ui.reel.adapter.ReelAdapter
import com.example.reelapp.ui.reel.data.ReelData
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations


class AdapterUnitTest {

    @Mock
    private val recyclerView: RecyclerView? = null

    private var reelAdapter: ReelAdapter? = null

    @Mock
    private lateinit var endVideoListener: (Int, Int) -> Unit

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        reelAdapter = ReelAdapter(endVideoListener)
    }

    @Test
    fun testInitialState() {
        assertNotNull(recyclerView)
        assertNotNull(reelAdapter)
        assertEquals(0, reelAdapter?.itemCount)
    }

    @Test
    fun testItemEquality() {
        val expectedItemId = "0"
        val testData = ReelData(expectedItemId, "Test Data")
        reelAdapter?.mList?.add(testData)

        val displayedItem: ReelData = reelAdapter!!.mList[0]
        assertEquals(expectedItemId, displayedItem.id)
    }

}