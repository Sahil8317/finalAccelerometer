package com.sahil.accelerometer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AccelerometerData::class],version = 1,exportSchema = true)

    abstract class AccelerometerDatabase:RoomDatabase() {

        abstract fun AccelerometerDAO():AccelerometerDAO

        companion object{
            @Volatile
            private var INSTANCE :AccelerometerDatabase?=null

            fun getDatabase(context: Context):AccelerometerDatabase{
                val checkInstance = INSTANCE
                if(checkInstance!=null){
                    return  checkInstance
                }
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AccelerometerDatabase::class.java,
                    "accelerometer_database"
                ).build()
                INSTANCE=instance
                return instance
            }
        }

    }
