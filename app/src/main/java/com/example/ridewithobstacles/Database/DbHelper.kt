package com.example.ridewithobstacles.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object {
        const val DATABASE_NAME = "MyApp.db"
        const val DATABASE_VERSION = 1

        object User {
            const val TABLE_NAME = "users"
            const val COLUMN_ID = "id"
            const val COLUMN_USERNAME = "username"
            const val COLUMN_MONEY = "money"
            const val COLUMN_CAR = "car"
            const val COLUMN_PASSWORD = "password"
            const val COLUMN_TOKEN = "token"
            const val COLUMN_SYNCED = "synced"

            val CREATE_TABLE = """
                CREATE TABLE $TABLE_NAME (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_USERNAME TEXT NOT NULL,
                    $COLUMN_MONEY INTEGER DEFAULT 0,
                    $COLUMN_CAR INTEGER,
                    $COLUMN_PASSWORD TEXT NOT NULL,
                    $COLUMN_TOKEN TEXT NOT NULL,
                    $COLUMN_SYNCED INTEGER DEFAULT 0,
                    FOREIGN KEY ($COLUMN_CAR) REFERENCES ${Car.TABLE_NAME}(${Car.COLUMN_ID})
                );
            """.trimIndent()
        }


        object Ride {
            const val TABLE_NAME = "rides"
            const val COLUMN_ID = "id"
            const val COLUMN_USER_ID = "user_id"
            const val COLUMN_SCORE = "score"
            const val COLUMN_MONEY = "money"
            const val COLUMN_SYNCED = "synced"

            val CREATE_TABLE = """
                CREATE TABLE $TABLE_NAME (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_USER_ID INTEGER NOT NULL,
                    $COLUMN_SCORE INTEGER NOT NULL,
                    $COLUMN_MONEY INTEGER DEFAULT 0,
                    $COLUMN_SYNCED BOOLEAN DEFAULT 0,
                    FOREIGN KEY ($COLUMN_USER_ID) REFERENCES ${User.TABLE_NAME}($COLUMN_ID)
                );
            """.trimIndent()
        }

        object Car {
            const val TABLE_NAME = "cars"
            const val COLUMN_ID = "id"
            const val COLUMN_NAME = "name"
            const val COLUMN_PRICE = "price"
            const val COLUMN_BUYED = "buyed"
            const val COLUMN_CHOOSED = "choosed"

            val CREATE_TABLE = """
                CREATE TABLE $TABLE_NAME (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_NAME TEXT NOT NULL,
                    $COLUMN_PRICE INTEGER DEFAULT 0,
                    $COLUMN_BUYED BOOLEAN DEFAULT 0,
                    $COLUMN_CHOOSED BOOLEAN DEFAULT 0
                );
            """.trimIndent()
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(User.CREATE_TABLE)
        db.execSQL(Ride.CREATE_TABLE)
        db.execSQL(Car.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${User.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${Ride.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${Car.TABLE_NAME}")
        onCreate(db)
    }
}