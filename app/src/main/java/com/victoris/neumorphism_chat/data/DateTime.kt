package com.victoris.neumorphism_chat.data

data class DateTime(
    val hour: Int,
    val min: Int
) {
    constructor() : this(-1, -1)
}
