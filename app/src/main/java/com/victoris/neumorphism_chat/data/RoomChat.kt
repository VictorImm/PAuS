package com.victoris.neumorphism_chat.data

data class RoomChat(
    val id: Int,
    val pass: String,
    val owner: String,
    val capacity: Int
) {
    constructor() : this(-1, "", "", -1)
}
