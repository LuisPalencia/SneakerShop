package com.cice.sneakershop.views.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.cice.sneakershop.R
import com.cice.sneakershop.constants.Constants
import com.cice.sneakershop.databinding.ActivityLoginBinding
import com.cice.sneakershop.utils.GenericFunctions
import com.cice.sneakershop.utils.PatternsAccount
import com.cice.sneakershop.viewmodels.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var binding: ActivityLoginBinding

    //ViewModel
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        getGoogleSignIn()

        setLogin()
    }

    private fun getGoogleSignIn(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestIdToken(getString(R.string.default_web_client_id)) // THE RESOURCE IS NOT WORKING
            .requestIdToken(Constants.DEFAULT_WEB_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }


    // Function that set up the funcionality of the EditTexts and buttons of this activity
    private fun setLogin(){
        // Sets a click listener in the login button
        binding.btnLogIn.setOnClickListener {
            //Check internet connection
            if(GenericFunctions.isDeviceConnectedToInternet(this)){
                val email = binding.editTextUsername.text.toString()
                val password = binding.editTextPassword.text.toString()

                // Check if the email and passwords are valid
                if(!PatternsAccount.isValidEmail(email)){ // Email is not valid
                    // Set the error in the EditText
                    binding.editTextUsername.error = getString(R.string.incorrect_email)
                    return@setOnClickListener
                } else if(!PatternsAccount.isValidPassword(password)){ // Password is not valid
                    // Set the error in the EditText
                    binding.editTextPassword.error = getString(R.string.incorrect_password)
                    return@setOnClickListener
                }

                // Email and password are correct
                binding.editTextUsername.error = null
                binding.editTextPassword.error = null

                binding.textViewErrorLogin.visibility = View.GONE

                // Try to log in by email in Firebase
                logInUserByEmail(email, password)
            }else{
                // Show error
                GenericFunctions.getErrorInternetDialog(this).show()
            }

        }

        // Sets a click listener in the Create account textview
        binding.textViewCreateAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity()::class.java))
            //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Sets a click listener in the forgot password textview
        binding.textViewForgotPassword.setOnClickListener {
            startActivity(Intent(this, RecoverPasswordActivity()::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Sets a click listener in the login by Google button
        binding.btnSignInGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    // Method that tries to log in in Firebase by email
    private fun logInUserByEmail(email: String, password: String){
        try{
            // Try to sign in
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(
                    email,
                    password
                ).addOnCompleteListener {
                    if(!it.isSuccessful){ // Email or password are incorrect
                        binding.textViewErrorLogin.text = getString(R.string.wrong_credentials)
                        binding.textViewErrorLogin.visibility = View.VISIBLE
                        return@addOnCompleteListener
                    }else if(!firebaseAuth.currentUser!!.isEmailVerified){ // Email is not verified
                        binding.textViewErrorLogin.text = getString(R.string.error_email_verified)
                        binding.textViewErrorLogin.visibility = View.VISIBLE
                        return@addOnCompleteListener
                    }

                    //Everything is correct
                    binding.textViewErrorLogin.visibility = View.GONE

                    // Go to main activity after successful login
                    userLoggedIn()

                }
        }catch (e: Exception){
            Log.e(TAG, e.toString())
            Snackbar.make(binding.scrollView, getString(R.string.error_create_account), Snackbar.LENGTH_LONG).show()
        }

    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncherSignInGoogle.launch(signInIntent)
    }

    private var resultLauncherSignInGoogle = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // There are no request codes
        val data: Intent? = result.data

        Log.d(TAG, "Result code: ${result.resultCode}")

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)

            //userViewModel.insertNewUser(account.id, account.displayName)
            firebaseAuthWithGoogle(account.idToken!!)

        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
            Snackbar.make(binding.scrollView, getString(R.string.error_login_google), Snackbar.LENGTH_LONG).show()
        }

        //if (result.resultCode == Activity.RESULT_OK) {
        //}
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    if(task.getResult().additionalUserInfo?.isNewUser == true){
                        val name: String
                        if(firebaseAuth.currentUser!!.displayName != null){
                            name = firebaseAuth.currentUser!!.displayName.toString()
                        }else{
                            name = ""
                        }

                        userViewModel.insertNewUser(firebaseAuth.currentUser!!.uid, name)
                    }

                    userLoggedIn()

                } else {
                    // If sign in fails, display a message to the user.
                    Snackbar.make(binding.scrollView, getString(R.string.error_login_google), Snackbar.LENGTH_LONG).show()
                }
            }
    }

    // Method that start MainActivity
    private fun userLoggedIn(){
        val intent = Intent(this, MainActivity().javaClass)
        intent.putExtra(Constants.LOGGED_BEFORE_MAIN_ACTIVITY, true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}