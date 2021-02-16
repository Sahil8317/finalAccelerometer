package com.sahil.accelerometer

import android.content.Context
import android.content.SharedPreferences

class DeviceData {

    var TAG:String = "DeviceData"
    var nodeMcuDeviceName:String?=null
    var nodeMcuDeviceAddress:String?=null
    var nodeMcuDeviceUUID:String?=null
    var context:Context?=null
    private var userReference:SharedPreferences?=null


    constructor(context: Context){
        this.context = context
         userReference = context.getSharedPreferences("BTData",Context.MODE_PRIVATE)
    }

    fun getNodeMCUData(nodeMcuDeviceName:String,nodeMcuDeviceAddress:String,nodeMcuDeviceUUID:String){
        this.nodeMcuDeviceAddress = nodeMcuDeviceAddress
        this.nodeMcuDeviceName = nodeMcuDeviceName
        this.nodeMcuDeviceUUID = nodeMcuDeviceUUID
    }

    // Save Data to user Preference
    fun saveBTData(){
        val editor =  userReference!!.edit()
        editor.putString("nodeMcuDeviceAddress",nodeMcuDeviceAddress)
        editor.putString("nodeMcuDeviceName",nodeMcuDeviceName)
        editor.putString("nodeMcuDeviceUUID",nodeMcuDeviceUUID)
        editor.apply()
    }

    fun getDeviceData(){
         address = userReference!!.getString("nodeMcuDeviceAddress","emptyAddress")
         deviceName = userReference!!.getString("nodeMcuDeviceName","emptyName")
         deviceUUID = userReference!!.getString("nodeMcuDeviceUUID","emptyUUID")

    }

    fun isLoggedInFirstTime(){
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
        var deviceUUID:String?=null

    }





}
