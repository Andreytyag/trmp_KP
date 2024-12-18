package com.example.ridewithobstacles.GameObjects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import com.example.ridewithobstacles.GamePanel


abstract class Object {
    public var x: Float = 0f
    public var y: Float = 0f
    public var sizeX: Float = 0f
    public var sizeY: Float = 0f
    protected var speed: Float = 0.5f
    protected var bitmapId: Int = 0
    protected lateinit var bitmap: Bitmap

    open fun init(context: Context) {
        val cBitmap = BitmapFactory.decodeResource(context.resources, bitmapId)

        bitmap = Bitmap.createScaledBitmap(
            cBitmap, (sizeX * GamePanel.unitW).toInt(), (sizeY * GamePanel.unitH).toInt(), false
        )

        cBitmap.recycle()
    }

    abstract fun update()

    fun drow(paint: Paint?, canvas: Canvas) {
        canvas.drawBitmap(bitmap, x * GamePanel.unitW, y * GamePanel.unitH, paint)
    }
}