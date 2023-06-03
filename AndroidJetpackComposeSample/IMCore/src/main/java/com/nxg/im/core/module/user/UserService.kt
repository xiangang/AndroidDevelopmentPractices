package com.nxg.im.core.module.user

import com.nxg.im.core.IMService
import com.nxg.im.core.data.Friend
import com.nxg.im.core.data.Result

interface UserService : IMService {


    suspend fun me(): Result<User>

    suspend fun updateUserInfo(): Result<User>

    suspend fun myFriends(): Result<List<Friend>>


}