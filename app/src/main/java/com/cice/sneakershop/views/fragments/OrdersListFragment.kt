package com.cice.sneakershop.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cice.sneakershop.R
import com.cice.sneakershop.databinding.FragmentOrdersListBinding
import com.cice.sneakershop.viewmodels.MainViewModel
import com.cice.sneakershop.views.adapters.OrdersAdapter
import com.google.android.material.snackbar.Snackbar


class OrdersListFragment : Fragment() {

    private val TAG = "OrdersListFragment"
    private lateinit var rootView: View
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: OrdersAdapter

    private var _binding: FragmentOrdersListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrdersListBinding.inflate(inflater, container, false)
        rootView = binding.root

        // Set up all the necessary things
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

    // Set the layout of the fragment depending if there are orders placed or not
    private fun setLayout(){
        if(adapter.itemCount > 0){ // There are orders places
            binding.ordersRecyclerView.visibility = View.VISIBLE
            binding.layoutNoOrders.visibility = View.GONE
        }else{ // There are no orders places
            binding.ordersRecyclerView.visibility = View.GONE
            binding.layoutNoOrders.visibility = View.VISIBLE

        }
    }

    // Method that sets the observer for the orders
    private fun setObservers(){
        mainViewModel.orders.observe(this, { orders ->
            // Update the view
            adapter.setOrdersList(orders)
            adapter.notifyDataSetChanged()
            setLayout()
        })
    }

    // Method that sets the recycler view of the orders fragment
    private fun setRecyclerView(){
        // Create the adapter
        adapter = OrdersAdapter(this.requireContext(), mainViewModel)

        adapter.setOnItemClickListener(object : OrdersAdapter.OnItemClickListener{
            // When an item is selected, show the info in a Snackbar
            override fun onItemClick(position: Int, idOrder: String) {
                Snackbar.make(binding.constraintLayout, "${getString(R.string.order_selected)}: $idOrder", Snackbar.LENGTH_LONG).show()
            }
        })

        // Set the adapter and the layout manager for the recyclerview
        binding.ordersRecyclerView.adapter = adapter
        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    //Function that sets up the go to shop button
    private fun setButton(){
        binding.btnOrdersGoBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}