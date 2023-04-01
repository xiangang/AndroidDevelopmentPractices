package com.nxg.socketiomobile.data


data class Message(val author: String, val body: String)

object SampleData {

    val conversationSample: List<Message> = mutableListOf(
        Message("author", "message"),
        Message("author", "message"),
        Message("author", "message")
    )
}