package com.cice.sneakershop.models

// Model for a Sneaker
data class Sneaker(
    var id: String = "",
    var name: String = "",
    var price: Float = 0F,
    var discount: Int = 0,
    var brand: String = "",
    var color: List<String> = listOf(),
    var isFavourite: Boolean = false,
    var mainImageURL: String = "",
    var smallImageURL: String = "",
    var availableSizes: Map<String, String> = mapOf(), // Size / Number of products available
    var description: String = "",
    var totalReviews: Int = 0,
    var averageMarkReview: Double = 0.0,
    ){

}
