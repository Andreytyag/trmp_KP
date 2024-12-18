package com.example.ridewithobstacles.GameObjects


import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.ridewithobstacles.GameActivity
import com.example.ridewithobstacles.GamePanel
import com.example.ridewithobstacles.MainActivity
import com.example.ridewithobstacles.R




class Car: Object {

    constructor(context: Context, carBitmap: Bitmap){
        bitmap = carBitmap
        sizeX = 3f
        sizeY = 5f
        x = (GamePanel.roadLanesWidth - sizeX) / 2
        y = GamePanel.maxY - sizeY - 3
        speed = 5.3f

        init(context)
    }

    override fun init(context: Context) {
        val cBitmap = bitmap

        bitmap = Bitmap.createScaledBitmap(
            cBitmap, (sizeX * GamePanel.unitW).toInt(), (sizeY * GamePanel.unitH).toInt(), false
        )

        cBitmap.recycle()
    }

    override fun update() {
        if(GameActivity.leftSwap){
            if(x - speed >= 0) {
                x -= speed;
            }
            GameActivity.leftSwap = false
        }
        if(GameActivity.rightSwap){
            if(x + speed < GamePanel.maxX) {
                x += speed;
            }
            GameActivity.rightSwap = false
        }
    }

    fun setDefaultLocation(){
        x = (GamePanel.roadLanesWidth - sizeX) / 2
        y = GamePanel.maxY - sizeY - 3
    }
}