package com.lauwba.surelabs.mishopcustomer.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase


/**
 * Created by Sigit Suryono on 07-Aug-17.
 */

class DeleteQuery(internal var c: Context) {
    internal var databaseHadler: DatabaseHandler
    internal var database: SQLiteDatabase

    init {
        databaseHadler = DatabaseHandler(c)
        database = databaseHadler.OpenDatabase()
    }

    fun deleteMessage() {
        try {
            database.execSQL("DELETE FROM chat")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
