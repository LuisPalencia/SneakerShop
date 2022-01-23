package com.cice.sneakershop.models

import androidx.lifecycle.MutableLiveData
import com.cice.sneakershop.Storage
import com.cice.sneakershop.connections.FirebaseConnection
import com.cice.sneakershop.constants.Constants


class SimpleSneakerProvider {

    companion object {
        private val TAG = "SimpleSneakerProvider"

        private val simpleSneakersRef = Storage.database.getReference(Constants.DB_REFERENCE_SIMPLE_SNEAKERS)

        fun getSimpleSneakersChild(liveData: MutableLiveData<MutableList<SimpleSneaker>>,
                                   liveDataFavourites: MutableLiveData<MutableList<SimpleSneaker>>,
                                   favouriteSneakers: MutableList<String>){
            FirebaseConnection.getSimpleSneakersChild(liveData, liveDataFavourites, favouriteSneakers)
        }

        // Function that gets the simple info of all the sneaker of the shop
        fun getSimpleSneakers(
            liveData: MutableLiveData<MutableList<SimpleSneaker>>,
            liveDataFavourites: MutableLiveData<MutableList<FavouriteProduct>>){

            FirebaseConnection.getSimpleSneakers(liveData, liveDataFavourites)
        }

        // Function that searchs for a simple sneaker with its ID
        fun getSimpleSneakerById(id: String): SimpleSneaker? {
            for(simpleSneaker in Storage.simpleSneakers){
                if (simpleSneaker.id == id){
                    return simpleSneaker
                }
            }

            return null
        }

        // Function that returns the small image of a simple sneaker
        fun getSmallImageSimpleSneaker(idSneaker: String): String?{
            for(simpleSneaker in Storage.simpleSneakers){
                if(idSneaker == simpleSneaker.id) return simpleSneaker.smallImageURL
            }
            return null
        }
    }
}