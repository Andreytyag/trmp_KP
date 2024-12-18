package com.example.ridewithobstacles.GameObjects

abstract class CollisionObject: Object() {
    fun checkCollision(carX: Float, carY: Float, carSizeX: Float, carSizeY: Float): Boolean {
        return !(((x + sizeX) < carX) || (x > (carX + carSizeX)) || (y > (carY + carSizeY)) || ((y + sizeY) < carY))
    }
}