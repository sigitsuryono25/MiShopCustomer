package com.lauwba.surelabs.mishopcustomer.sqlite


import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

/**
 * Created by Sigit Suryono on 07-Aug-17.
 */

class SelectQuery(context: Context) {

    internal var databaseHadler: DatabaseHandler
    internal var database: SQLiteDatabase

    init {
        databaseHadler = DatabaseHandler(context)
        database = databaseHadler.OpenDatabase()
    }

    fun checkIfIdExist(id: String): Cursor? {
        return database.rawQuery("SELECT * FROM inbox WHERE id='$id'", null)
    }
}



