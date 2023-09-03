package com.nxg.im.core.module.user

import com.nxg.im.core.IMService
import com.nxg.im.core.data.db.entity.Friend
import com.nxg.im.core.data.bean.Result

interface UserService : IMService {

    suspend fun getMe(): Result<User>

    suspend fun updateUserInfo(): Result<User>

    suspend fun getMyFriends(): Result<List<Friend>>


}