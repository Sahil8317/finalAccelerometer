package com.sahil.accelerometer

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseUser

class UserData{
   private var context:Context?=null
    private var flutterPreference:SharedPreferences?=null

    constructor(context: Context){
        this.context = context
        flutterPreference = context.getSharedPreferences("FlutterSharedPreferences",Context.MODE_PRIVATE)
    }
    companion object{
        var isLoggedIn:Boolean = false
        lateinit var uid:String
        lateinit var userName:String
        lateinit var emailID:String
        lateinit var phoneNumber:String
        lateinit var userAge:String
        lateinit var userVehicleNumber:String
        lateinit var currentUser:FirebaseUser
    }


    fun isLogInFirstTime(){
        val tempUserID = flutterPreference!!.getString("userID","empty")
        if(tempUserID!="empty"){
            val editor = flutterPreference!!.edit()
            editor.putString("flutter.loggedInfo","LoggedIN")  // for flutter code use
            editor.apply()
        }
        isLoggedIn = tempUserID!="empty"

    }

    fun saveUserDataToSharedPreference(){
        val editor =flutterPreference!!.edit()
        editor.putString("UserName",userName)
        editor.putString("userID", uid)
        editor.putString("emailID",emailID)
        editor.putString("Age", userAge)
        editor.putString("phoneNumber", phoneNumber)
        editor.putString("vehicleNumber", userVehicleNumber)
        editor.apply()
    }

}
