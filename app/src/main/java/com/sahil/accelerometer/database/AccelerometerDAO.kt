package com.sahil.accelerometer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface AccelerometerDAO {

@Insert(onConflict = OnConflictStrategy.IGNORE)
 fun addData(data:AccelerometerData)

}