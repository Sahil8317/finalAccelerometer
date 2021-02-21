package com.sahil.accelerometer

import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

class SplashScreen : AppCompatActivity() {

   // var TAG:String = "SplashScreen"
    private var bluetoothAdapterRequestCode = 123
    private var locationRequestCode = 245
    private var bAdapter : BluetoothAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(1)
        window.setFlags(1024,1024)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash_screen)
        bAdapter = BluetoothAdapter.getDefaultAdapter()
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
            DeviceData.isBTEnabled= true
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



}