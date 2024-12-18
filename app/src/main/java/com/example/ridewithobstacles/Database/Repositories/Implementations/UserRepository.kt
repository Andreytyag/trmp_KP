package com.example.ridewithobstacles.Database.Repositories.Implementations

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.ridewithobstacles.Database.DbHelper
import android.database.sqlite.SQLiteOpenHelper;
import com.example.ridewithobstacles.Database.DbHelper.Companion.User
import com.example.ridewithobstacles.Database.DbHelper.Companion.Car
import com.example.ridewithobstacles.Database.Models.CarModel
import com.example.ridewithobstacles.Database.Models.UserModel

class UserRepository(private val db: SQLiteDatabase) {
    fun insert(user: UserModel): Long {
        val values = ContentValues().apply {
            put(User.COLUMN_USERNAME, user.username)
            put(User.COLUMN_MONEY, user.money)
            put(User.COLUMN_CAR, user.car.id)
            put(User.COLUMN_PASSWORD, user.password)
            put(User.COLUMN_TOKEN, user.token)
            put(User.COLUMN_SYNCED, user.synced)
        }

        return db.insert(User.TABLE_NAME, null, values)
    }

    fun update(user: UserModel) {
        val values = ContentValues().apply {
            put(User.COLUMN_USERNAME, user.username)
            put(User.COLUMN_MONEY, user.money)
            put(User.COLUMN_CAR, user.car.id)
            put(User.COLUMN_PASSWORD, user.password)
            put(User.COLUMN_TOKEN, user.token)
            put(User.COLUMN_SYNCED, user.synced)
        }

        db.update(
            User.TABLE_NAME,
            values,
            "${User.COLUMN_ID} = ?",
            arrayOf(user.id.toString())
        )
    }

    fun getUser(): UserModel? {
        var user: UserModel? = null
        val cursor = db.query(
            User.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        if(cursor.moveToFirst()){
            val carId = cursor.getInt(cursor.getColumnIndexOrThrow(User.COLUMN_CAR))

            if (carId != null) {
                val carCursor = db.query(
                    Car.TABLE_NAME,
                    null,
                    "${Car.COLUMN_ID} = ?",
                    arrayOf(carId.toString()),
                    null,
                    null,
                    null
                )

                if (carCursor.moveToFirst()) {
                    val car: CarModel = CarModel(
                        carCursor.getInt(carCursor.getColumnIndexOrThrow(Car.COLUMN_ID)),
                        carCursor.getString(carCursor.getColumnIndexOrThrow(Car.COLUMN_NAME)),
                        carCursor.getInt(carCursor.getColumnIndexOrThrow(Car.COLUMN_PRICE)),
                        carCursor.getInt(carCursor.getColumnIndexOrThrow(Car.COLUMN_BUYED)) > 0,
                        carCursor.getInt(carCursor.getColumnIndexOrThrow(Car.COLUMN_CHOOSED)) > 0
                    )

                    user = UserModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(User.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(User.COLUMN_USERNAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(User.COLUMN_MONEY)),
                        car,
                        cursor.getString(cursor.getColumnIndexOrThrow(User.COLUMN_PASSWORD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(User.COLUMN_TOKEN)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(User.COLUMN_SYNCED)) > 0,
                    )
                }

            }
        }
        cursor.close()
        return user
    }
}