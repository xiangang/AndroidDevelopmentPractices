package com.nxg.im.core.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nxg.im.core.data.db.dao.FriendDao
import com.nxg.im.core.data.db.dao.ConversationDao
import com.nxg.im.core.data.db.dao.MessageDao
import com.nxg.im.core.data.db.entity.Conversation
import com.nxg.im.core.data.db.entity.Friend
import com.nxg.im.core.data.db.entity.Message
import com.nxg.mvvm.logger.SimpleLogger


@Database(entities = [Conversation::class, Friend::class,Message::class], version = 1)
abstract class KtChatDatabase : RoomDatabase(), SimpleLogger {

    abstract fun conversationDao(): ConversationDao

    abstract fun friendDao(): FriendDao

    abstract fun messageDao(): MessageDao

    companion object {

        const val TAG = "KtChatDatabase"

        @Volatile
        private var instance: KtChatDatabase? = null

        fun getInstance(context: Context): KtChatDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): KtChatDatabase {
            return Room.databaseBuilder(context, KtChatDatabase::class.java, "kt_chat.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.i(TAG, "onCreate: ")
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        Log.i(TAG, "onOpen: ")
                    }
                })
                .build()
        }
    }


}