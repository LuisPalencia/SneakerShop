package com.cice.sneakershop.views.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cice.sneakershop.R
import com.cice.sneakershop.constants.Constants
import com.cice.sneakershop.databinding.FragmentBasketBinding
import com.cice.sneakershop.models.BasketItem
import com.cice.sneakershop.utils.GenericFunctions
import com.cice.sneakershop.viewmodels.MainViewModel
import com.cice.sneakershop.views.activities.MainActivity
import com.cice.sneakershop.views.adapters.BasketAdapter
import com.cice.sneakershop.views.interfaces.SupportFragmentManager
import com.google.android.material.snackbar.Snackbar
import java.io.IOException


class BasketFragment : Fragment() {

    private val TAG = "BasketFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: BasketAdapter

    private var totalPrice = 0.0

    private var _binding: FragmentBasketBinding? = null
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
        _binding = FragmentBasketBinding.inflate(inflater, container, false)
        rootView = binding.root

        binding.txtShippingCostInfo.text = "${getString(R.string.shipping_costs_info)} ${Constants.MINIMUM_PRICE_FREE_SHIPPING_COSTS} ${getString(R.string.euro_character)} "

        // Set all basket things
        setRecyclerView()
        setObservers()
        setButtons()
        setLayout()
        setInfoBasket()

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Method that sets the observer for the basket items
    private fun setObservers(){
        mainViewModel.basketItems.observe(this, { basketItems ->
            refreshList(basketItems)
        })
    }

    // Method that refresh the list of basket items and the layout of the fragment
    private fun refreshList(basketItems: MutableList<BasketItem>){
        // Update the adapter
        adapter.setBasketItemsList(basketItems)
        adapter.notifyDataSetChanged()
        // check the layout in case there are no items in the basket
        setLayout()
        // Update the fragment info
        setInfoBasket()
    }

    // Method that sets the recycler view of the basket fragment
    private fun setRecyclerView(){
        // Create the adapter
        adapter = BasketAdapter(this.requireContext(), mainViewModel)

        adapter.setOnItemClickListener(object : BasketAdapter.OnItemClickListener{
            // When an item is selected, it opens the sneaker details
            override fun onItemClick(position: Int, idSneaker: String) {
                if(mainViewModel.simpleSneakers.value != null){
                    // Call the activity in order to show sneaker details
                    supportFragmentManager.showSneakerDetailsBasket(
                        idSneaker
                    )
                }
            }

            //Listener when a basket item is removed
            override fun onRemoveItemClick(idBasketItem: String) {
                mainViewModel.removeItemFromBasket(idBasketItem)
                //refreshList(mainViewModel.getBasketItems())
                Snackbar.make(binding.constraintLayout, getString(R.string.basket_item_removed), Snackbar.LENGTH_LONG).show()
            }
        })

        // Set the adapter and the layout manager for the recyclerview
        binding.basketRecyclerView.adapter = adapter
        binding.basketRecyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    // Set the logic of the buttons
    private fun setButtons(){
        binding.btnGoToShop.setOnClickListener {
            supportFragmentManager.setShopMenu()
        }
    }

    // Set the layout of the fragment depending if there are basket items or not
    private fun setLayout(){
        if(adapter.itemCount > 0){ // There are basket items
            binding.basketRecyclerView.visibility = View.VISIBLE
            binding.layoutNoBasketItems.visibility = View.GONE
        }else{ // There are no basket items
            binding.basketRecyclerView.visibility = View.GONE
            binding.layoutNoBasketItems.visibility = View.VISIBLE
        }
    }

    // Set the info of the basket fragment
    private fun setInfoBasket(){
        val basketItems = mainViewModel.getBasketItems()

        if(basketItems.size > 0){  // There are basket items
            // Calculate the price of the basket items in the basket
            calculatePrice(basketItems)

            // check if it exceeds the minimun for the free shipping costs
            if(totalPrice > Constants.MINIMUM_PRICE_FREE_SHIPPING_COSTS){
                binding.txtPriceOfShip.text = "0.00 ${context?.getString(R.string.euro_character)}"
            }else{
                // First, add the costs of the shipping costs
                totalPrice += Constants.SHIPPING_COSTS
                binding.txtPriceOfShip.text = "${GenericFunctions.doubleToStringTwoDecimals(Constants.SHIPPING_COSTS)} ${context?.getString(R.string.euro_character)}"
            }

            binding.txtFinalPrice.text = "${GenericFunctions.doubleToStringTwoDecimals(totalPrice)} ${context?.getString(R.string.euro_character)}"

            // Enable the finish order button
            enableButton()
        }else{ // There are no basket items
            binding.txtFinalPrice.text = "0.00 ${context?.getString(R.string.euro_character)}"
            binding.txtPriceOfShip.text = "0.00 ${context?.getString(R.string.euro_character)}"
            // Disable the finish order button
            disableButton()
        }
    }

    // Method that enables the confirm order button
    private fun enableButton(){
        binding.btnConfirmOrder.setOnClickListener {
            val basketProducts = mainViewModel.getBasketItems()
            if(basketProducts.size > 0){
                // Show a dialog in order to confirm the order
                AlertDialog.Builder(context!!)
                    .setMessage(R.string.confirm_order_message)
                    .setTitle(R.string.confirm_order_title)
                    .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, id ->
                        // If the user confirms, the order is created
                        val result = mainViewModel.createOrder(mainViewModel.getCurrentUser().uuid, basketProducts, totalPrice)

                        if(result){ // If everything is ok, the layout needs to be refreshed
                            refreshList(mainViewModel.getBasketItems())
                            Snackbar.make(binding.constraintLayout, getString(R.string.order_completed), Snackbar.LENGTH_LONG).show()
                        }else{
                            Snackbar.make(binding.constraintLayout, getString(R.string.error_finish_order), Snackbar.LENGTH_LONG).show()
                        }
                    })
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, id ->
                        // Cancel the order
                        dialog.dismiss()
                    })
                    .create()
                    .show()
            }else{
                Snackbar.make(binding.constraintLayout, getString(R.string.error_basket_empty), Snackbar.LENGTH_LONG).show()
            }

        }
    }

    // Method that disables the confirm order button
    private fun disableButton(){
        binding.btnConfirmOrder.setOnClickListener {
            Snackbar.make(binding.constraintLayout, getString(R.string.error_basket_empty), Snackbar.LENGTH_LONG).show()
        }
    }

    // Method that calculates the price of the order with all the basket items
    private fun calculatePrice(basketItems: MutableList<BasketItem>){
        totalPrice = 0.0
        for(basketItem in basketItems){
            val sneaker = mainViewModel.getSimpleSneakerById(basketItem.id_product)
            if(sneaker != null){
                var price: Double
                if(sneaker.discount != 0){
                    price = GenericFunctions.calculateDiscount(sneaker.price, sneaker.discount)
                }else{
                    price = sneaker.price.toDouble()
                }
                totalPrice += price
            }
        }
    }
}