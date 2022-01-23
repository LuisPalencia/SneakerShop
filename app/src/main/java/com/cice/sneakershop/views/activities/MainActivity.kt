package com.cice.sneakershop.views.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cice.sneakershop.R
import com.cice.sneakershop.constants.Constants
import com.cice.sneakershop.databinding.ActivityMainBinding
import com.cice.sneakershop.models.User
import com.cice.sneakershop.utils.GenericFunctions
import com.cice.sneakershop.utils.observeOnce
import com.cice.sneakershop.viewmodels.MainViewModel
import com.cice.sneakershop.views.fragments.*
import com.cice.sneakershop.views.interfaces.SupportFragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MainActivity : AppCompatActivity(), SupportFragmentManager {
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //ViewModels
    private val mainViewModel: MainViewModel by viewModels()

    // Navcontroller for the navigation graph
    private var currentNavController: LiveData<NavController>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //This extra indicates if user logged manually in the previous activity
        val loginPreviousActivity = intent.getBooleanExtra(Constants.LOGGED_BEFORE_MAIN_ACTIVITY, false)

        //Check if sneaker ID is correct
        if(loginPreviousActivity){
            //If user has just logged in, show a snackbar indicating loggin was successfully
            Snackbar.make(binding.constraintLayoutMainActivity, getString(R.string.correct_login), Snackbar.LENGTH_LONG).show()
        }

        //Check if device is connected to internet
        if(GenericFunctions.isDeviceConnectedToInternet(this)){
            initializeMainActivity()
        }else{
            setLayoutNoInternetConnection()
        }

    }

    //Method that is executed when the toolbar back button is pressed
    override fun onSupportNavigateUp(): Boolean {
        //onBackPressed()
        //return true
        return currentNavController?.value?.navigateUp() ?: false
    }

    //Method that sets visible the layout when the device is not connected to internet
    private fun setLayoutNoInternetConnection(){
        //Set the correct visibility of the layouts
        binding.layoutShop.visibility = View.GONE
        binding.layoutNoConnection.root.visibility = View.VISIBLE

        // Set the listener of the try again button
        findViewById<Button>(R.id.btnTryConnection).setOnClickListener {
            if(GenericFunctions.isDeviceConnectedToInternet(this)){
                // If the device is connected now to internet, do the normal flow
                initializeMainActivity()
            }else{
                Snackbar.make(binding.constraintLayoutMainActivity, getString(R.string.error_internet_content), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    // Method that initializes the main activity
    private fun initializeMainActivity(){
        firebaseAuth = FirebaseAuth.getInstance()

        //Set the user observer
        setInitialObserverUser()
        //Get the initial data
        getInitialData()

        //Set the correct visibility of the layouts
        binding.layoutShop.visibility = View.VISIBLE
        binding.layoutNoConnection.root.visibility = View.GONE

        // Set the navigation graph of the app in order to keep the progress on each item of the bottomNavigationView
        val navGraphIds = listOf(R.navigation.shop, R.navigation.favourites, R.navigation.basket, R.navigation.profile)

        val controller = binding.bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )

        currentNavController = controller
    }

    //Method that sets only once an observer for the user info
    private fun setInitialObserverUser(){
        mainViewModel.user.observeOnce(this, Observer<User> {
            if (it != null) {
                // Once the user info is obtained, the app gets the info of all the sneakers in the DB
                    // First, it gets only the necessary info for the lists (SimpleSneaker)
                mainViewModel.getSimpleSneakers()
                // Also it gets the user orders
                mainViewModel.getOrders(mainViewModel.getCurrentUser().uuid)
            }
        })
    }


    //Method that request the info of the user from the DB
    private fun getInitialData(){
        mainViewModel.getUserByUUID(firebaseAuth.currentUser!!.uid)
    }

    //Method that selects programatically the Shop menu
    override fun setShopMenu() {
        binding.bottomNavigationView.findViewById<View>(R.id.shop).performClick()
    }

    //Method that sets visible the orders list fragment
    override fun showOrders() {
        //Check if device is connected to internet
        if(GenericFunctions.isDeviceConnectedToInternet(this)){
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_profile_to_ordersList)
        }else{
            //Show error
            GenericFunctions.getErrorInternetDialog(this).show()
        }
    }

    //Method that sets visible the contact fragment
    override fun showContactDetails() {
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_profile_to_contact)
    }

    //Method that signs out the current user, finishes the activity and start the LoginActivity
    override fun signOutUser() {
        //Sign out user from Firebase
        firebaseAuth.signOut()
        //Open login activity
        val intent = Intent(this, LoginActivity::class.java)
        // Flags in order to remove from the stack this activity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // Method that show the details of a sneaker from the Shop menu
    override fun showSneakerDetailsShop(param: String){
        showSneakerDetails(param, R.id.action_shop_to_sneakerDetails)
    }

    // Method that show the details of a sneaker from the Favourite Sneakers menu
    override fun showSneakerDetailsFavourites(param: String){
        showSneakerDetails(param, R.id.action_favourites_to_sneakerDetails)
    }

    // Method that show the details of a sneaker from the Basket menu
    override fun showSneakerDetailsBasket(param: String){
        showSneakerDetails(param, R.id.action_basket_to_sneakerDetails)
    }

    // Method that navigates into the sneaker details fragment
    private fun showSneakerDetails(param: String, action: Int) {
        var bundle = bundleOf()
        bundle.putString(Constants.PRODUCT_ID_EXTRA, param)
        findNavController(R.id.nav_host_fragment).navigate(action, bundle)
    }
}