package com.nxg.im.core.data.db.dao

import androidx.room.*
import com.nxg.im.core.data.db.entity.Conversation
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(vararg conversations: Conversation)

    @Update
    suspend fun updateConversations(vararg conversations: Conversation)

    @Delete
    suspend fun deleteConversations(vararg conversations: Conversation)

    @Query("SELECT * from conversation WHERE user_id =:user_id")
    suspend fun getConversations(user_id: Long): List<Conversation>

    @Query("SELECT * from conversation WHERE user_id =:user_id")
    fun flowConversationList(user_id: Long): Flow<List<Conversation>>


}