package com.cice.sneakershop.views.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cice.sneakershop.R
import com.cice.sneakershop.databinding.FragmentShopBinding
import com.cice.sneakershop.models.SimpleSneaker
import com.cice.sneakershop.viewmodels.MainViewModel
import com.cice.sneakershop.views.activities.MainActivity
import com.cice.sneakershop.views.adapters.ShopAdapter
import com.cice.sneakershop.views.interfaces.SupportFragmentManager
import java.io.IOException


class ShopFragment : Fragment() {
    private val TAG = "HomeFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: ShopAdapter

    private var _binding: FragmentShopBinding? = null
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
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        rootView = binding.root

        // Set up all the necessary things for the view
        setRecyclerView()
        setObservers()

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Method that sets the observer for the sneaker items
    private fun setObservers(){
        mainViewModel.simpleSneakers.observe(this, { simpleSneakers ->
            adapter.setSimpleSneakersList(simpleSneakers)
            adapter.notifyDataSetChanged()
            //txtNumberItems.text = "${simpleSneakers.size} ${rootView.resources.getString(R.string.items_showed)}"
        })
    }

    private fun setRecyclerView(){
        // Create the adapter
        adapter = ShopAdapter(this.requireContext())

        adapter.setOnItemClickListener(object : ShopAdapter.OnItemClickListener{
            // When an item is selected, it opens the sneaker details
            override fun onItemClick(position: Int, idSneaker: String) {
                if(mainViewModel.simpleSneakers.value != null){
                    supportFragmentManager.showSneakerDetailsShop(
                        idSneaker
                    )
                }
            }

            // Listener when the favourite icon of an element is pressed
            override fun onFavouriteClick(position: Int, idSneaker: String, previousFavouriteValue: Boolean) {
                if(mainViewModel.simpleSneakers.value != null){
                    if(previousFavouriteValue){
                        mainViewModel.removeSneakerFromFavouriteItems(idSneaker)
                    }else{
                        mainViewModel.addSneakerToFavouriteItems(idSneaker)
                    }

                    adapter.notifyItemChanged(position)
                }
            }

        })

        // Set the adapter and the layout manager for the recyclerview
        binding.shopRecyclerView.adapter = adapter
        binding.shopRecyclerView.layoutManager = GridLayoutManager(this.context, 2)
    }
}