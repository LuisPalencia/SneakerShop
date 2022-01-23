package com.cice.sneakershop.constants

object Constants {
    // This val is because resource "R.string.default_web_client_id" is not being loaded successfully for Google Login, so it has been added manually
    const val DEFAULT_WEB_CLIENT_ID = "28990263070-i4htcc6a7ag7khcuvq7gbefbmtb12q32.apps.googleusercontent.com"


    const val DB_URL = "https://sneakershop-36e8d-default-rtdb.europe-west1.firebasedatabase.app/"

    const val DB_REFERENCE_SIMPLE_SNEAKERS = "simple_sneakers"
    const val DB_REFERENCE_SNEAKERS = "sneakers"
    const val DB_REFERENCE_USERS = "users"
    const val DB_REFERENCE_ORDERS = "orders"

    //Bundles
    //Bundle ProductInfoFragment
    const val PRODUCT_ID_EXTRA = "PRODUCT_ID_EXTRA"
    //Bundle login
    const val LOGGED_BEFORE_MAIN_ACTIVITY = "LOGGED_BEFORE_MAIN_ACTIVITY"

    // Minimum price for free shipping costs
    const val MINIMUM_PRICE_FREE_SHIPPING_COSTS = 120.0
    const val SHIPPING_COSTS = 2.99
}