package com.cice.sneakershop.views.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cice.sneakershop.R
import com.cice.sneakershop.databinding.FragmentFavouriteSneakersBinding
import com.cice.sneakershop.models.FavouriteProduct
import com.cice.sneakershop.viewmodels.MainViewModel
import com.cice.sneakershop.views.activities.MainActivity
import com.cice.sneakershop.views.adapters.FavouriteSneakersAdapter
import com.cice.sneakershop.views.interfaces.SupportFragmentManager
import com.google.android.material.snackbar.Snackbar
import java.io.IOException


class FavouriteSneakersFragment : Fragment() {


    private val TAG = "FavouriteSneakersFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View
    private val mainViewModel: MainViewModel by activityViewModels()

    // Adapter for the recyclerview
    private lateinit var adapter: FavouriteSneakersAdapter

    private var _binding: FragmentFavouriteSneakersBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Get the MainActivity connector
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

        _binding = FragmentFavouriteSneakersBinding.inflate(inflater, container, false)
        rootView = binding.root

        // Set up all the necessary things for the view
        setRecyclerView()
        setObservers()
        setLayout()
        setButton()

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Method that sets the observer for the favourite sneaker items
    private fun setObservers(){
        mainViewModel.favouriteProducts.observe(this, { simpleFavouriteSneakers ->
            setFavouriteSneakersListData(simpleFavouriteSneakers)
        })
    }

    // Method that receives the new favourite lists and sets it in the fragment view
    private fun setFavouriteSneakersListData(simpleFavouriteSneakers: MutableList<FavouriteProduct>){
        // Update list
        adapter.setSimpleSneakersList(simpleFavouriteSneakers)
        adapter.notifyDataSetChanged()
        binding.txtNumberItems.text = "${rootView.resources.getString(R.string.number_favourite_items)} ${simpleFavouriteSneakers.size}"
        // Check in case there are no favourite items in order to change layout
        setLayout()

    }

    // Method that sets the recycler view of the favourite sneakers fragment
    private fun setRecyclerView(){
        // Create the adapter
        adapter = FavouriteSneakersAdapter(this.requireContext(), mainViewModel)

        adapter.setOnItemClickListener(object : FavouriteSneakersAdapter.OnItemClickListener{
            // When an item is selected, it opens the sneaker details
            override fun onItemClick(position: Int, idSneaker: String) {
                if(mainViewModel.favouriteProducts.value != null){
                    supportFragmentManager.showSneakerDetailsFavourites(
                        //sharedViewModel.simpleFavouriteSneakers.value!![position].id
                        idSneaker
                    )
                }

            }
            // Listener when the favourite icon of an element is pressed
            override fun onFavouriteClick(position: Int, idSneaker: String, previousFavouriteValue: Boolean) {
                if(mainViewModel.favouriteProducts.value != null){
                    // As it is in the favourites list, the value of previousFavouriteValue is true, so the item is removed from the favourite list
                    mainViewModel.removeSneakerFromFavouriteItems(idSneaker)
                    //adapter.notifyItemRemoved(position)
                    Snackbar.make(binding.constraintLayoutFavourites, getString(R.string.favourite_item_removed), Snackbar.LENGTH_LONG).show()
                }
            }

        })

        // Set the adapter and the layout manager for the recyclerview
        binding.favouritesRecyclerView.adapter = adapter
        binding.favouritesRecyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    // Set the layout of the fragment depending if there are favourite sneakers or not
    private fun setLayout(){
        if(adapter.itemCount > 0){ // There are favourite sneakers
            binding.layoutFavouriteItems.visibility = View.VISIBLE
            binding.layoutNoFavouriteItems.visibility = View.GONE
        }else{ // There are no favourite sneakers
            binding.layoutFavouriteItems.visibility = View.GONE
            binding.layoutNoFavouriteItems.visibility = View.VISIBLE

        }
    }

    //Function that sets up the go to shop button
    private fun setButton(){
        binding.btnGoToShop.setOnClickListener {
            // Go to shop menu fragment
            supportFragmentManager.setShopMenu()
        }
    }
}