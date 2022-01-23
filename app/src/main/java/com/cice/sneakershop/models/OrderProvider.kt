package com.cice.sneakershop.models


import androidx.lifecycle.MutableLiveData
import com.cice.sneakershop.connections.FirebaseConnection


class OrderProvider {
    companion object {
        private val TAG = "OrderProvider"

        // Function that gets all the orders created by a user
        fun getOrders(userId: String, liveData: MutableLiveData<MutableList<Order>>){
            FirebaseConnection.getOrders(userId, liveData)
        }

        // Function that creates a new order with some items
        fun createOrder(userId: String, basketProducts: MutableList<BasketItem>, totalPrice: Double): Boolean {
            return FirebaseConnection.createOrder(userId, basketProducts, totalPrice)
        }
    }
}