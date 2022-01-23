package com.cice.sneakershop.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.cice.sneakershop.R
import com.cice.sneakershop.databinding.ActivitySignUpBinding
import com.cice.sneakershop.utils.GenericFunctions
import com.cice.sneakershop.utils.PatternsAccount
import com.cice.sneakershop.viewmodels.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

class SignUpActivity : AppCompatActivity() {
    private val TAG = "SignUpActivity"

    private lateinit var binding: ActivitySignUpBinding

    //ViewModel
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Set the listeners for the buttons and the edittexts
        setCreateAccountButton()
        setEditTextListeners()
        setBackToLoginButton()

    }

    // Function that set up the funcionality of the create account button
    private fun setCreateAccountButton(){
        // Sets a click listener in the create account button
        findViewById<Button>(R.id.btnCreateAccount).setOnClickListener {
            //Check internet connection
            if(GenericFunctions.isDeviceConnectedToInternet(this)){
                val name = binding.editTextName.text.toString()
                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()
                val confirmPassword = binding.editTextConfirmPassword.text.toString()

                // checks these three things: email valid, password that matches minimum requiriments, and the confirm password is the same as the password
                if(!PatternsAccount.isValidEmail(email) ||
                    name.isEmpty() ||
                    !PatternsAccount.isValidPassword(password) ||
                    !PatternsAccount.isValidConfirmPassword(password, confirmPassword)
                ){ // There are errors in the data introduced
                    Snackbar.make(it, getString(R.string.error_in_data_account), Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                // All data is correct, proceed to create user
                createUserWithEmailFirebase(email, password)
            }else{
                // Show error
                GenericFunctions.getErrorInternetDialog(this).show()
            }
        }
    }

    // Function that set up the funcionality of the Edittexts
    // All EditTexts will have a listener when a character is added in order to check if fields are correct
    private fun setEditTextListeners(){
        // Sets a text changed listener in the email edittext
        binding.editTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val email = binding.editTextName.text.toString()
                if(!PatternsAccount.isValidEmail(email)){
                    // If there are some errors, the error message is enabled
                    //enableErrorMessage(getString(R.string.incorrect_email))
                    binding.editTextName.error = getString(R.string.incorrect_email)
                }else{
                    // If everything is fine, disable error message
                    //disableErrorMessage()
                    binding.editTextName.error = null
                }
            }

        })

        // Sets a text changed listener in the name edittext
        binding.editTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val name = binding.editTextName.text.toString()

                if(name.isEmpty()){
                    // If there are some errors, the error message is enabled
                    //enableErrorMessage(getString(R.string.incorrect_name))
                    binding.editTextName.error = getString(R.string.incorrect_name)
                }else{
                    // If everything is fine, disable error message
                    //disableErrorMessage()
                    binding.editTextName.error = null
                }
            }

        })

        // Sets a text changed listener in the password edittext
        binding.editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val password = binding.editTextPassword.text.toString()

                if(!PatternsAccount.isValidPassword(password)){
                    // If there are some errors, the error message is enabled
                    //enableErrorMessage(getString(R.string.incorrect_password))
                    binding.editTextPassword.error = getString(R.string.incorrect_password)
                }else{
                    // If everything is fine, disable error message
                    //disableErrorMessage()
                    binding.editTextPassword.error = null
                }
            }

        })

        // Sets a text changed listener in the confirm password edittext
        binding.editTextConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val password = binding.editTextPassword.text.toString()
                val confirmPassword = binding.editTextConfirmPassword.text.toString()
                if(!PatternsAccount.isValidConfirmPassword(password, confirmPassword)){
                    // If there are some errors, the error message is enabled
                    //enableErrorMessage(getString(R.string.different_passwords))
                    binding.editTextConfirmPassword.error = getString(R.string.different_passwords)
                }else{
                    // If everything is fine, disable error message
                    //disableErrorMessage()
                    binding.editTextConfirmPassword.error = null
                }
            }

        })
    }

    // Fun that disables the error message TextView
    private fun disableErrorMessage(){
        binding.textViewErrorCreateAccount.text = ""
        binding.textViewErrorCreateAccount.visibility = View.GONE
    }

    // Fun that enables the error message TextView
    private fun enableErrorMessage(error: String){
        binding.textViewErrorCreateAccount.text = error
        binding.textViewErrorCreateAccount.visibility = View.VISIBLE
    }

    // Fun that sets the logic of the back to login button
    private fun setBackToLoginButton(){
        findViewById<Button>(R.id.btnBackToLogin).setOnClickListener {
            finish()
        }
    }

    // Fun that creates a user with email in Firebase
    private fun createUserWithEmailFirebase(email: String, password: String){
        try{
            val firebaseAuth = FirebaseAuth.getInstance()

            //Try to create user
            firebaseAuth.createUserWithEmailAndPassword(
                email,
                password
            ).addOnCompleteListener { task ->
                if (!task.isSuccessful){
                    // There are errors when trying to create the user
                    Snackbar.make(findViewById(R.id.scrollViewSignUp), getString(R.string.error_create_account), Snackbar.LENGTH_LONG).show()
                    return@addOnCompleteListener
                }

                // User has been created successfully
                firebaseAuth.currentUser!!.sendEmailVerification().addOnCompleteListener(this){
                    Toast.makeText(this, getString(R.string.success_account_creation), Toast.LENGTH_LONG).show()
                    userViewModel.insertNewUser(firebaseAuth.currentUser!!.uid, email)
                    finish()
                }
            }
        }catch (e: Exception){
            Log.e(TAG, e.toString())
            Snackbar.make(findViewById(R.id.scrollViewSignUp), getString(R.string.error_create_account), Snackbar.LENGTH_LONG).show()
        }

    }
}