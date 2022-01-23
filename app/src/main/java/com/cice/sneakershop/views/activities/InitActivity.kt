package com.cice.sneakershop.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cice.sneakershop.constants.Constants
import com.google.firebase.auth.FirebaseAuth

class InitActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        //Storage.firebaseConnection = FirebaseConnection()


        if(firebaseAuth.currentUser != null){ // User is authenticated
            val intent = Intent(this, MainActivity::class.java)
            // Flags in order to remove from the stack this activity
            intent.putExtra(Constants.LOGGED_BEFORE_MAIN_ACTIVITY, false)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }else{ // User is not authenticated
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        finish()
    }
}