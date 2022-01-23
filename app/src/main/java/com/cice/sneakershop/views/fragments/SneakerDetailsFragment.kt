package com.cice.sneakershop.views.fragments

import android.content.DialogInterface
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.cice.sneakershop.R
import com.cice.sneakershop.constants.Constants
import com.cice.sneakershop.databinding.FragmentSneakerDetailsBinding
import com.cice.sneakershop.models.Sneaker
import com.cice.sneakershop.utils.GenericFunctions
import com.cice.sneakershop.viewmodels.MainViewModel
import com.cice.sneakershop.views.interfaces.SupportFragmentManager
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso


class SneakerDetailsFragment : Fragment() {

    private val TAG = "SneakerDetailsFragment"
    private lateinit var rootView: View


    private var _binding: FragmentSneakerDetailsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var sneakerId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTranslationZ(getView()!!, 100f)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentSneakerDetailsBinding.inflate(inflater, container, false)
        rootView = binding.root

        val sneakerIdExtra = arguments?.getString(Constants.PRODUCT_ID_EXTRA)

        //Check if sneaker ID is correct
        if(sneakerIdExtra != null){
            sneakerId = sneakerIdExtra

            //Set up the toolbar and the views
            //setToolbar()

            //Set up the observer for the sneaker info
            setObservers()

            //Get all the sneaker info from the viewModel
            mainViewModel.getSneakerById(sneakerId)
        }else{
            //show error
            AlertDialog.Builder(this.requireContext())
                .setMessage(R.string.error_get_product)
                .setTitle(R.string.error)
                .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                    //finish()
                })
                .setCancelable(false)
                .create()
                .show()
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    //Method that is executed when the toolbar back button is pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

     */

    /*
    //Method that set up the toolbar
    private fun setToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

     */

    //Method that set up the observer
    private fun setObservers(){
        mainViewModel.sneaker.observe(this, Observer { sneaker ->
            showProductInfo(sneaker)
            Log.d(TAG, sneaker.toString())
        })
    }

    //Method that set up the activity layout with all the product info
    private fun showProductInfo(sneaker: Sneaker) {
        //Set up the image
        /*
        Glide.with(this.requireContext())
            .load(sneaker.mainImageURL)
            .into(binding.imgProduct)

         */

        Picasso.get()
            .load(sneaker.mainImageURL)
            .into(binding.imgProduct)

        // Sets the toolbar title
        binding.collapsingToolbar.title = sneaker.name

        //Set up the product info in the layout
        binding.txtBrand.text = sneaker.brand
        binding.txtProductName.text = sneaker.name
        binding.txtPrice.text = GenericFunctions.doubleToStringTwoDecimals(sneaker.price.toDouble())

        //check if the product has discount
        if(sneaker.discount != 0){
            binding.txtReducedPrice.visibility = View.VISIBLE
            val reducedPrice = GenericFunctions.calculateDiscount(sneaker.price,  sneaker.discount)
            binding.txtReducedPrice.text = GenericFunctions.doubleToStringTwoDecimals(reducedPrice)

            //show the old price strikethrough
            binding.txtPrice.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }else{
            //As there is no discount, the reduced price is removed from the view
            binding.txtReducedPrice.visibility = View.GONE
            //Don't show the price textview strikethrough
            binding.txtPrice.apply {
                paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        binding.imgFavouriteIcon.setColorFilter(
            if (sneaker.isFavourite) this.requireContext().getColor(R.color.red_relegation)
            else this.requireContext().getColor(R.color.white)
        )

        binding.imgFavouriteIcon.setOnClickListener {
            if(mainViewModel.simpleSneakers.value != null){
                Log.d(TAG, "isFavourite: ${sneaker.isFavourite}")
                if(sneaker.isFavourite){
                    mainViewModel.removeSneakerFromFavouriteItems(sneaker)
                }else{
                    mainViewModel.addSneakerToFavouriteItems(sneaker)
                }

                binding.imgFavouriteIcon.setColorFilter(
                    if (sneaker.isFavourite) this.requireContext().getColor(R.color.red_relegation)
                    else this.requireContext().getColor(R.color.white)
                )
            }
        }

        // Spinner configuration
        val listAvailableSizes = mutableListOf<String>()

        //Add the sizes to the list
        for(availableSize in sneaker.availableSizes){
            if(availableSize.value.toInt() > 0){
                listAvailableSizes.add(availableSize.key)
            }
        }

        //Sort the sizes list
        listAvailableSizes.sort()
        listAvailableSizes.add(0, getString(R.string.choose_size))


        //Create an adapter for the spinner and set it on the spinner view
        val adapter = ArrayAdapter(this.requireContext(), R.layout.support_simple_spinner_dropdown_item, listAvailableSizes)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        binding.spinnerAvailableSizes.adapter = adapter

        //Listener for the add to basket button
        binding.btnAddToBasket.setOnClickListener {
            if(GenericFunctions.isDeviceConnectedToInternet(this.requireContext())){
                val selectedSize = binding.spinnerAvailableSizes.selectedItem.toString()
                if(selectedSize != getString(R.string.choose_size)){ //check if default option is not selected
                    if(!mainViewModel.isItemAlreadyInBasket(sneakerId, selectedSize)){ //Check if product with the same size is already added
                        mainViewModel.addItemToBasket(sneakerId, selectedSize)
                        Snackbar.make(binding.coordinatorLayout, getString(R.string.item_added_to_basket), Snackbar.LENGTH_LONG).show()
                    }else{
                        Snackbar.make(binding.coordinatorLayout, getString(R.string.error_item_already_in_basket), Snackbar.LENGTH_LONG).show()
                    }
                }else{
                    Snackbar.make(binding.coordinatorLayout, getString(R.string.error_select_size), Snackbar.LENGTH_LONG).show()
                }
            }else{
                //GenericFunctions.getErrorInternetDialog(this).show()
                Snackbar.make(binding.coordinatorLayout, getString(R.string.error_internet_content), Snackbar.LENGTH_LONG).show()
            }

        }

        //Set the description and the review views
        binding.txtDescription.text = sneaker.description
        binding.txtReviewsTitle.text = "${getString(R.string.reviews)} (${sneaker.totalReviews})"
        binding.txtAverageReview.text = "${sneaker.averageMarkReview}/5"
        binding.ratingBarReviews.rating = sneaker.averageMarkReview.toFloat()
    }
}