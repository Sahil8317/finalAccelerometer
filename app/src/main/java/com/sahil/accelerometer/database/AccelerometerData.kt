package com.sahil.accelerometer.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rider_data")
data class AccelerometerData(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val Ax:Float,
    val Ay:Float,
    val Az:Float,
    val Temp:Float,
    val Gx:Float,
    val Gy:Float,
    val Gz:Float,
    val timeStamp:String

)