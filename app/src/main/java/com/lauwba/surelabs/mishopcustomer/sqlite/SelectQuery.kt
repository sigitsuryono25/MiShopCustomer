package com.lauwba.surelabs.mishopcustomer.sqlite


import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.lauwba.surelabs.mishopcustomer.chat.model.ItemChat

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

    fun getAllChatList(): MutableList<ItemChat> {
        val res = mutableListOf<ItemChat>()
        val query = "SELECT * FROM chat ORDER BY id ASC "
        try {

            val cursor = database.rawQuery(query, null)
            try {

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        val item = ItemChat()
                        //only one column
                        item.isMe = cursor.getString(2)
                        item.message = cursor.getString(1)
                        item.timeStamp = cursor.getString(3)

                        //you could add additional columns here..

                        res.add(item)
                    } while (cursor.moveToNext())
                }

            } finally {
                try {
                    cursor.close()
                } catch (ignore: Exception) {
                }

            }

        } finally {

        }

        return res
    }

}



