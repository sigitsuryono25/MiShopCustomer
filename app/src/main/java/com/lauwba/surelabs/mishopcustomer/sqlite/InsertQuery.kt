package com.lauwba.surelabs.mishopcustomer.sqlite


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.lauwba.surelabs.mishopcustomer.dashboard.model.InboxModel

/**
 * Created by Sigit Suryono on 07-Aug-17.
 */

class InsertQuery(internal var c: Context) {
    internal var databaseHadler: DatabaseHandler
    internal var database: SQLiteDatabase

    init {
        databaseHadler = DatabaseHandler(c)
        database = databaseHadler.OpenDatabase()
    }

    fun InsertInbox(inboxModel: InboxModel): Boolean? {
        inboxModel.broadcaston
        try {
            database.execSQL(
                "INSERT INTO inbox VALUES(" +
                        "'${inboxModel.broadcaston}'," +
                        "'${inboxModel.foto}'," +
                        "'${inboxModel.id}'," +
                        "'${inboxModel.message}'," +
                        "'${inboxModel.to}'," +
                        "'${inboxModel.type}')"
            )
            Log.d(
                "QUERY INSERT", "INSERT INTO inbox VALUES(" +
                        "'${inboxModel.broadcaston}'," +
                        "'${inboxModel.foto}'," +
                        "'${inboxModel.id}'," +
                        "'${inboxModel.message}'," +
                        "'${inboxModel.to}'," +
                        "'${inboxModel.type}')"
            )
            return true
        } catch (e: Exception) {
            return false
        }

    }
}
