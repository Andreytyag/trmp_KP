package com.example.ridewithobstacles.Database.Repositories.Implementations

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.ridewithobstacles.Database.DbHelper
import com.example.ridewithobstacles.Database.DbHelper.Companion.Car
import com.example.ridewithobstacles.Database.DbHelper.Companion.User
import com.example.ridewithobstacles.Database.Models.CarModel
import com.example.ridewithobstacles.Database.Models.UserModel

class CarRepository(private val db: SQLiteDatabase) {
    fun insert(car: CarModel): Long {
        val values = ContentValues().apply {
            //put(Car.COLUMN_ID, car.id)
            put(Car.COLUMN_NAME, car.carName)
            put(Car.COLUMN_PRICE, car.price)
            put(Car.COLUMN_BUYED, car.buyed)
            put(Car.COLUMN_CHOOSED, car.choosed)
        }
        val result = db.insert(Car.TABLE_NAME, null, values)
        return result


    }

    fun update(car: CarModel) {
        val values = ContentValues().apply {
            put(Car.COLUMN_NAME, car.carName)
            put(Car.COLUMN_PRICE, car.price)
            put(Car.COLUMN_BUYED, car.buyed)
            put(Car.COLUMN_CHOOSED, car.choosed)
        }

        db.update(
            Car.TABLE_NAME,
            values,
            "${User.COLUMN_ID} = ?",
            arrayOf(car.id.toString())
        )
    }

    fun getAll(): List<CarModel> {
        val cars = mutableListOf<CarModel>()
        val cursor = db.query(
            Car.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val car = CarModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow(Car.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Car.COLUMN_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Car.COLUMN_PRICE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Car.COLUMN_BUYED)) > 0,
                    cursor.getInt(cursor.getColumnIndexOrThrow(Car.COLUMN_CHOOSED)) > 0
                )
                cars.add(car)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return cars
    }

    fun getById(id: Int): CarModel? {
        val cursor = db.query(
            Car.TABLE_NAME,
            null,
            "${Car.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var car: CarModel? = null
        with(cursor) {
            if (cursor.moveToFirst()) {
                car = CarModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow(Car.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Car.COLUMN_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Car.COLUMN_PRICE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Car.COLUMN_BUYED)) > 0,
                    cursor.getInt(cursor.getColumnIndexOrThrow(Car.COLUMN_CHOOSED)) > 0
                )
            }
        }
        cursor.close()
        return car
    }
}