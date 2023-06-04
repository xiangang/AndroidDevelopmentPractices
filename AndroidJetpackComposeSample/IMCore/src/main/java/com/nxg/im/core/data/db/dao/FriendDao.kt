package com.nxg.im.core.data.db.dao

import androidx.room.*
import com.nxg.im.core.data.db.entity.Friend
import com.nxg.mvvm.dao.IBaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao : IBaseDao<Friend, Long> {
    override val tableName: String
        get() = "friend"

    @Query("SELECT * from friend WHERE uuid =:uuid")
    suspend fun getFriend(uuid: Long): Friend

    @Query("SELECT * from friend")
    fun flowFriendList(): Flow<List<Friend>>

}