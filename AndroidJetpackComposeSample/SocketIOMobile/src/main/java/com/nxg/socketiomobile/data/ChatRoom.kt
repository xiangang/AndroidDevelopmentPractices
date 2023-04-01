package com.nxg.socketiomobile.data

data class ChatRoom(val id: String, val name: String, val memberList: List<ChatRoomMember>)


data class ChatRoomMember(val id: String, val name: String)