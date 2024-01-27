package com.example.reelapp.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.reelapp.ui.reel.data.ReelData


class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "TaskDatabase.db"
        const val DATABASE_VERSION = 1

        // table name
        const val TBL_REELS = "reels"

        // Reel table column
        const val REEl_ID = "reel_id"
        const val REEl_NAME = "name"
        const val REEl_VIDEO_URL = "video_url"
        const val REEl_VIEW_COUNT = "view_count"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        createReelTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TBL_REELS")
        onCreate(db)
    }

    private fun createReelTable(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TBL_REELS (
                $REEl_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $REEl_NAME TEXT,
                $REEl_VIDEO_URL TEXT,
                $REEl_VIEW_COUNT INTEGER
                )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    fun insertReelData(name: String, videoUrl: String, viewCount: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(REEl_NAME, name)
            put(REEl_VIDEO_URL, videoUrl)
            put(REEl_VIEW_COUNT, viewCount)
        }
        db.insert(TBL_REELS, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getAllData(): ArrayList<ReelData> {
        val resultList = ArrayList<ReelData>()

        val db = this.readableDatabase
        val cursor = db.rawQuery(
            """SELECT * FROM $TBL_REELS ORDER BY $REEl_VIEW_COUNT DESC""",
            null
        )

        try {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(cursor.getColumnIndex(REEl_ID))
                    val name = cursor.getString(cursor.getColumnIndex(REEl_NAME))
                    val videoUrl = cursor.getString(cursor.getColumnIndex(REEl_VIDEO_URL))
                    val viewCount = cursor.getInt(cursor.getColumnIndex(REEl_VIEW_COUNT))

                    resultList.add(ReelData(id, name, videoUrl, viewCount))

                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
            db.close()
        }
        return resultList
    }

    fun updateViewCount(reelId: String, count: Int) {
        val values = ContentValues().apply {
            put(REEl_VIEW_COUNT, count)
        }

        val db = writableDatabase
        db.update(TBL_REELS, values, "$REEl_ID = $reelId", null)
        db.close()
    }

}