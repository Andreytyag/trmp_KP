package com.example.ridewithobstacles.Shop

interface OnItemClickListener {
    fun onItemBuy(position: Int, price: Int)
    fun onItemChoose(position: Int)
}