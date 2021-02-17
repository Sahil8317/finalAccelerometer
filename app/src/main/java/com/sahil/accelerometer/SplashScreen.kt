package com.sahil.accelerometer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class SplashScreen : AppCompatActivity() {

    var TAG:String = "SplashScreen"
    private val btModuleName = "HC-06"
    private var bluetoothAdapterRequestCode = 123
    private var bAdapter : BluetoothAdapter?=null
   private val btPairedDeviceList = ArrayList<BluetoothDeviceData>()
    private val availableDevices = ArrayList<BluetoothDeviceData>()

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
        if(requestCode==bluetoothAdapterRequestCode && resultCode== RESULT_OK ){
            Toast.makeText(applicationContext,"Enabled",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(applicationContext,"Enable Bluetooth firstly",Toast.LENGTH_LONG).show()
        }
    }

    private fun searchForAvailableDevices(){
        try {
            if(DeviceData.isBTEnabled && bAdapter!!.isEnabled){
                if(bAdapter!!.isDiscovering){
                    bAdapter!!.cancelDiscovery()
                }
                val discover = bAdapter!!.startDiscovery()
                Log.d("Starting",discover.toString())
                val intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(btReceiver,intentFilter)
                //TODO Connect with the device named HC-06

                if(availableDevices.isNotEmpty()){
                    val moduleInfo:BluetoothDeviceData?=null
                    for(device in availableDevices){
                        if(device.deviceName==btModuleName){
                           moduleInfo!!.deviceName = device.deviceName
                            moduleInfo.deviceAddress=device.deviceAddress
                            break
                        }
                    }
                    if(moduleInfo==null){
                        Log.d("not found","Not Found in Paired Devices")
                        // Again Search for available devices
                        // in second stage add a alert dialogue box for again searching the device
                        searchForAvailableDevices()
                    }else{
                        //TODO connect with this device name (HC-06) ie module info implement same function
                        connectToModule(moduleInfo,"FromSearch")
                    }
                }
            }else{
                logMessage()
            }

        }catch (ex:Exception){
            println(ex)
        }
    }

    private fun connectToModule(moduleInfo: BluetoothDeviceData,tag:String){
        // TODO 1. Implement this Function
        try {
            if(tag == "FromSearch"){

            }else{

            }

        }catch (ex:Exception){
            Log.d("Exception",ex.toString())
            println(ex)
        }

    }
    private val btReceiver = object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action
            if(action==BluetoothDevice.ACTION_FOUND){
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                availableDevices.add(BluetoothDeviceData(device!!.name,device.address))
            }else{
                Log.d("unable","receiving failed")
            }
        }
    }

   private fun getPairedDevices(){
       if(DeviceData.isBTEnabled && bAdapter!!.isEnabled) {
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
        if(moduleInfo==null){
            Log.d("not found","Not Found in Paired Devices")
            searchForAvailableDevices()
        }else{
            //TODO connect with this device name (HC-06) ie module info
            connectToModule(moduleInfo,"FromPairedDevice")

        }
    }

    private fun logMessage(){
        Log.d("Unable","BT Disabled")
    }


}