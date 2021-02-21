package com.sahil.accelerometer

import android.content.Context
import android.content.SharedPreferences

class DeviceData {

  // private var TAG:String = "DeviceData"
   private var nodeMcuDeviceName:String?=null
    private var nodeMcuDeviceAddress:String?=null
    private var context:Context?=null
    private var userReference:SharedPreferences?=null


    constructor(context: Context){
        this.context = context
         userReference = context.getSharedPreferences("BTData",Context.MODE_PRIVATE)
    }

   private fun saveNodeMCUData(nodeMcuDeviceName:String,nodeMcuDeviceAddress:String,nodeMcuDeviceUUID:String){
        this.nodeMcuDeviceAddress = nodeMcuDeviceAddress
        this.nodeMcuDeviceName = nodeMcuDeviceName
    }

    // Save Data to user Preference
   private fun saveBTData(){
        val editor =  userReference!!.edit()
        editor.putString("nodeMcuDeviceAddress",nodeMcuDeviceAddress)
        editor.putString("nodeMcuDeviceName",nodeMcuDeviceName)
        editor.apply()
    }

   private fun getDeviceData(){
         address = userReference!!.getString("nodeMcuDeviceAddress","emptyAddress")
         deviceName = userReference!!.getString("nodeMcuDeviceName","emptyName")

    }

   private fun isLoggedInFirstTime(){
        val data = userReference!!.getString("nodeMcuDeviceAddress","empty")
        if(data.equals("empty")){
            isAlreadyUser=false
        }
    }


    companion object{
        var isAlreadyUser:Boolean=true
        var isBTEnabled:Boolean = false
        var address:String?=null
        var deviceName:String?=null

    }





}

