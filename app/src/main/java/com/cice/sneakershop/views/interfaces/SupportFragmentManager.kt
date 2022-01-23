package com.cice.sneakershop.views.interfaces

import androidx.fragment.app.Fragment

interface SupportFragmentManager {
    fun showSneakerDetailsShop(param: String = "")
    fun showSneakerDetailsFavourites(param: String = "")
    fun showSneakerDetailsBasket(param: String = "")
    fun setShopMenu()
    fun showOrders()
    fun signOutUser()
    fun showContactDetails()
}