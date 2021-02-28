package com.sahil.accelerometer.database

import android.content.Context

class DatabaseRepository(context: Context) {
   private var roomDAO:AccelerometerDAO?=null

    init {
         roomDAO = AccelerometerDatabase.getDatabase(context).AccelerometerDAO()
    }

   fun addData(data:AccelerometerData){
      roomDAO!!.addData(data)
  }
}