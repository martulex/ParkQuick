package com.team12.parkquick.database

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

@Database(entities = [ParkingCard::class], version = 1)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun parkingCardDao(): ParkingCardDao
    companion object {
        private val DB_NAME = "parking_cards"
        @Volatile private var INSTANCE: AppRoomDatabase? = null
        fun getInstance(context: Context): AppRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabase::class.java,
                    DB_NAME
                ).build().also { INSTANCE = it }
            }
        }
    }
}


