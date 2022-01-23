package com.cice.sneakershop.models

import androidx.lifecycle.MutableLiveData
import com.cice.sneakershop.connections.FirebaseConnection

class SneakerProvider {

    companion object {
        private val TAG = "SneakerProvider"

        // Method that gets the info of a sneaker
        fun getSneakerByID(liveData: MutableLiveData<Sneaker>, simpleSneaker: SimpleSneaker){
            // Create the Sneaker object
            val sneaker = Sneaker()

            // Set the info of the sneaker contained in the SimpleSneaker object
            sneaker.apply {
                id = simpleSneaker.id
                name = simpleSneaker.name
                price = simpleSneaker.price
                discount = simpleSneaker.discount
                brand = simpleSneaker.brand
                color = simpleSneaker.color
                isFavourite = simpleSneaker.isFavourite
                smallImageURL = simpleSneaker.smallImageURL
            }

            // Get the extra info from Firebase
            FirebaseConnection.getSneakerByID(sneaker, liveData)

        }
    }
}