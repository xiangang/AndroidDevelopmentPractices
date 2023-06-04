package com.nxg.im.core.data.db.dao

import androidx.room.*
import com.nxg.im.core.data.db.entity.Message

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(vararg messages: Message)

    @Update
    suspend fun updateMessages(vararg messages: Message)

    @Delete
    suspend fun deleteMessages(vararg messages: Message)

    @Query("SELECT * from message WHERE (from_id =:fromId AND to_id =:toId OR from_id =:toId AND to_id =:fromId ) AND chat_type=:chatType")
    suspend fun loadMessages(fromId: Long, toId: Long, chatType: Int): List<Message>

}