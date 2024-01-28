package com.example.reelapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.reelapp.db.DatabaseHelper
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


@RunWith(AndroidJUnit4::class)
@LargeTest
class SQLiteTest {

    private lateinit var dbHelper: DatabaseHelper

    @Mock
    private lateinit var mockDb: SQLiteDatabase

    @Mock
    private lateinit var mockCursor: Cursor

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        val context = ApplicationProvider.getApplicationContext<Context>()
        dbHelper = DatabaseHelper(context)

        `when`(mockDb.rawQuery(Mockito.anyString(), Mockito.isNull())).thenReturn(mockCursor)
    }

    @Test
    fun testInsertReelData() {
        dbHelper.writableDatabase
        dbHelper.insertReelData("TestReel", "test_url", "5")

        mockDb.insert(
            DatabaseHelper.TBL_REELS,
            null,
            ContentValues().apply {
                put(DatabaseHelper.REEl_NAME, "TestReel")
                put(DatabaseHelper.REEl_VIDEO_URL, "test_url")
                put(DatabaseHelper.REEl_VIEW_COUNT, "5")
            }
        )

        mockDb.close()
    }

    @Test
    fun matchGetAllDataSize() {
        dbHelper.writableDatabase
        dbHelper.insertReelData("name1", "v1.mp4", "5")
        dbHelper.insertReelData("name2", "v2.mp4", "5")
        dbHelper.insertReelData("name3", "v3.mp4", "5")

        dbHelper.getAllData()
        dbHelper.close()

        Assert.assertEquals(3, dbHelper.getAllData().size)
    }

    @Test
    fun updateViewCount() {
        dbHelper.writableDatabase
        dbHelper.insertReelData("name1", "v1.mp4", "0") // id - 1
        dbHelper.insertReelData("name2", "v2.mp4", "0") // id - 2

        dbHelper.updateViewCount("1", 2)

        if (dbHelper.getAllData().first().viewCounts == 2) {
            assert(true)
        } else {
            assert(false)
        }
    }

    @Test
    fun testOnUpgrade() {
        dbHelper.onUpgrade(mockDb, 1, 2)

        mockDb.execSQL(eq("DROP TABLE IF EXISTS ${DatabaseHelper.TBL_REELS}"))

        mockDb.execSQL(
            eq(
                "CREATE TABLE ${DatabaseHelper.TBL_REELS} (" +
                        "${DatabaseHelper.REEl_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "${DatabaseHelper.REEl_NAME} TEXT, " +
                        "${DatabaseHelper.REEl_VIDEO_URL} TEXT, " +
                        "${DatabaseHelper.REEl_VIEW_COUNT} INTEGER)"
            )
        )
    }

}

