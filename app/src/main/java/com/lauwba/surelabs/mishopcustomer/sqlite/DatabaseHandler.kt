package com.lauwba.surelabs.mishopcustomer.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class DatabaseHandler(context: Context) :
    SQLiteAssetHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    fun OpenDatabase(): SQLiteDatabase {
        return writableDatabase
    }

    companion object {


        private val DATABASE_NAME = "mishop.db"
        private val DATABASE_VERSION = 13
    }
}
