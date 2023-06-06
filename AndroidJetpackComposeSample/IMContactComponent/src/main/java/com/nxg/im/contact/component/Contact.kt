package com.nxg.im.contact.component

import com.nxg.im.core.data.db.entity.Friend

sealed class Contact {

    data class ContactVerifyMsg(val data: List<VerifyMsg>) : Contact()

    data class ContactBackList(val data: List<BackList>) : Contact()

    data class ContactGroupChat(val data: List<GroupChat>) : Contact()

    data class ContactFriendListHeader(val data: String) : Contact()

    data class ContactFriendList(val data: List<Friend>) : Contact()
}


class VerifyMsg

class BackList

class GroupChat

data class ContactDetail(val friend: Friend)