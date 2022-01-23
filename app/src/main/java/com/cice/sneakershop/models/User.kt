package com.cice.sneakershop.models

// Model for a user
data class User(
    var uuid: String = "",
    var name: String = "",
    //var surname: String = "",
    var favouriteProducts: MutableList<FavouriteProduct> = mutableListOf(),
    var basketProducts: MutableList<BasketItem> = mutableListOf()
)
