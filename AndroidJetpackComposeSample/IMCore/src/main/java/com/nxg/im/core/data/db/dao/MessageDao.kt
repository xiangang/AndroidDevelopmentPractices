package com.nxg.im.core.data.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.nxg.im.core.data.db.entity.Message

@Dao
interface MessageDao {

    @Insert()
    suspend fun insertMessage(message: Message): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMessage(message: Message)

    @Query("SELECT * from message WHERE uuid =:uuid AND from_id =:fromId AND to_id =:toId AND chat_type=:chatType")
    suspend fun queryMessage(uuid: Long, fromId: Long, toId: Long, chatType: Int): Message?

    @Query("SELECT * FROM message WHERE ((from_id =:fromId AND to_id =:toId ) OR (from_id =:toId AND to_id =:fromId ) )AND chat_type=:chatType order by id desc ")
    fun pagingSource(fromId: Long, toId: Long, chatType: Int): PagingSource<Int, Message>

    @Query("SELECT * FROM message WHERE from_id =:fromId AND to_id =:toId  AND chat_type=:chatType order by id desc limit 1 ")
    fun lastMessage(fromId: Long, toId: Long, chatType: Int): Message?

}