package com.cice.sneakershop.connections

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cice.sneakershop.Storage
import com.cice.sneakershop.constants.Constants
import com.cice.sneakershop.models.*
import com.cice.sneakershop.utils.GenericFunctions
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.*

// Class that manages all the requests to Firebase in order to get all the data

class FirebaseConnection {
    companion object{
        private val TAG = "FirebaseConnection"

        private val database = Firebase.database(Constants.DB_URL)

        // All references
        //val firebaseRef = database.getReference("")
        private val simpleSneakersRef = database.getReference(Constants.DB_REFERENCE_SIMPLE_SNEAKERS)
        private val usersRef = database.getReference(Constants.DB_REFERENCE_USERS)
        private val ordersRef = database.getReference(Constants.DB_REFERENCE_ORDERS)

        // User methods

        //Method that gets the user info
        fun getUserByUUID(uuid: String, liveDataUser: MutableLiveData<User>, liveDataBasket: MutableLiveData<MutableList<BasketItem>>){
            val user = User()
            // Assign the user to the local storage
            Storage.user = user
            user.uuid = uuid

            val userRef = Storage.database.getReference("${Constants.DB_REFERENCE_USERS}/$uuid")

            userRef.get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val result = task.result

                    //Get all data
                    result?.children?.forEach { snapshot ->
                        when(snapshot.key){
                            "name" -> user.name = snapshot.value?.toString() ?: ""
                            /*
                            "favouriteProducts" -> {
                                val gson = GsonBuilder().setPrettyPrinting().create()
                                val sType = object : TypeToken<List<String>>() { }.type
                                user.favouriteProducts = gson.fromJson(snapshot.value?.toString() ?: "", sType)
                            }

                             */
                            "favouriteProducts" -> {
                                for(child in snapshot.children){
                                    if(child.key != null){
                                        val favouriteProduct = FavouriteProduct(child.key!!, child.value?.toString() ?: "")
                                        user.favouriteProducts.add(favouriteProduct)
                                    }

                                }
                            }
                            //"basketProducts" -> {
                                //val gson = GsonBuilder().setPrettyPrinting().create()
                                //val sType = object : TypeToken<List<BasketItem>>() { }.type
                                //user.basketProducts = gson.fromJson(snapshot.value?.toString() ?: "", sType)
                            //}
                        }
                    }

                    // Post the user data to the MutableLiveData
                    liveDataUser.postValue(user)
                }else{

                }
            }

            //After getting user info, get the basket products
            getBasketProductsUser(uuid, liveDataBasket)
        }

        // Method that adds a new user to the Firebase DB
        fun addNewUser(uuid: String, name: String){
            usersRef.child(uuid).child("name").setValue(name)
            usersRef.child(uuid).child("basketProducts").setValue(mapOf<String, String>())
            usersRef.child(uuid).child("favouriteProducts").setValue(mapOf<String, String>())
        }



        // Simple sneaker methods

        // Method that gets all the simple sneakers with the childEventListener method of firebase
        fun getSimpleSneakersChild(liveData: MutableLiveData<MutableList<SimpleSneaker>>,
                                   liveDataFavourites: MutableLiveData<MutableList<SimpleSneaker>>,
                                   favouriteSneakers: MutableList<String>){
            Storage.simpleSneakers = mutableListOf()

            simpleSneakersRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    //Log.d(TAG, snapshot.toString())

                    var simpleSneaker = SimpleSneaker()

                    simpleSneaker.id = snapshot.key.toString()

                    // Get each field of a SimpleSneaker item
                    snapshot.children.forEach {
                        when(it.key){
                            //"id" -> simpleSneaker.id = it.value?.toString() ?: ""
                            "name" -> simpleSneaker.name = it.value?.toString() ?: ""
                            "price" -> simpleSneaker.price = it.value?.toString()?.toFloatOrNull() ?: 0F
                            "discount" -> simpleSneaker.discount = it.value?.toString()?.toIntOrNull() ?: 0
                            "brand" -> simpleSneaker.brand = it.value?.toString() ?: ""
                            "smallImageUrl" -> simpleSneaker.smallImageURL = it.value?.toString() ?: ""
                        }
                    }

                    // Add the element to the list
                    Storage.simpleSneakers.add(simpleSneaker)
                    //Update the MutableLiveData
                    liveData.postValue(Storage.simpleSneakers)

                    /*
                    // Check if product is favourite
                    if(simpleSneaker.id.isNotEmpty()){
                        if(simpleSneaker.id in favouriteSneakers){
                            // Set the favourite product
                            simpleSneaker.isFavourite = true
                            Storage.simpleSneakersFavourites.add(simpleSneaker)
                            liveDataFavourites.postValue(Storage.simpleSneakersFavourites)
                        }
                    }

                     */


                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            }
            )
        }

        // Method that gets all the simple sneakers from Firebase
        fun getSimpleSneakers(
            liveData: MutableLiveData<MutableList<SimpleSneaker>>,
            liveDataFavourites: MutableLiveData<MutableList<FavouriteProduct>>){

            Storage.simpleSneakers = mutableListOf()

            val favouriteProducts = Storage.user.favouriteProducts.map { it.id_product }

            simpleSneakersRef.get().addOnCompleteListener { task ->

                if(task.isSuccessful){
                    val result = task.result
                    //Log.d(TAG, "Result: ${result.toString()}")

                    // Get each SimpleSneaker
                    result?.children?.forEach { snapshot ->
                        //Log.d(TAG, "KEY: ${snapshot.key}")
                        //Log.d(TAG, "VALUE: ${snapshot.value}")

                        val simpleSneaker = SimpleSneaker()

                        simpleSneaker.id = snapshot.key.toString()

                        // Get each field of a SimpleSneaker item
                        snapshot.children.forEach {

                            when(it.key){
                                //"id" -> simpleSneaker.id = it.value?.toString() ?: ""
                                "name" -> simpleSneaker.name = it.value?.toString() ?: ""
                                "price" -> simpleSneaker.price = it.value?.toString()?.toFloatOrNull() ?: 0F
                                "discount" -> simpleSneaker.discount = it.value?.toString()?.toIntOrNull() ?: 0
                                "brand" -> simpleSneaker.brand = it.value?.toString() ?: ""
                                "smallImageUrl" -> simpleSneaker.smallImageURL = it.value?.toString() ?: ""
                            }
                        }

                        //Update the MutableLiveData
                        liveData.postValue(Storage.simpleSneakers)
                        Storage.simpleSneakers.add(simpleSneaker)

                        // Check if product is favourite
                        if(simpleSneaker.id in favouriteProducts){
                            simpleSneaker.isFavourite = true
                        }
                    }

                    //This postValue is to update the list of favourite products as now the simplesneaker details have been obtained
                    liveDataFavourites.postValue(Storage.user.favouriteProducts)
                    //The call for this method is in order to set a listener when the favourite products change
                    getFavouriteProductsUser(liveData, liveDataFavourites)
                }else{
                    //MANDAR EXCEPCION AL MAINACTIVITY
                }
            }
        }

        // Sneaker methods

        //Method that obatins all the details of a sneaker with the ID
        fun getSneakerByID(sneaker: Sneaker, liveData: MutableLiveData<Sneaker>){
            val sneakerRef = Storage.database.getReference("${Constants.DB_REFERENCE_SNEAKERS}/${sneaker.id}")

            sneakerRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Get the elements
                    snapshot.children.forEach {
                        when(it.key){
                            "mainImageUrl" -> sneaker.mainImageURL = it.value?.toString() ?: ""
                            "description" -> sneaker.description = it.value?.toString() ?: ""
                            "availableSizes" -> {
                                sneaker.availableSizes = it.value as Map<String, String>
                            }
                            "totalReviews" -> sneaker.totalReviews = it.value?.toString()?.toInt() ?: 0
                            "averageMarkReview" -> sneaker.averageMarkReview = it.value?.toString()?.toDouble() ?: 0.0
                        }
                    }
                    //Post the value of the sneaker obtained
                    liveData.postValue(sneaker)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

        // Order methods

        // Method that gets all user orders
        fun getOrders(userId: String, liveData: MutableLiveData<MutableList<Order>>){
            val orders = mutableListOf<Order>()
            Storage.orders = orders

            val ordersUserRef = ordersRef.child(userId)
            ordersUserRef.addChildEventListener(object :
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    // Create order
                    val order = Order(
                        orderId = snapshot.key.toString(),
                        userId = userId
                    )

                    //get the order info
                    snapshot.children.forEach {
                        when(it.key){
                            "products" -> {
                                val gson = GsonBuilder().setPrettyPrinting().create()
                                val sType = object : TypeToken<List<OrderProduct>>() { }.type
                                order.products = gson.fromJson(it.value?.toString() ?: "", sType)
                            }
                            "totalPrice" -> order.totalPrice = it.value?.toString()?.toDoubleOrNull() ?: 0.0
                            "dateOfOrder" -> order.dateOfOrder = it.value?.toString() ?: ""
                        }

                    }
                    //Add order to list
                    orders.add(order)
                    // Update value in MutableLiveData
                    liveData.postValue(orders)

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            }
            )
        }

        //Method that creates a order
        fun createOrder(userId: String, basketProducts: MutableList<BasketItem>, totalPrice: Double): Boolean {
            //Get a random id for the order
            val orderId = ordersRef.push().key ?: return false

            //Create list of order products
            val orderProducts = mutableListOf<OrderProduct>()
            for(basketProduct in basketProducts){
                orderProducts.add(
                    OrderProduct(
                        idProduct = basketProduct.id_product,
                        size = basketProduct.size
                    )
                )
            }

            //Create order object
            val order = Order(
                orderId = orderId!!,
                userId = userId,
                products = orderProducts,
                totalPrice = totalPrice,
                dateOfOrder = GenericFunctions.dateToString(Date())
            )

            //Set the map and update it in the DB
            val childUpdates = hashMapOf<String, Any>(
                "products" to order.products,
                "totalPrice" to order.totalPrice,
                "dateOfOrder" to order.dateOfOrder
            )

            //Perform the DB insertion
            ordersRef.child(userId).child(order.orderId).updateChildren(childUpdates)

            //ordersRef.child(userId).child(order.orderId).child("products").setValue(order.products)
            //ordersRef.child(userId).child(order.orderId).child("totalPrice").setValue(order.totalPrice)
            //ordersRef.child(userId).child(order.orderId).child("dateOfOrder").setValue(order.dateOfOrder)

            return true
        }

        // Basket methods

        //Method that gets all the user basket products
        fun getBasketProductsUser(uuid: String, liveDataBasket: MutableLiveData<MutableList<BasketItem>>){
            val basketRef = Storage.database.getReference("${Constants.DB_REFERENCE_USERS}/$uuid/basketProducts")


            basketRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val basketItem = BasketItem()

                    if(snapshot.key != null){
                        //the ID is the snapshot key
                        val idBasketItem = snapshot.key.toString()

                        var isBasketItemAlreadyAdded = false

                        //Check if basket item is already added in the storage list
                        val iterator = Storage.user.basketProducts.iterator()

                        while(iterator.hasNext()){
                            val basketProduct = iterator.next()
                            if(idBasketItem == basketProduct.id){
                                isBasketItemAlreadyAdded = true
                                break
                            }
                        }

                        //If basket wasn't in the list, get the info and add it
                        if(!isBasketItemAlreadyAdded){
                            // Get each field of a SimpleSneaker item
                            basketItem.id = idBasketItem
                            snapshot.children.forEach {
                                when(it.key){
                                    "id_product" -> basketItem.id_product = it.value?.toString() ?: ""
                                    "size" -> basketItem.size = it.value?.toString() ?: ""
                                }
                            }

                            //Add the new basket item
                            Storage.user.basketProducts.add(basketItem)
                            // Update the mutablelivedata
                            liveDataBasket.postValue(Storage.user.basketProducts)
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    if(snapshot.key != null) {
                        val idBasketItem = snapshot.key.toString()

                        val iterator = Storage.user.basketProducts.iterator()

                        //Check all the basket items and remove the one received in this listener method
                        while(iterator.hasNext()){
                            val basketProduct = iterator.next()
                            if (idBasketItem == basketProduct.id) {
                                iterator.remove()
                                //Storage.user.basketProducts.remove(basketProduct)
                                liveDataBasket.postValue(Storage.user.basketProducts)
                            }
                        }
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            }
            )
        }

        // Function that adds a item to the basket
        fun addItemToBasket(id_sneaker: String, size: String): Boolean{
            val basketRef = usersRef.child(Storage.user.uuid).child("basketProducts")

            // Get the basket product ID
            val basketProductId = basketRef.push().key ?: return false

            //Set the map and update it in the DB
            val childUpdates = hashMapOf<String, Any>(
                "id_product" to id_sneaker,
                "size" to size
            )

            basketRef.child(basketProductId).updateChildren(childUpdates)


            //basketRef.child(basketProductId).child("id_product").setValue(id_sneaker)
            //basketRef.child(basketProductId).child("size").setValue(size)

            return true
        }

        //Method that removes an item from the DB
        fun removeItemFromBasket(basketItem: BasketItem){
            val basketRef = usersRef.child(Storage.user.uuid).child("basketProducts")
            basketRef.child(basketItem.id).removeValue()
        }

        //Method that removes all basket items from the Firebase DB
        fun removeAllBasketItems(){
            usersRef.child(Storage.user.uuid).child("basketProducts").removeValue()
        }

        // Favourite products methods

        //Method that gets all the user basket products
        fun getFavouriteProductsUser(
            liveDataSimpleSneakers: MutableLiveData<MutableList<SimpleSneaker>>,
            favouriteSneakers: MutableLiveData<MutableList<FavouriteProduct>>){
            val favouriteProductsRef = Storage.database.getReference("${Constants.DB_REFERENCE_USERS}/${Storage.user.uuid}/favouriteProducts")


            favouriteProductsRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if(snapshot.key != null){

                        val idFavouriteSneaker = snapshot.value?.toString() ?: ""

                        if(idFavouriteSneaker.isNotEmpty()){
                            //Check if basket item is already added in the storage list
                            val iterator = Storage.user.favouriteProducts.iterator()

                            while(iterator.hasNext()){
                                val favouriteProduct = iterator.next()
                                if(favouriteProduct.id_product == idFavouriteSneaker){
                                    Log.d(TAG, "Product already added to favourites")
                                    if(favouriteProduct.id == ""){
                                        favouriteProduct.id = snapshot.key!!
                                    }
                                    return
                                }
                            }

                            val newFavouriteProduct = FavouriteProduct(snapshot.key!!, idFavouriteSneaker)
                            Storage.user.favouriteProducts.add(newFavouriteProduct)

                            favouriteSneakers.postValue(Storage.user.favouriteProducts)
                            liveDataSimpleSneakers.postValue(Storage.simpleSneakers)
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    if(snapshot.key != null){
                        val idFavouriteSneaker = snapshot.value?.toString() ?: ""

                        if(idFavouriteSneaker.isNotEmpty()){
                            //Check if basket item is already added in the storage list
                            val iterator = Storage.user.favouriteProducts.iterator()

                            while(iterator.hasNext()){
                                val favouriteProduct = iterator.next()
                                if(favouriteProduct.id == idFavouriteSneaker){
                                    Storage.user.favouriteProducts.remove(favouriteProduct)
                                    favouriteSneakers.postValue(Storage.user.favouriteProducts)
                                    liveDataSimpleSneakers.postValue(Storage.simpleSneakers)
                                    return
                                }
                            }

                            Log.d(TAG, "Product already removed from favourites")
                        }
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            }
            )
        }

        /*
        fun updateFavouriteItemsList(){
            usersRef.child(Storage.user.uuid).child("favouriteProducts").setValue(Storage.user.favouriteProducts)
        }

         */

        // Method that adds a sneaker to the favourite items
        fun addSneakerToFavouriteItems(idSneaker: String): FavouriteProduct?{
            val favouriteProductsRef = usersRef.child(Storage.user.uuid).child("favouriteProducts")

            // Get the basket product ID
            val favouriteProductId = favouriteProductsRef.push().key ?: return null
            val favouriteProduct = FavouriteProduct(favouriteProductId, idSneaker)

            favouriteProductsRef.child(favouriteProductId).setValue(idSneaker)

            return favouriteProduct
        }

        // Method that removes a sneaker from the favourite items
        fun removeSneakerFromFavouriteItems(favouriteProduct: FavouriteProduct){
            val favouriteProductsRef = usersRef.child(Storage.user.uuid).child("favouriteProducts")
            favouriteProductsRef.child(favouriteProduct.id).removeValue()
        }
    }


}