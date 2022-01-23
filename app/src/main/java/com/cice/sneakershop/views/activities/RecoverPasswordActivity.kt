package com.cice.sneakershop.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.cice.sneakershop.R
import com.cice.sneakershop.utils.GenericFunctions
import com.cice.sneakershop.utils.PatternsAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception
import com.google.firebase.auth.SignInMethodQueryResult

import androidx.annotation.NonNull




class RecoverPasswordActivity : AppCompatActivity() {
    private val TAG = "RecoverPasswordActivity"

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        firebaseAuth = FirebaseAuth.getInstance()

        setRecoverPasswordButton()
        setBackToLoginButton()
    }

    // Function that sets the funcionality so user is able to recover the password
    private fun setRecoverPasswordButton(){
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val btnRecoverPassword = findViewById<Button>(R.id.btnRecoverPassword)

        // Sets a click listener in the recover password button
        btnRecoverPassword.setOnClickListener {
            //Check internet connection
            if(GenericFunctions.isDeviceConnectedToInternet(this)){
                val email = editTextEmail.text.toString()

                // Check if email is valid
                if(!PatternsAccount.isValidEmail(email)){
                    editTextEmail.error = getString(R.string.incorrect_email)
                    return@setOnClickListener
                }
                // Email is valid, so it continues

                try{
                    // Send email to recover password
                    firebaseAuth.fetchSignInMethodsForEmail(email)
                        .addOnCompleteListener { task ->
                            // Check if the user exists first
                            val isNewUser = task.result.signInMethods?.isEmpty()
                            if (isNewUser != null && !isNewUser) {
                                Log.e("TAG", "Email exists")
                                // If user exists, then proceed to send the email
                                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(this){ task ->
                                    if(!task.isSuccessful){ // An error ocurred when sending the email
                                        Snackbar.make(findViewById(R.id.constraintLayoutRecoverPassword), getString(R.string.error_email_recover_password), Snackbar.LENGTH_LONG).show()
                                        return@addOnCompleteListener
                                    }else{ // The email has been sent successfully
                                        Toast.makeText(this, getString(R.string.success_email_recover_password), Toast.LENGTH_LONG).show()
                                        finish()
                                    }
                                }
                            } else { // The email doesn't exist
                                Snackbar.make(findViewById(R.id.constraintLayoutRecoverPassword), getString(R.string.error_not_existing_email), Snackbar.LENGTH_LONG).show()
                                Log.e("TAG", "Email doesn't exist")
                            }
                        }
                }catch (e: Exception){
                    Log.e(TAG, e.toString())
                    Snackbar.make(findViewById(R.id.constraintLayoutRecoverPassword), getString(R.string.error_email_recover_password), Snackbar.LENGTH_LONG).show()
                }
            }else{
                // Show error
                GenericFunctions.getErrorInternetDialog(this).show()
            }

        }
    }

    // Fun that sets the logic of the back to login button
    private fun setBackToLoginButton(){
        findViewById<Button>(R.id.btnBackToLogin).setOnClickListener {
            finish()
        }
    }
}