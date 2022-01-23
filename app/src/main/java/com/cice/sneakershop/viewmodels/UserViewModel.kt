package com.cice.sneakershop.viewmodels

import androidx.lifecycle.ViewModel
import com.cice.sneakershop.models.UserProvider

class UserViewModel : ViewModel(){

    // function that inserts a new user into Firebase
    fun insertNewUser(uuid: String, name: String){
        UserProvider.addNewUser(uuid, name)
    }

}