package com.victoris.neumorphism_chat.data

data class Chat(
    val id: String,
    val msg: String,
    val sender: String,
    val time: DateTime
) {
    constructor() : this("", "", "", DateTime(-1, -1))
}
