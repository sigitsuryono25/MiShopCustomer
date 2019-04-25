package com.lauwba.surelabs.mishopcustomer.sqlite


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.lauwba.surelabs.mishopcustomer.chat.model.ItemChat
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
            return true
        } catch (e: Exception) {
            return false
        }

    }

    fun insertChat(itemChat: ItemChat?): Boolean? {
        try {
            database.execSQL(
                "INSERT INTO chat(message, isMe, timeStamp) VALUES('${itemChat?.message}'," +
                        "'${itemChat?.isMe}','${itemChat?.timeStamp}')"
            )
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
