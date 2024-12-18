package com.example.ridewithobstacles.Database.Repositories.Implementations

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.ridewithobstacles.ApiService.DataModels.RecordDto
import com.example.ridewithobstacles.Database.DbHelper.Companion.Ride
import com.example.ridewithobstacles.Database.Models.RideModel

class RideRepository(private val db: SQLiteDatabase) {
    fun insert(ride: RideModel): Long {
        val values = ContentValues().apply {
            //put(Car.COLUMN_ID, car.id)
            put(Ride.COLUMN_USER_ID, ride.user_id)
            put(Ride.COLUMN_MONEY, ride.moneyCollected)
            put(Ride.COLUMN_SCORE, ride.score)
            put(Ride.COLUMN_SYNCED, ride.synchred)
        }
        val result = db.insert(Ride.TABLE_NAME, null, values)
        return result


    }

    fun update(ride: RideModel) {
        val values = ContentValues().apply {
            put(Ride.COLUMN_USER_ID, ride.user_id)
            put(Ride.COLUMN_MONEY, ride.moneyCollected)
            put(Ride.COLUMN_SCORE, ride.score)
            put(Ride.COLUMN_SYNCED, ride.synchred)
        }

        db.update(
            Ride.TABLE_NAME,
            values,
            "${Ride.COLUMN_ID} = ?",
            arrayOf(ride.id.toString())
        )
    }

    fun getAll(): List<RideModel> {
        val rides = mutableListOf<RideModel>()
        val cursor = db.query(
            Ride.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val ride = RideModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow(Ride.COLUMN_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Ride.COLUMN_SCORE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Ride.COLUMN_MONEY)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Ride.COLUMN_USER_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Ride.COLUMN_SYNCED)) > 0
                )
                rides.add(ride)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return rides
    }

    fun getById(id: Int): RideModel? {
        val cursor = db.query(
            Ride.TABLE_NAME,
            null,
            "${Ride.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var ride: RideModel? = null
        with(cursor) {
            if (cursor.moveToFirst()) {
                ride = RideModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow(Ride.COLUMN_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Ride.COLUMN_SCORE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Ride.COLUMN_MONEY)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Ride.COLUMN_USER_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Ride.COLUMN_SYNCED)) > 0
                )
            }
        }
        cursor.close()
        return ride
    }

    fun getRecord(): RecordDto{
        val query = "SELECT MAX(${Ride.COLUMN_SCORE}) AS max_score, MAX(${Ride.COLUMN_MONEY}) as max_money, AVG(${Ride.COLUMN_SCORE}) AS avg_score, AVG(${Ride.COLUMN_MONEY}) AS avg_money FROM ${Ride.TABLE_NAME}"

        val cursor = db.rawQuery(query, null)

        var maxScore: Int = 0
        var avgScore: Float = 0f
        var maxMoney: Int = 0
        var avgMoney: Float = 0f
        if (cursor.moveToFirst()) {
            val maxScoreIndex = cursor.getColumnIndex("max_score")
            val avgScoreIndex = cursor.getColumnIndex("avg_score")
            val maxMoneyIndex = cursor.getColumnIndex("max_money")
            val avgMoneyIndex = cursor.getColumnIndex("avg_money")
            maxScore = cursor.getInt(maxScoreIndex)
            avgScore = cursor.getFloat(avgScoreIndex)
            maxMoney = cursor.getInt(maxMoneyIndex)
            avgMoney = cursor.getFloat(avgMoneyIndex)
        }

        cursor.close()

        return RecordDto(
            maxScore,
            maxMoney,
            avgScore,
            avgMoney,
            ""
        )
    }

    fun getRidesCount(): Int{
        val query = "SELECT COUNT(*) FROM ${Ride.TABLE_NAME}"
        val cursor = db.rawQuery(query, null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0) // Получаем результат из первой колонки
        }

        return count
    }

    fun getMoneySum(): Int{
        val query = "SELECT SUM(${Ride.COLUMN_MONEY}) FROM ${Ride.TABLE_NAME}"
        val cursor = db.rawQuery(query, null)
        var sum = 0
        if (cursor.moveToFirst()) {
            sum = cursor.getInt(0) // Получаем результат из первой колонки
        }
        return sum
    }

    fun getScoreSum(): Int{
        val query = "SELECT SUM(${Ride.COLUMN_SCORE}) FROM ${Ride.TABLE_NAME}"
        val cursor = db.rawQuery(query, null)
        var sum = 0
        if (cursor.moveToFirst()) {
            sum = cursor.getInt(0) // Получаем результат из первой колонки
        }
        return sum
    }
}