package com.example.reelapp

import com.example.reelapp.ui.reel.data.ReelData
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class DataClassTest {

    @Test
    fun testEquals() {
        val reelOne = ReelData("1", "First", "one.mp4", 2)
        val reelTwo = ReelData("1", "First", "one.mp4", 2)
        val reelThree = ReelData("2", "Second", "one.mp4", 2)

        assertEquals(reelOne, reelTwo)
        assertNotEquals(reelOne, reelThree)
    }

    @Test
    fun testHashCode() {
        val reelOne = ReelData("1", "First", "one.mp4", 2)
        val reelTwo = ReelData("1", "First", "one.mp4", 2)
        val reelThree = ReelData("1", "Second", "one.mp4", 2)

        assertEquals(reelOne.hashCode(), reelTwo.hashCode())
        assertNotEquals(reelOne.hashCode(), reelThree.hashCode())
    }

    @Test
    fun testToString() {
        val reelData = ReelData(id = "1", null, null, null)
        val toStringResult = reelData.toString()
        assertEquals("ReelData(id=1, name=null, videoUrl=null, viewCounts=null)", toStringResult)
    }

}