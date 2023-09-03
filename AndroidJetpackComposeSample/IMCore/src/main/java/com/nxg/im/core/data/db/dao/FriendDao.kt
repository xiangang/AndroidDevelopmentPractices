package com.nxg.im.core.data.db.dao

import androidx.room.*
import com.nxg.im.core.data.db.entity.Friend
import com.nxg.mvvm.dao.IBaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao : IBaseDao<Friend, Long> {
    override val tableName: String
        get() = "friend"

    @Query("SELECT * from friend WHERE user_id =:userId AND friend_id=:friendId ")
    suspend fun getFriend(userId: Long, friendId: Long): Friend?

    @Query("SELECT * from friend WHERE user_id =:userId")
    fun flowFriendList(userId: Long): Flow<List<Friend>>

}