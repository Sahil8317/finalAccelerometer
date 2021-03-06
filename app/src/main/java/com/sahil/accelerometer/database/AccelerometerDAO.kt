package com.sahil.accelerometer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AccelerometerDAO {

@Insert(onConflict = OnConflictStrategy.IGNORE)
 fun addData(data:AccelerometerData)

 @Query("SELECT * FROM rider_data ORDER BY id ASC")     // get all data in ascending order
 fun readAllReadings() : List<AccelerometerData>

}