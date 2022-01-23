package com.cice.sneakershop.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cice.sneakershop.models.*
import java.util.*

class MainViewModel : ViewModel() {

    //User
    val user = MutableLiveData<User>()
    //SimpleSneakers
    val simpleSneakers = MutableLiveData<MutableList<SimpleSneaker>>()
    //SimpleSneakers that are favourite
    val favouriteProducts = MutableLiveData<MutableList<FavouriteProduct>>()
    //Sneaker
    val sneaker = MutableLiveData<Sneaker>()
    //Basket items
    val basketItems = MutableLiveData<MutableList<BasketItem>>()
    //Orders
    val orders = MutableLiveData<MutableList<Order>>()

    //User methods

    // Function that gets the user info with the UUID
    fun getUserByUUID(uuid: String){
        UserProvider.getUserByUUID(uuid, user, basketItems)
    }

    // Function that returns the current user object model
    fun getCurrentUser(): User{
        return UserProvider.getCurrentUser()
    }

    //SimpleSneaker methods

    // Function that gets the simple info of all the sneaker of the shop
    fun getSimpleSneakers(){
        SimpleSneakerProvider.getSimpleSneakers(
            simpleSneakers,
            favouriteProducts)
        basketItems.postValue(user.value!!.basketProducts)
    }

    // Function that searchs for a simple sneaker with its ID
    fun getSimpleSneakerById(id: String): SimpleSneaker? {
        return SimpleSneakerProvider.getSimpleSneakerById(id)
    }

    // Function that returns the small image of a simple sneaker
    fun getSmallImageSneaker(idSneaker: String): String?{
        return SimpleSneakerProvider.getSmallImageSimpleSneaker(idSneaker)
    }

    // Function that adds a sneaker to the favourite list of sneakers of the user
    fun addSneakerToFavouriteItems(idSneaker: String){
        val simpleSneaker = getSimpleSneakerById(idSneaker)

        if(simpleSneaker != null){
            UserProvider.addSneakerToFavouriteItems(simpleSneaker, favouriteProducts)
            //SimpleSneakerProvider.addSneakerToFavouriteItems(simpleSneaker, simpleFavouriteSneakers)
        }

    }

    // Function that adds a sneaker to the favourite list of sneakers of the user
    fun addSneakerToFavouriteItems(sneaker: Sneaker){
        val simpleSneaker = getSimpleSneakerById(sneaker.id)

        if(simpleSneaker != null){
            UserProvider.addSneakerToFavouriteItems(simpleSneaker, favouriteProducts)
            sneaker.isFavourite = true
            //SimpleSneakerProvider.addSneakerToFavouriteItems(simpleSneaker, simpleFavouriteSneakers)
        }
    }

    // Function that removes a sneaker from the favourite list of sneakers of the user
    fun removeSneakerFromFavouriteItems(idSneaker: String){
        val simpleSneaker = getSimpleSneakerById(idSneaker)

        if(simpleSneaker != null){
            UserProvider.removeSneakerFromFavouriteItems(simpleSneaker, favouriteProducts)
            //SimpleSneakerProvider.addSneakerToFavouriteItems(simpleSneaker, simpleFavouriteSneakers)
        }
    }

    // Function that removes a sneaker from the favourite list of sneakers of the user
    fun removeSneakerFromFavouriteItems(sneaker: Sneaker){
        val simpleSneaker = getSimpleSneakerById(sneaker.id)

        if(simpleSneaker != null){
            UserProvider.removeSneakerFromFavouriteItems(simpleSneaker, favouriteProducts)
            sneaker.isFavourite = false
            //SimpleSneakerProvider.addSneakerToFavouriteItems(simpleSneaker, simpleFavouriteSneakers)
        }
    }

    //Sneaker methods

    // Method that gets the info of a sneaker
    fun getSneakerById(id: String){
        val simpleSneaker = SimpleSneakerProvider.getSimpleSneakerById(id)
        if(simpleSneaker != null){
            SneakerProvider.getSneakerByID(sneaker, simpleSneaker)
        }

    }

    // Basket methods

    // Function that returns all the basket items of the user
    fun getBasketItems(): MutableList<BasketItem>{
        return UserProvider.getBasketItems()
    }

    // Function that removes an item from the basket list
    fun removeItemFromBasket(idBasketItem: String){
        UserProvider.removeItemFromBasket(idBasketItem)
    }

    // Function that removes all basket items
    private fun removeAllBasketItems(){
        UserProvider.removeAllBasketItems()
    }

    // Function that adds a new item to the basket list
    fun addItemToBasket(id_sneaker: String, size: String){
        UserProvider.addItemToBasket(id_sneaker, size)
    }

    // Function that checks if an element is already in the basket
    fun isItemAlreadyInBasket(id_sneaker: String, size: String): Boolean{
        return UserProvider.isItemAlreadyInBasket(id_sneaker, size)
    }


    //Order methods

    // Function that gets all the orders created by a user
    fun getOrders(userId: String){
        OrderProvider.getOrders(userId, orders)
    }

    // Function that creates a new order with some items
    fun createOrder(userId: String, products: MutableList<BasketItem>, totalPrice: Double): Boolean {
        val isOrderCreated = OrderProvider.createOrder(userId, products, totalPrice)

        if(isOrderCreated) removeAllBasketItems()

        return isOrderCreated
    }

}