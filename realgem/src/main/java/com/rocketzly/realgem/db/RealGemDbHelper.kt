package com.rocketzly.realgem.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * User: Rocket
 * Date: 2020/12/4
 * Time: 3:45 PM
 */
class RealGemDbHelper private constructor(context: Context) : SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "realGem.db"
        const val DATABASE_VERSION = 1
        private const val SQL_CREATE_TRANCE_LOG_TABLE =
                "CREATE TABLE ${RealGemContract.TraceLogEntry.TABLE_NAME} (" +
                        "${RealGemContract.TraceLogEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY," +
                        "${RealGemContract.TraceLogEntry.COLUMN_NAME_TIME} INTEGER," +
                        "${RealGemContract.TraceLogEntry.COLUMN_NAME_METHOD} TEXT," +
                        "${RealGemContract.TraceLogEntry.COLUMN_NAME_THREAD_ID} INTEGER," +
                        "${RealGemContract.TraceLogEntry.COLUMN_NAME_PROCESS_ID} INTEGER," +
                        "${RealGemContract.TraceLogEntry.COLUMN_NAME_THREAD_NAME} TEXT," +
                        "${RealGemContract.TraceLogEntry.COLUMN_NAME_VERSION} TEXT" +
                        ")"

        private var db: SQLiteDatabase? = null

        @Synchronized
        fun getDb(context: Context): SQLiteDatabase {
            if (db != null && db!!.isOpen) return db!!
            db?.close()
            db = RealGemDbHelper(context).writableDatabase
            return db!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TRANCE_LOG_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}