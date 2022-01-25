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
import com.cice.sneakershop.models.FavouriteProduct
import com.cice.sneakershop.models.SimpleSneaker
import com.cice.sneakershop.utils.GenericFunctions
import com.cice.sneakershop.viewmodels.MainViewModel


class FavouriteSneakersAdapter(
    private val context: Context,
    private val mainViewModel: MainViewModel
): RecyclerView.Adapter<FavouriteSneakersAdapter.ViewHolder>() {

    private var TAG = "FavouriteSneakersAdapter"
    private lateinit var mListener : OnItemClickListener
    private var data = mutableListOf<FavouriteProduct>()

    // Listeners when an item of the favourite list is selected or the favourite icon is selected
    interface OnItemClickListener {
        fun onItemClick(position: Int, idSneaker: String)
        fun onFavouriteClick(position: Int, idSneaker: String, previousFavouriteValue: Boolean)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    fun setSimpleSneakersList(simpleSneakers: MutableList<FavouriteProduct>){
        data = simpleSneakers
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(view) {

        var imgProduct: ImageView
        var imgFavouriteIcon: ImageView
        var txtBrand: TextView
        var txtProductName: TextView
        var txtReducedPrice: TextView
        var txtPrice: TextView
        var txtDiscountPercentage: TextView



        init {
            // Define click listener for the ViewHolder's View.
            view.setOnClickListener {
                //listener.onItemClick(adapterPosition)
            }

            imgProduct = view.findViewById(R.id.imgProduct)
            imgFavouriteIcon = view.findViewById(R.id.imgFavouriteIcon)
            txtBrand = view.findViewById(R.id.txtBrand)
            txtProductName = view.findViewById(R.id.txtProductName)
            txtReducedPrice = view.findViewById(R.id.txtReducedPrice)
            txtPrice = view.findViewById(R.id.txtPrice)
            txtDiscountPercentage = view.findViewById(R.id.txtDiscountPercentage)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_favourite_product, viewGroup, false)

        return ViewHolder(view, mListener)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the simple sneaker
        val simpleSneaker = mainViewModel.getSimpleSneakerById(data[position].id_product)

        if(simpleSneaker != null){
            // Set the image
            Glide.with(context)
                .load(simpleSneaker.smallImageURL)
                .placeholder(R.drawable.ic_image_not_loaded)
                .into(viewHolder.imgProduct)

            // Set the textfields info
            viewHolder.txtBrand.text = simpleSneaker.brand
            viewHolder.txtProductName.text = simpleSneaker.name.uppercase()
            viewHolder.txtPrice.text = "${GenericFunctions.doubleToStringTwoDecimals(simpleSneaker.price.toDouble())} ${viewHolder.itemView.context.getString(
                R.string.euro_character)}"

            // Check if the product has a discount
            if(simpleSneaker.discount != 0){
                // Calculate the new price
                val reducedPrice = GenericFunctions.calculateDiscount(simpleSneaker.price,  simpleSneaker.discount)
                viewHolder.txtReducedPrice.text = "${GenericFunctions.doubleToStringTwoDecimals(reducedPrice)} ${viewHolder.itemView.context.getString(
                    R.string.euro_character)}"

                // Set the TextView visible
                viewHolder.txtReducedPrice.visibility = View.VISIBLE

                // Set the text of the previous price strikethrough
                viewHolder.txtPrice.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }

                // Set the TextView with the discount percentage visible and the text
                viewHolder.txtDiscountPercentage.visibility = View.VISIBLE
                viewHolder.txtDiscountPercentage.text = "-${simpleSneaker.discount}%"
            }else{
                // Set the TextView for the reduced price gone
                viewHolder.txtReducedPrice.visibility = View.GONE

                // Set the text of the previous price normal
                viewHolder.txtPrice.apply {
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                // Set the TextView for the percentage discount gone
                viewHolder.txtDiscountPercentage.visibility = View.GONE
            }

            Log.d(TAG, simpleSneaker.isFavourite.toString())

            // Set the color of the favourite icon depending if the sneaker is favourite or not
            viewHolder.imgFavouriteIcon.setColorFilter(
                if (simpleSneaker.isFavourite) context.getColor(R.color.red_relegation)
                else context.getColor(R.color.white)
            )

            // Set click listeners
            // Selected item
            viewHolder.itemView.setOnClickListener {
                mListener.onItemClick(position, simpleSneaker.id)
            }

            // Favourite icon selected
            viewHolder.imgFavouriteIcon.setOnClickListener {
                mListener.onFavouriteClick(position, simpleSneaker.id, simpleSneaker.isFavourite)
            }
        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size


}