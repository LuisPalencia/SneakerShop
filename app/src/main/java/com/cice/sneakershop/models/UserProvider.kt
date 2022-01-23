package com.cice.sneakershop.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cice.sneakershop.Storage
import com.cice.sneakershop.connections.FirebaseConnection


class UserProvider {

    companion object {
        private val TAG = "UserProvider"

        // Function that returns the current user object model
        fun getCurrentUser(): User{
            return Storage.user
        }

        // Function that gets the user info from Firebase with the UUID
        fun getUserByUUID(uuid: String, liveDataUser: MutableLiveData<User>, liveDataBasket: MutableLiveData<MutableList<BasketItem>>){
            FirebaseConnection.getUserByUUID(uuid, liveDataUser, liveDataBasket)
        }

        // Function that adds a new user to Firebase
        fun addNewUser(uuid: String, name: String){
            FirebaseConnection.addNewUser(uuid, name)
        }

        // Function that adds a sneaker to the favourite list of sneakers of the user
        fun addSneakerToFavouriteItems(
            simpleSneaker: SimpleSneaker,
            liveDataFavouriteSneakers: MutableLiveData<MutableList<FavouriteProduct>>
        ){
            //Insert the new value in the firebase DB
            val favouriteProduct = FirebaseConnection.addSneakerToFavouriteItems(simpleSneaker.id)
            if(favouriteProduct != null){
                // Set the simpleSneaker to favourite
                simpleSneaker.isFavourite = true
                // Add the favourite product to the user favourite products list
                Storage.user.favouriteProducts.add(favouriteProduct)
                //Notify the new value in the liveData of the favourite products list
                liveDataFavouriteSneakers.postValue(Storage.user.favouriteProducts)
            }
        }

        // Function that removes a sneaker from the favourite list of sneakers of the user
        fun removeSneakerFromFavouriteItems(simpleSneaker: SimpleSneaker, liveDataFavouriteSneakers: MutableLiveData<MutableList<FavouriteProduct>>){
            val favouriteProduct = getFavouriteProduct(simpleSneaker.id)

            if(favouriteProduct != null){
                // Disable the sneaker from the favourites list
                simpleSneaker.isFavourite = false
                Storage.user.favouriteProducts.remove(favouriteProduct)
                //Notify that a value has been removed from the liveData of the favourite products list
                liveDataFavouriteSneakers.postValue(Storage.user.favouriteProducts)

                // Remove the favourite sneaker from Firebase
                FirebaseConnection.removeSneakerFromFavouriteItems(favouriteProduct)
            }

        }

        // Function that returns all the basket items of the user
        fun getBasketItems(): MutableList<BasketItem>{
            return Storage.user.basketProducts
        }

        // function that returns an element of the basket item list
        private fun getBasketItem(idBasketItem: String): BasketItem?{
            for(basketItem in Storage.user.basketProducts){
                if(basketItem.id == idBasketItem) return basketItem
            }

            return null
        }

        // Function that checks if an element is already in the basket
        fun isItemAlreadyInBasket(id_sneaker: String, size: String): Boolean{
            for(basketItem in Storage.user.basketProducts){
                if(basketItem.id_product == id_sneaker && basketItem.size == size) return true
            }
            return false
        }

        // Function that adds a new item to the basket list
        fun addItemToBasket(id_sneaker: String, size: String){
            FirebaseConnection.addItemToBasket(id_sneaker, size)
        }

        // Function that removes an item from the basket list
        fun removeItemFromBasket(idBasketItem: String){
            val basketItem = getBasketItem(idBasketItem)
            if(basketItem != null){
                FirebaseConnection.removeItemFromBasket(basketItem)
            }
        }

        // Function that removes all basket items
        fun removeAllBasketItems(){
            FirebaseConnection.removeAllBasketItems()
        }

        // Function that returns a FavouriteProduct with the sneaker ID
        private fun getFavouriteProduct(idSneaker: String): FavouriteProduct?{
            for(favouriteProduct in Storage.user.favouriteProducts){
                if(favouriteProduct.id_product == idSneaker) return favouriteProduct
            }

            return null
        }
    }
}