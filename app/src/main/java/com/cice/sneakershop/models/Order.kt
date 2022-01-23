package com.cice.sneakershop.models

import java.util.*

// Model for an Order created in the app
data class Order(
    var orderId: String = "",
    var userId: String = "",
    var products: MutableList<OrderProduct> = mutableListOf(),
    var totalPrice: Double = 0.0,
    var dateOfOrder: String = ""
)