package com.cice.sneakershop.utils

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AlertDialog
import com.cice.sneakershop.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class GenericFunctions {
    companion object{
        //Method that calculates the new price of a product with a discount
        fun calculateDiscount(price: Float, discount: Int): Double{
            return roundDoubleTwoDecimals(price * (1 - discount / 100.0))
        }

        //Method that rounds a double to two decimals
        fun roundDoubleTwoDecimals(number: Double): Double{
            return (number * 100.0).roundToInt() / 100.0

        }

        fun doubleToStringTwoDecimals(number: Double): String{
            val df = DecimalFormat("0.00")
            return df.format(number)
        }

        //Method that converts a date to a string
        fun dateToString(date: Date): String{
            return SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date)
        }

        //Method that checks if the device is connected to internet
        fun isDeviceConnectedToInternet(context: Context): Boolean{
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        }

        //Method that returns an AlertDialog explaining that the device is not connected to internet
        fun getErrorInternetDialog(context: Context): AlertDialog{
            return AlertDialog.Builder(context)
                .setMessage(R.string.error_internet_content)
                .setTitle(R.string.error_internet_title)
                .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })
                .setCancelable(false)
                .create()
        }
    }
}