package com.sahil.accelerometer.database

import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface AccelerometerDAO {

@Insert(onConflict = OnConflictStrategy.IGNORE)
 fun addData(data:AccelerometerData)

}