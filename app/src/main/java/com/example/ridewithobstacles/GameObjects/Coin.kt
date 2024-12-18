package com.example.ridewithobstacles.GameObjects

import android.content.Context
import com.example.ridewithobstacles.GamePanel
import com.example.ridewithobstacles.R
import kotlin.random.Random

class Coin: CollisionObject {

    constructor(context: Context){

        bitmapId = R.drawable.coin
        var rnd = (0 ..< GamePanel.roadLanesCount).random()
        y = 0f
        x = rnd * GamePanel.roadLanesWidth
        sizeY = 3f
        sizeX = GamePanel.roadLanesWidth

        init(context)
    }

    override fun update() {
        y += speed;
    }

//    fun isCollision(carX: Float, carY: Float, carSizeX: Float, carSizeY: Float): Boolean {
//        return !(((x + sizeX) < carX) || (x > (carX + carSizeX)) || (y > (carY + carSizeY)) || ((y + sizeY) < carY))
//    }

}