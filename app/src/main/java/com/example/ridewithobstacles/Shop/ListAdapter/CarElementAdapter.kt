package com.example.ridewithobstacles.Shop.ListAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ridewithobstacles.R
import com.example.ridewithobstacles.Shop.CarElement
import com.example.ridewithobstacles.Shop.OnItemClickListener
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.Resources

class CarElementAdapter (private var carElementList: List<CarElement>) : RecyclerView.Adapter<CarElementAdapter.CarViewHolder>(){

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarElementAdapter.CarViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.car_element_view, parent, false)
        return CarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarElementAdapter.CarViewHolder, position: Int) {
        val currentItem = carElementList[position]

        holder.priceTextView.text = currentItem.price.toString()
        //holder.descriptionTextView.text = currentItem.description

        // Загрузка изображения с использованием Glide
        Glide.with(holder.itemView.context)
            .load(currentItem.image)
            .into(holder.imageView)

        if(currentItem.isBuyed){
            holder.buyButton.visibility = View.INVISIBLE
            holder.isBuyed.visibility = View.VISIBLE
            holder.isChoosed.visibility = View.VISIBLE

            if(currentItem.isChoosed){
                holder.isChoosed.setText("Машина выбрана")
            }
            else{
                holder.isChoosed.setText("Машина не выбрана")
            }
            holder.chooseButton.visibility = View.VISIBLE
            holder.chooseButton.setOnClickListener{
                onItemClickListener?.onItemChoose(position)
            }
        }
        else{
            holder.isBuyed.visibility = View.INVISIBLE
            holder.isChoosed.visibility = View.INVISIBLE
            holder.chooseButton.visibility = View.INVISIBLE
            holder.buyButton.setOnClickListener{
                onItemClickListener?.onItemBuy(position, currentItem.price)
            }
        }
    }

    override fun getItemCount(): Int {
        return carElementList.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.carImage)
        val priceTextView: TextView = itemView.findViewById(R.id.price)
        val buyButton: Button = itemView.findViewById(R.id.buyButton)
        val chooseButton: Button = itemView.findViewById(R.id.chooseButton)
        val isBuyed: TextView = itemView.findViewById(R.id.isBuyed)
        val isChoosed: TextView = itemView.findViewById(R.id.isChoosed)
    }


}