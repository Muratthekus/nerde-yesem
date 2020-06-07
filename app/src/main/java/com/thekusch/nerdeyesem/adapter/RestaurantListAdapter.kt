package com.thekusch.nerdeyesem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.ajalt.timberkt.Timber
import com.thekusch.nerdeyesem.R
import com.thekusch.nerdeyesem.data.model.nearby.NearbyRestaurant
import com.thekusch.nerdeyesem.data.model.nearby.NearbyResult
import com.thekusch.nerdeyesem.databinding.RecyclerviewListRestaurantBinding
import kotlinx.android.synthetic.main.recyclerview_list_restaurant.view.*

class RestaurantListAdapter : RecyclerView.Adapter<RestaurantListAdapter.NearbyViewHolder>() {

    private var nearbyList : List<NearbyRestaurant> = ArrayList()
    private lateinit var listener : ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyViewHolder {
        val itemView : View = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_list_restaurant,parent,false)
        return NearbyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NearbyViewHolder, position: Int) {
        val item = nearbyList[position]
        holder.resName.text = item.restaurant?.name
        holder.resCuisines.text = item.restaurant?.cuisines
        val rating = "${item.restaurant?.userRating?.aggregate_rating}/5"
        holder.aggregateRate.text = rating
        holder.resAddress.text = item.restaurant?.location?.address
        holder.resPhone.text = item.restaurant?.phone_numbers
        if(item.restaurant?.phone_numbers==null)
            holder.phoneWrapper.visibility = View.GONE

        val resId:Int?= item.restaurant?.id?.toInt()
        holder.itemView.setOnClickListener{
            listener.onItemClick(resId)
        }
    }

    inner class NearbyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var resName : TextView = itemView.findViewById(R.id.resName)
        var aggregateRate : TextView = itemView.findViewById(R.id.aggregate_rating)
        var resCuisines : TextView = itemView.findViewById(R.id.resCuisines)
        var resAddress : TextView = itemView.findViewById(R.id.resAddress)
        var resPhone : TextView = itemView.findViewById(R.id.resPhone)
        var phoneWrapper : LinearLayout = itemView.phoneWrapper
    }
    fun setData(list:List<NearbyRestaurant>){
        nearbyList = list
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return nearbyList.size
    }
    //Click listener call backs
    interface ItemClickListener{
        fun onItemClick(res_id:Int?)
    }
    fun setItemClickListener(listener:ItemClickListener){
        this.listener = listener
    }
}