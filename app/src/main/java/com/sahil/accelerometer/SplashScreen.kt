package com.sahil.accelerometer

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class SplashScreen : AppCompatActivity() {

    var TAG:String = "SplashScreen"
    private val btModuleName = "HC-06"
    private var bluetoothAdapterRequestCode = 123
    private var bAdapter : BluetoothAdapter?=null
    val btPairedDeviceList = ArrayList<BluetoothDeviceData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        checkBluetoothState()
        val data = DeviceData(this)
        data.isLoggedInFirstTime()
        if(DeviceData.isAlreadyUser){
            // for old user
            getPairedDevices()
        }else{
            // means the user is new
            searchForAvailableDevices()
        }
    }

   private fun checkBluetoothState(){
        if(bAdapter!!.isEnabled){
            Toast.makeText(applicationContext,"Bluetooth is Enable",Toast.LENGTH_SHORT).show()
            DeviceData.isBTEnabled= true
        }else{
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent,bluetoothAdapterRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==bluetoothAdapterRequestCode && resultCode== RESULT_OK && data!= null){
            Toast.makeText(applicationContext,"Enabled",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(applicationContext,"Enable Bluetooth firstly",Toast.LENGTH_LONG).show()
        }
    }

    private fun searchForAvailableDevices(){
        if(DeviceData.isBTEnabled && bAdapter!!.isEnabled){
            //ToDO  Implement function for searching all the BT Devices


        }else{
          logMessage()
        }
    }

   private fun getPairedDevices(){
       if(DeviceData.isBTEnabled && bAdapter!!.isEnabled) {
           //TODO Implement a function for getting all the paired devices
               try {
           val btDevices = bAdapter!!.bondedDevices
           if (btDevices.size > 0) {
               for (device in btDevices) {
                   btPairedDeviceList.add(BluetoothDeviceData(device.name, device.address))
               }
               connectToPairedDevice()
           } else {
               // no paired devices available then do normal scanning of all the BTDevices
               Toast.makeText(applicationContext, "No Paired Device Found", Toast.LENGTH_SHORT)
                   .show()
               Log.d("noFound", "Searching for all devices nearby")
               searchForAvailableDevices()
           }
       }catch (ex:Exception){
           Log.d("exception",ex.toString())
       }

       }else{
          logMessage()
       }

    }

    private fun connectToPairedDevice(){
        val moduleInfo:BluetoothDeviceData?=null
        for(device in btPairedDeviceList){
            if(device.deviceName==btModuleName){
                moduleInfo!!.deviceName = device.deviceName
                moduleInfo.deviceAddress = device.deviceAddress
                break
            }
        }
        //TODO connect with this device name (HC-06) ie module info
    }


    private fun logMessage(){
        Log.d("Unable","BT Disabled")
    }







}