package com.cice.sneakershop.views.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cice.sneakershop.R
import com.cice.sneakershop.models.BasketItem
import com.cice.sneakershop.models.Order
import com.cice.sneakershop.utils.GenericFunctions
import com.cice.sneakershop.viewmodels.MainViewModel

class OrdersAdapter(
    private val context: Context,
    private val mainViewModel: MainViewModel
): RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    private var TAG = "OrdersAdapter"
    private lateinit var mListener : OnItemClickListener
    private var data = mutableListOf<Order>()

    interface OnItemClickListener {
        fun onItemClick(position: Int, idOrder: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    fun setOrdersList(orders: MutableList<Order>){
        data = orders
    }



    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(view) {

        var imgProduct: ImageView
        var txtIdOrder: TextView
        var txtTotalPrice: TextView
        var txtItemsNumber: TextView
        var txtDate: TextView


        init {
            imgProduct = view.findViewById(R.id.imgProduct)
            txtIdOrder = view.findViewById(R.id.txtIdOrder)
            txtTotalPrice = view.findViewById(R.id.txtTotalPrice)
            txtItemsNumber = view.findViewById(R.id.txtItemsNumber)
            txtDate = view.findViewById(R.id.txtDate)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_order_list, viewGroup, false)

        return ViewHolder(view, mListener)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Set the data of the textfields
        viewHolder.txtIdOrder.text = "${viewHolder.itemView.context.getString(R.string.order_id)}: ${data[position].orderId}"
        viewHolder.txtTotalPrice.text = "${viewHolder.itemView.context.getString(R.string.price)}: ${GenericFunctions.doubleToStringTwoDecimals(data[position].totalPrice)} ${viewHolder.itemView.context.getString(R.string.euro_character)}"
        viewHolder.txtItemsNumber.text = "${viewHolder.itemView.context.getString(R.string.items)}: ${data[position].products.size.toString()}"
        viewHolder.txtDate.text = "${viewHolder.itemView.context.getString(R.string.date)}: ${data[position].dateOfOrder}"

        // Set the image of the order
        // It places the image of the first product
        var urlImage = mainViewModel.getSmallImageSneaker(data[position].products[0].idProduct)
        if(urlImage != null){
            Glide.with(context)
                .load(urlImage)
                .into(viewHolder.imgProduct)
        }

        viewHolder.itemView.setOnClickListener {
            mListener.onItemClick(position, data[position].orderId)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}