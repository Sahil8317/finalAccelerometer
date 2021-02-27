package com.sahil.accelerometer.database

import android.content.Context

class databaseRepository(context:Context) {
   private var roomDAO:AccelerometerDAO?=null

    init {
         roomDAO = AccelerometerDatabase.getDatabase(context).AccelerometerDAO()
    }

  private fun addData(data:AccelerometerData){
      roomDAO!!.addData(data)
  }
}