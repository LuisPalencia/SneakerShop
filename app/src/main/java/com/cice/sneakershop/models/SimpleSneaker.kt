package com.cice.sneakershop.models

// Model for a SimpleSneaker
data class SimpleSneaker(
    var id: String = "",
    var name: String = "",
    var price: Float = 0F,
    var discount: Int = 0,
    var brand: String = "",
    var color: List<String> = listOf(),
    var isFavourite: Boolean = false,
    var smallImageURL: String = "",
)

