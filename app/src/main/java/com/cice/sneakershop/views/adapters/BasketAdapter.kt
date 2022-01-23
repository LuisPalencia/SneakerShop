package com.cice.sneakershop.views.adapters

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cice.sneakershop.R
import com.cice.sneakershop.models.BasketItem
import com.cice.sneakershop.utils.GenericFunctions
import com.cice.sneakershop.viewmodels.MainViewModel

class BasketAdapter(
    private val context: Context,
    private val mainViewModel: MainViewModel
    ): RecyclerView.Adapter<BasketAdapter.ViewHolder>() {

    private var TAG = "BasketAdapter"
    private lateinit var mListener : OnItemClickListener
    private var data = mutableListOf<BasketItem>()

    // Listeners when an item of the basket list is selected or removed
    interface OnItemClickListener {
        fun onItemClick(position: Int, idSneaker: String)
        fun onRemoveItemClick(idBasketItem: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    fun setBasketItemsList(simpleSneakers: MutableList<BasketItem>){
        data = simpleSneakers
    }



    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imgProduct: ImageView
        var imgRemoveItem: ImageView
        var txtBrand: TextView
        var txtProductName: TextView
        var txtReducedPrice: TextView
        var txtPrice: TextView
        var txtSize: TextView



        init {
            imgProduct = view.findViewById(R.id.imgProduct)
            imgRemoveItem = view.findViewById(R.id.imgRemoveItem)
            txtBrand = view.findViewById(R.id.txtBrand)
            txtProductName = view.findViewById(R.id.txtProductName)
            txtReducedPrice = view.findViewById(R.id.txtReducedPrice)
            txtPrice = view.findViewById(R.id.txtPrice)
            txtSize = view.findViewById(R.id.txtSize)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_basket_product, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the simple sneaker
        val simpleSneaker = mainViewModel.getSimpleSneakerById(data[position].id_product)

        if(simpleSneaker != null){
            // Set the image
            Glide.with(context)
                .load(simpleSneaker.smallImageURL)
                .into(viewHolder.imgProduct)

            // Set the textfields info
            viewHolder.txtBrand.text = simpleSneaker.brand
            viewHolder.txtProductName.text = simpleSneaker.name.uppercase()
            viewHolder.txtSize.text = "${viewHolder.itemView.context.getString(R.string.size)}: ${data[position].size}"

            viewHolder.txtPrice.text = "${GenericFunctions.doubleToStringTwoDecimals(simpleSneaker.price.toDouble())} ${viewHolder.itemView.context.getString(R.string.euro_character)}"

            // Check if the product has a discount
            if(simpleSneaker.discount != 0){
                // Calculate the new price
                val reducedPrice = GenericFunctions.calculateDiscount(simpleSneaker.price, simpleSneaker.discount)
                viewHolder.txtReducedPrice.text = "${GenericFunctions.doubleToStringTwoDecimals(reducedPrice)} ${viewHolder.itemView.context.getString(R.string.euro_character)}"

                // Set the TextView visible
                viewHolder.txtReducedPrice.visibility = View.VISIBLE

                // Set the text of the previous price strikethrough
                viewHolder.txtPrice.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
            }else{
                // Set the TextView gone
                viewHolder.txtReducedPrice.visibility = View.GONE

                // Set the text of the previous price normal
                viewHolder.txtPrice.apply {
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }

            // Set click listeners
            // Selected item
            viewHolder.itemView.setOnClickListener {
                mListener.onItemClick(position, data[position].id_product)
            }

            // Basket item is removed
            viewHolder.imgRemoveItem.setOnClickListener {
                mListener.onRemoveItemClick(data[position].id)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size


}