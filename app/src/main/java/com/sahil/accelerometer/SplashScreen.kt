package com.sahil.accelerometer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

class SplashScreen : AppCompatActivity() {

    var TAG:String = "SplashScreen"
    private val btModuleName = "HC-06"
    private var bluetoothAdapterRequestCode = 123
    private var locationRequestCode = 245
    private var bAdapter : BluetoothAdapter?=null
   private val btPairedDeviceList = ArrayList<BluetoothDeviceData>()
    private val availableDevices = ArrayList<BluetoothDeviceData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(1)
        window.setFlags(1024,1024)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash_screen)
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        checkPermission()

    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    override fun onRestart() {
        super.onRestart()
        checkPermission()
    }

   private fun checkBluetoothState(){
        if(bAdapter!!.isEnabled){
            Toast.makeText(applicationContext,"Bluetooth is Enable",Toast.LENGTH_SHORT).show()
            DeviceData.isBTEnabled= true
            val intent = Intent(this,GpsPermissionActivity::class.java)
            startActivity(intent)
        }else{
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent,bluetoothAdapterRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==bluetoothAdapterRequestCode && resultCode== RESULT_OK ){
            Toast.makeText(applicationContext,"Enabled",Toast.LENGTH_SHORT).show()
            val intent = Intent(this,GpsPermissionActivity::class.java)
            startActivity(intent)
        }else{
            Toast.makeText(applicationContext,"Enable Bluetooth firstly",Toast.LENGTH_LONG).show()
        }
    }
   private fun checkPermission() {
       if (Build.VERSION.SDK_INT >= 23) {
           if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
               && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
               requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),locationRequestCode)
               return
           }else{
               checkBluetoothState()
               Log.d("Granted","Location permission granted")
           }

       }else{
           checkBluetoothState()
           Log.d("Granted","Location permission granted")
       }
   }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==locationRequestCode && grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
            Log.d("Granted"," permission granted")
            checkBluetoothState()

        }else{
            checkPermission()
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun startDiscovery(){
        if(bAdapter!!.isDiscovering){
            bAdapter!!.cancelDiscovery()
        }
        val discover = bAdapter!!.startDiscovery()
        Log.d("Starting",discover.toString())
    }

    private fun searchForAvailableDevices(){
        try {
            if(DeviceData.isBTEnabled && bAdapter!!.isEnabled){
                Toast.makeText(applicationContext,"searching Devices",Toast.LENGTH_SHORT).show()
                val intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(btReceiver,intentFilter)
              //  println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
                //TODO Connect with the device named HC-06
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
            println("Received #################################")
            if(action==BluetoothDevice.ACTION_FOUND){
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                availableDevices.add(BluetoothDeviceData(device!!.name,device.address))
                Toast.makeText(applicationContext,device.name.toString(),Toast.LENGTH_SHORT).show()
            }else{
                Log.d("unable","receiving failed")
            }
        }

    }

    private fun connectToSearchedDevice() {
        try {
            if (availableDevices.isNotEmpty()) {
                val moduleInfo: BluetoothDeviceData? = null
                for (device in availableDevices) {
                    Toast.makeText(applicationContext, "searching", Toast.LENGTH_SHORT).show()
                    if (device.deviceName == btModuleName) {
                        moduleInfo!!.deviceName = device.deviceName
                        moduleInfo.deviceAddress = device.deviceAddress
                        break
                    }
                }
                if (moduleInfo == null) {
                    Log.d("not found", "Not Found in Paired Devices")
                    // Again Search for available devices
                    // in second stage add a alert dialogue box for again searching the device
                    searchForAvailableDevices()
                } else {
                    //TODO connect with this device name (HC-06) ie module info implement same function
                    connectToModule(moduleInfo, "FromSearch")
                }
            }
        }catch (ex:Exception){
            Log.d("error",ex.toString())
        }
    }

   private fun getPairedDevices(){
       Toast.makeText(applicationContext,"getting paired devices",Toast.LENGTH_SHORT).show()
       if(DeviceData.isBTEnabled && bAdapter!!.isEnabled) {
           try {
           val btDevices = bAdapter!!.bondedDevices
           if (btDevices.size > 0) {
               for (device in btDevices) {
                   btPairedDeviceList.add(BluetoothDeviceData(device.name, device.address))
                   Toast.makeText(applicationContext,device.name,Toast.LENGTH_SHORT).show()
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
        Toast.makeText(applicationContext,"Got paired Devices",Toast.LENGTH_LONG).show()
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