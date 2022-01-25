package com.cice.sneakershop

import com.cice.sneakershop.constants.Constants
import com.cice.sneakershop.models.Order
import com.cice.sneakershop.models.SimpleSneaker
import com.cice.sneakershop.models.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object Storage {
    val database = Firebase.database(Constants.DB_URL)

    // This var is in order to control if the data has been downloaded from the DB
    var isDataAvailable = false

    //User logged
    lateinit var user: User

    //Data of app:
    //List of SimpleSneakers
    var simpleSneakers: MutableList<SimpleSneaker> = mutableListOf()

    //SimpleSneakers that are favourite
    //var simpleSneakersFavourites: MutableList<SimpleSneaker> = mutableListOf()

    //Orders
    var orders: MutableList<Order> = mutableListOf()


}