package com.sahil.accelerometer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import io.flutter.embedding.engine.FlutterEngine
import kotlinx.android.synthetic.main.activity_search_and_connect.*
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class SearchAndConnectActivity : AppCompatActivity() {

    private val btModuleName = "HC05"
    private var isFirstSearch  = true
    private var bluetoothDevice:BluetoothDevice?=null
    private val btPairedDeviceList = ArrayList<BluetoothDevice>()
    private val availableDevices = ArrayList<BluetoothDeviceData>()
    private val availableBluetoothDevice = ArrayList<BluetoothDevice>()   // contains object of bluetooth device in built
    private var bAdapter : BluetoothAdapter?=null
    private var data:DeviceData?=null
    private var btSocket:BluetoothSocket?=null
    private var isStreamOpen = false
    private var btInputStream: InputStream?=null
    private var writeData = "B"
    private var isInsertedToDatabase = false
    private var dataList = ArrayList<String>()
    private lateinit var secondFlutterEngine :FlutterEngine


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_and_connect)
        supportActionBar!!.hide()
         data = DeviceData(applicationContext)
        data!!.isLoggedInFirstTime()
        bikeAnimation.visibility = View.INVISIBLE
        search_animation.visibility = View.INVISIBLE
        connect_animation.visibility = View.INVISIBLE
        bAdapter = BluetoothAdapter.getDefaultAdapter()

        if(DeviceData.isAlreadyUser){
            println("@@@@@@@@@@@@@@@@@@@@@@@@@@@")
            // for old user
            //  getPairedDevices()
            oldUser()
        }else{
            newUser()
            // means the user is new
            // checkPermission()
            //  startDiscovery()
            // searchForAvailableDevices()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        btSocket!!.close()
    }
    private fun newUser(){
        Log.d("new","New User")
        startDiscovery()
        searchForAvailableDevices()

    }
    private fun oldUser(){
        Log.d("old","old User")
        connect_animation.visibility = View.VISIBLE
        connect_animation.playAnimation()
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

            }else{
                Toast.makeText(applicationContext,DeviceData.isBTEnabled.toString(),Toast.LENGTH_SHORT).show()
                Toast.makeText(applicationContext,bAdapter!!.isEnabled.toString(),Toast.LENGTH_SHORT).show()
                logMessage()
            }
        }catch (ex:Exception){
            println(ex)
        }
    }

    private val btReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                val action = intent!!.action
                println("Received #################################")
                when (action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        availableDevices.add(BluetoothDeviceData(device!!.name, device.address))
                        availableBluetoothDevice.add(device)
                        Toast.makeText(
                            applicationContext,
                            device.name.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        //todo spot the animation and start connecting animation here
                        search_animation.visibility = View.INVISIBLE
                        connectToSearchedDevice()
                    }

                }
            }catch(ex:Exception){
                Log.d("Search er",ex.toString())
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
                        bluetoothDevice = device
                        break
                    }
                }
                if (moduleInfo == null) {
                    Log.d("not found", "Not Found in Paired Devices")
                    // Again Search for available devices
                    // in second stage add a alert dialogue box for again searching the device
                    Toast.makeText(applicationContext,"Module not found",Toast.LENGTH_SHORT).show()
                    if(isFirstSearch) {
                        getPairedDevices()
                        isFirstSearch = false
                    }
                    startDiscovery()
                    searchForAvailableDevices()
                } else {
                    //TODO connect with this device name (HC-06) ie module info implement same function
                       if(isAlreadyPaired(moduleInfo)){

                           connectToModule(moduleInfo)

                       }else{
                           pairSearchedDevice(moduleInfo)
                       }
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

    private fun isAlreadyPaired(moduleInfo:BluetoothDevice):Boolean{
        val state = moduleInfo.bondState
        println(state)
        if(state==BluetoothDevice.BOND_BONDED){
            if(!DeviceData.isAlreadyUser){
                data!!.saveNodeMCUData(moduleInfo.name,moduleInfo.address)
                data!!.saveBTData()
            }
        }
        return state == BluetoothDevice.BOND_BONDED

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
                          showToast("paired")
                          /** saving data in preference**/
                          val device = DeviceData(applicationContext)
                          device.saveNodeMCUData(bluetoothDevice!!.name,bluetoothDevice!!.address)
                          device.saveBTData()
                          //calling final function for connecting module
                          connectToModule(bluetoothDevice!!)
                        }
                      }
              }

        }

    }

    private fun connectToModule(moduleInfo: BluetoothDevice){
        try {
            try {
                /** UUID from android bluetooth app**/
                btSocket = moduleInfo.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
            }catch (ec:Exception){
                Log.d("Socket ex",ec.toString())
            }
            try{
                btSocket!!.connect()
            }catch (e1:IOException){
                e1.printStackTrace()
                Log.d("socket ex","cannot connect to socket")
                try{
                    btSocket!!.close()
                    Log.d("socket","socket closed")
                }catch(e3:IOException){
                    Log.d("Socket ex","Socket cannot be closed")
                }
            }
            if(btSocket!!.isConnected){
                showToast("Socket is connected to the module")
                connect_animation.visibility = View.INVISIBLE
                startThread()

            }else{
                showToast("Error in connecting")
            }
        }catch (ex:Exception){
            Log.d("Exception",ex.toString())
            println(ex)
        }
    }

    private fun startThread(){
        Thread(Runnable {
                if(btSocket!!.isConnected){
                    ConnectThread(btSocket!!).start()
                    Log.d("started","Thread Started")
                }
        }).start()
    }

    private fun getPairedDevices(){
        Toast.makeText(applicationContext,"getting paired devices", Toast.LENGTH_SHORT).show()
        if(DeviceData.isBTEnabled && bAdapter!!.isEnabled) {
            connect_animation.visibility = View.VISIBLE
            connect_animation.playAnimation()
            try {
                val btDevices = bAdapter!!.bondedDevices
                if (btDevices.size > 0) {
                    for (device in btDevices) {
                        btPairedDeviceList.add(device)
                        Toast.makeText(applicationContext,device.name, Toast.LENGTH_SHORT).show()
                    }
                    connectToPairedDevice()
                } else {
                    // no paired devices available then do normal scanning of all the BTDevices
                    Toast.makeText(applicationContext, "No Paired Device Found", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("noFound", "Searching for all devices nearby")
                    startDiscovery()
                    searchForAvailableDevices()
                }
            }catch (ex:Exception){
                Log.d("exception",ex.toString())
            }

        }else{
            showToast("Enable Bluetooth")
            logMessage()
        }

    }

    private fun connectToPairedDevice(){
        var moduleInfo:BluetoothDevice?=null
        Toast.makeText(applicationContext,"Got paired Devices", Toast.LENGTH_LONG).show()
        for(device in btPairedDeviceList){
            if(device.name==btModuleName){
               moduleInfo = device
                break
            }
        }
        if(moduleInfo==null){
            Log.d("not found","Not Found in Paired Devices")
            connect_animation.visibility=View.INVISIBLE
            startDiscovery()
            searchForAvailableDevices()
        }else{
            //TODO connect with this device name (HC-06) ie module info
            connectToModule(moduleInfo)

        }
    }

    private fun logMessage(){
        Log.d("Unable","BT Disabled")
    }
    private fun showToast(msg:String){
        Toast.makeText(applicationContext,msg,Toast.LENGTH_SHORT).show()
    }

    inner class ConnectThread(mSocket:BluetoothSocket) : Thread(){
       private var mInputStream:InputStream?=null
        init {
            try {
              if(btSocket!=null && btSocket!!.isConnected){
                  btInputStream = btSocket!!.inputStream
                  isStreamOpen=true
                  mInputStream = btInputStream
                 // showToast("Got Input Stream")
                  Log.d("Stream","Got input stream")
              }
                else{
                    isStreamOpen=false
                  Log.d("socket unavailable","cannot connect")
              }
            }catch (e:IOException){
                e.printStackTrace()
                Log.d("stream ex","cannot get input stream")
            }
        }

        override fun run() {
            try {
                if(btSocket!!.isConnected && mInputStream!=null){
                    Log.d("connected","Starting to listen on incoming calls/Messages")

                    if(btSocket!!.isConnected && isStreamOpen) {
                        try {
                            runOnUiThread(object:Runnable{
                                override fun run() {
                                    bikeAnimation.visibility = View.VISIBLE
                                    bikeAnimation.playAnimation()
                                }
                            } )
                            do {
                                val outputStream = btSocket!!.outputStream
                                outputStream.write(writeData.toByteArray())    // writing  B in module
                                var finalString = ""

                                while(true) { // loop for each batch of data
                                    val data = btInputStream!!.read()
                                    if(data==40){                       // means first batch of data is completed and delay started
                                        break
                                    }
                                    if(data!=44) {                           // not equal to comma
                                        val characterData = data.toChar()
                                        finalString =finalString+ characterData.toString()
                                    }
                                    if(data==44){         // for separating  values
                                        dataList.add(finalString)    // adding each coordinate to list
                                        finalString = ""
                                    }
                                }
                                //println(finalString)
//                            for(d in datalist){
//                                println(d)
//                            }
//                            println(datalist.size)
                                isInsertedToDatabase = Conversion(applicationContext,dataList).insertDataInDatabase()
                            } while (isInsertedToDatabase)
                        }catch (ex:IOException){
                            println(ex)
                            Log.d("closed","Stream closed or socket closed handle it")
                            runOnUiThread(object:Runnable{
                                override fun run() {
                                    showDialogBox()
                                }
                            } )
                            Handler(Looper.getMainLooper()).post(object :Runnable{
                                override fun run() {
                                        bikeAnimation.visibility =
                                            View.INVISIBLE       // stopping animation
                                        Toast.makeText(
                                            applicationContext,
                                            "Module closed",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                }

                            })

                        }

                    }
                        Log.d("out","got out")

                }
                super.run()
            }catch (e1:IOException){
                Log.d("Error",e1.toString())
                //TODO Implement the function when module is disconnected handle the exception

            }

        }

    }
     fun showDialogBox(){
         Log.d("close","Module closed")
         val dialogBuilder = AlertDialog.Builder(this)
         dialogBuilder.setTitle("Module Closed")
         dialogBuilder.setMessage("is your ride ended?")

         dialogBuilder.setPositiveButton("Yes"){
                 _, _ ->
             Log.d("Yes","selected yes ride ended")
              FormCsvFile(applicationContext)
             val intent = Intent(applicationContext,MainPageFlutter::class.java)
             finish()
             startActivity(intent)
         }
         dialogBuilder.setNegativeButton("No"){
                 _,_->
             Log.d("No","Try to connect again to the module")
             getPairedDevices()
         }
         dialogBuilder.show()
     }


}