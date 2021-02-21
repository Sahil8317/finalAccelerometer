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
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_search_and_connect.*

class SearchAndConnectActivity : AppCompatActivity() {

    private val btModuleName = "LG OM4560(E5)"
    private val btPairedDeviceList = ArrayList<BluetoothDeviceData>()
    private val availableDevices = ArrayList<BluetoothDeviceData>()
    private val availableBluetoothDevice = ArrayList<BluetoothDevice>()   // contains object of bluetooth device in built
    private var bAdapter : BluetoothAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_and_connect)
        search_animation.visibility = View.INVISIBLE
        connect_animation.visibility = View.INVISIBLE
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        val data = DeviceData(this)
        data.isLoggedInFirstTime()

        if(DeviceData.isAlreadyUser){
            // for old user
            //  getPairedDevices()
            oldUser()
        }else{
            print("get")
            newUser()
            // means the user is new
            // checkPermission()
            //  startDiscovery()
            // searchForAvailableDevices()
        }
    }
    private fun newUser(){
        Log.d("new","New User")
        startDiscovery()
        searchForAvailableDevices()

    }
    private fun oldUser(){
        Log.d("old","old User")
        getPairedDevices()

    }

    private fun startDiscovery(){
        if(bAdapter!!.isDiscovering){
            bAdapter!!.cancelDiscovery()
        }
        // todo start animation from here
        //search animation
        val discover = bAdapter!!.startDiscovery()
        if(discover) {
            search_animation.visibility = View.VISIBLE
            search_animation.playAnimation()
            Log.d("Starting", discover.toString())
        }else{
            Toast.makeText(applicationContext,discover.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchForAvailableDevices(){
        try {
            if(DeviceData.isBTEnabled && bAdapter!!.isEnabled){
                Toast.makeText(applicationContext,"searching Devices", Toast.LENGTH_SHORT).show()
                val intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                registerReceiver(btReceiver,intentFilter)
                //  println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
                //TODO Connect with the device named HC-06
            }else{
                Toast.makeText(applicationContext,DeviceData.isBTEnabled.toString(),Toast.LENGTH_SHORT).show()
                Toast.makeText(applicationContext,bAdapter!!.isEnabled.toString(),Toast.LENGTH_SHORT).show()

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
                // from paired devices

            }

        }catch (ex:Exception){
            Log.d("Exception",ex.toString())
            println(ex)
        }

    }
    private val btReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action
            println("Received #################################")
            when(action){
                BluetoothDevice.ACTION_FOUND->{
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    availableDevices.add(BluetoothDeviceData(device!!.name,device.address))
                    availableBluetoothDevice.add(device)
                    Toast.makeText(applicationContext,device.name.toString(), Toast.LENGTH_SHORT).show()
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED->{
                    //todo spot the animation and start connecting animation here
                    search_animation.visibility = View.INVISIBLE
                    connectToSearchedDevice()
                }

            }
        }
    }

    private fun connectToSearchedDevice() {
        try {
            if (availableBluetoothDevice.isNotEmpty()) {
                var moduleInfo: BluetoothDevice? = null
                for (device in availableBluetoothDevice) {
                  // showToast("searching")
                    if (device.name == btModuleName) {   // found node mcu
                       moduleInfo = device
                        break
                    }
                }
                if (moduleInfo == null) {
                    Log.d("not found", "Not Found in Paired Devices")
                    // Again Search for available devices
                    // in second stage add a alert dialogue box for again searching the device
                    Toast.makeText(applicationContext,"Module not found",Toast.LENGTH_SHORT).show()
                    startDiscovery()
                    searchForAvailableDevices()
                } else {
                    //TODO connect with this device name (HC-06) ie module info implement same function
                        pairSearchedDevice(moduleInfo)
                        //connectToModule(moduleInfo, "FromSearch")
                }
            }else{
                Log.d("empty","list is empty")
                startDiscovery()
                searchForAvailableDevices()
            }
        }catch (ex:Exception){
            Log.d("error",ex.toString())
        }
    }

    private fun pairSearchedDevice(btDevice : BluetoothDevice){
        try {
            connect_animation.visibility = View.VISIBLE
            connect_animation.playAnimation()
            btDevice::class.java.getMethod("createBond").invoke(btDevice)
            val intentFilter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            registerReceiver(btPairReceiver,intentFilter)
        }catch (ex:Exception){
            Log.d("error","Cannot create bond")
        }

    }
    private val btPairReceiver  = object:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            showToast("StartedPairing")
              when(intent!!.action){
                  BluetoothDevice.ACTION_BOND_STATE_CHANGED->{
                      val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,BluetoothDevice.ERROR)
                      val prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,BluetoothDevice.ERROR)
                      if(state==BluetoothDevice.BOND_BONDED && prevState==BluetoothDevice.BOND_BONDING){
                          Log.d("Paired","Paired with device")
                          connect_animation.visibility = View.INVISIBLE
                          // TODO go to the next function of connecting it and change thw ending position of animation
                          showToast("paired")
                        }
                      }
              }

        }

    }

    private fun getPairedDevices(){
        Toast.makeText(applicationContext,"getting paired devices", Toast.LENGTH_SHORT).show()
        if(DeviceData.isBTEnabled && bAdapter!!.isEnabled) {
            try {
                val btDevices = bAdapter!!.bondedDevices
                if (btDevices.size > 0) {
                    for (device in btDevices) {
                        btPairedDeviceList.add(BluetoothDeviceData(device.name, device.address))
                        Toast.makeText(applicationContext,device.name, Toast.LENGTH_SHORT).show()
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
        Toast.makeText(applicationContext,"Got paired Devices", Toast.LENGTH_LONG).show()
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
    private fun showToast(msg:String){
        Toast.makeText(applicationContext,msg,Toast.LENGTH_SHORT).show()
    }

}