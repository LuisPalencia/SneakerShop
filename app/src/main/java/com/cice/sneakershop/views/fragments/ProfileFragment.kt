package com.cice.sneakershop.views.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.cice.sneakershop.R
import com.cice.sneakershop.databinding.FragmentProfileBinding
import com.cice.sneakershop.viewmodels.MainViewModel
import com.cice.sneakershop.views.activities.MainActivity
import com.cice.sneakershop.views.interfaces.SupportFragmentManager
import java.io.IOException


class ProfileFragment : Fragment() {

    private val TAG = "BasketFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager

    private lateinit var rootView: View

    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try{
            supportFragmentManager = context as MainActivity
        }catch (e: IOException){
            Log.d(TAG, "MainActivity is on null state")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        rootView = binding.root

        setButtons()

        binding.txtHelloUser.text = "${getString(R.string.hello)} ${mainViewModel.getCurrentUser().name}"

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Method that sets the logic of the buttons of this view
    private fun setButtons(){
        binding.btnMyOrders.setOnClickListener {
            // Call the activity in order to show the orders fragment
            supportFragmentManager.showOrders()
        }

        binding.btnContact.setOnClickListener {
            // Call the activity in order to show the contact fragment
            supportFragmentManager.showContactDetails()
        }

        binding.btnLogoff.setOnClickListener {
            // Call the activity in order to logoff from the app
            supportFragmentManager.signOutUser()
        }
    }
}