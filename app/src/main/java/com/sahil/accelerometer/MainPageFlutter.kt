package com.sahil.accelerometer

import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.SplashScreen
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.lang.Exception
import java.util.concurrent.TimeUnit

class MainPageFlutter : FlutterActivity() {

    private val methodChannel = "com.sahil.accelerometer"
    private var number:String?=null
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var manualVerificationID:String
    var  isVerificationSuccessful = false


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        startActivity(withCachedEngine("firstEngine").build(this))
    }

     /** from flutter to android**/
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger,methodChannel).setMethodCallHandler{
                call, result ->
            when(call.method){
                "startSearchAndConnect"->{
                    print("okay")
                    val intent = Intent(this,SearchAndConnectActivity::class.java)
                    startActivity(intent)
                    Log.d("start","activity started")
                    result.success("successful")
                }
                "formCSVFile"->{
                    val csvFile = FormCsvFile(this)
                    csvFile.openCsvFile()
                }
                "startLogin"->{
                    number = call.arguments.toString()
                    val options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber("+91"+number!!).setTimeout(60L,TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                auth.signInWithCredential(credential).addOnCompleteListener {
                                    if(it.isSuccessful){
                                        val appUser = it.result.user
                                        print(appUser)
                                        Toast.makeText(context,"verification successful",Toast.LENGTH_SHORT).show()
                                        val uid = appUser!!.uid
                                        println(uid)
                                        isVerificationSuccessful = true
                                        UserData.uid = uid
                                        println("ssssssssssssssssss0")
                                        println(isVerificationSuccessful)
                                        if(isVerificationSuccessful){
                                            val firebaseDatabase = FirebaseDatabase.getInstance()
                                            val ref = firebaseDatabase.getReference("Users")
                                            ref.orderByChild("PhoneNumber").equalTo(number).addListenerForSingleValueEvent(object:ValueEventListener{
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    if(snapshot.exists()){
                                                        val screenName = "/"
                                                        result.success(screenName)  // go to main screen user already exists
                                                    }else{
                                                        val screenName = "/userDetailScreen"
                                                        println(screenName)
                                                        result.success(screenName)  // change flutter screen
                                                    }
                                                }
                                                override fun onCancelled(error: DatabaseError) {

                                                }

                                            })

                                        }

                                    }else{
                                        if(it.exception is FirebaseAuthInvalidCredentialsException){
                                            Toast.makeText(context,"Invalid verification code ",Toast.LENGTH_SHORT).show()
                                            result.success("failed 1")  // invalid verification code
                                            // isVerificationSuccessful = false

                                        }
                                    }
                                }
                            }

                            override fun onVerificationFailed(p0: FirebaseException) {
                                Toast.makeText(context,"failed to verify phone number",Toast.LENGTH_SHORT).show()
                                result.success("failed 2")   // failed to send code try after some time
                            }

                            override fun onCodeSent(verificationID: String, p1: PhoneAuthProvider.ForceResendingToken) {
                                super.onCodeSent(verificationID, p1)
                                Toast.makeText(context,"code sent",Toast.LENGTH_LONG).show()
                                manualVerificationID = verificationID
                            }

                        }).build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                        println("ssss")
                }

                "verifyOTP"->{
                    try {
                        val otp = call.arguments
                        val credential = PhoneAuthProvider.getCredential(manualVerificationID,otp.toString())
                        println(credential)
                        println("vvvvvvvvvvvvvvvvv")
                        auth.signInWithCredential(credential).addOnCompleteListener {
                            if(it.isSuccessful){
                                val appUser = it.result.user
                                print(appUser)
                                Toast.makeText(context,"successful verification",Toast.LENGTH_SHORT).show()
                                val uid = appUser!!.uid
                                println(uid)
                                isVerificationSuccessful = true
                                UserData.uid = uid
                                // UserData.currentUser = appUser
                                println("ssssssssssssssssss")
                                println(isVerificationSuccessful)
                                if(isVerificationSuccessful){
                                    val firebaseDatabase = FirebaseDatabase.getInstance()
                                    val ref = firebaseDatabase.getReference("Users")
                                    ref.orderByChild("PhoneNumber").equalTo(appUser.phoneNumber).addListenerForSingleValueEvent(object:ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if(snapshot.exists()){
                                                val screenName = "/"
                                                println(screenName)
                                                result.success(screenName)  // go to main screen

                                            }else{
                                                val screenName = "/userDetailScreen"
                                                println(screenName)
                                                result.success(screenName)  // change flutter screen go to details screen
                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {

                                        }

                                    })
                                }

                            }else{
                                if(it.exception is FirebaseAuthInvalidCredentialsException){
                                    Toast.makeText(context,"Invalid verification code",Toast.LENGTH_SHORT).show()
                                    result.success("invalid code")
                                    // isVerificationSuccessful = false
                                }
                            }
                        }
                    }catch (e:Exception){
                        println(e)
                    }


                }
                "SaveAndContinue"->{
                    try {
                        val data = call.arguments.toString()
                        val realData = data.split(",")
                        val firebaseDatabase = FirebaseDatabase.getInstance()
                        val databaseReference = firebaseDatabase.getReference("Users").child(UserData.uid)
                        val userName = realData[0]
                        val userEmailID = realData[1]
                        val userAge = realData[2]
                        val userVehicleNumber = realData[3]
                        val userData = UserData(context)
                        UserData.currentUser = FirebaseAuth.getInstance().currentUser!!
                        UserData.userName = userName
                        UserData.emailID = userEmailID
                        val number = UserData.currentUser.phoneNumber
                        UserData.phoneNumber = number!!
                        UserData.userAge = userAge
                        UserData.userVehicleNumber = userVehicleNumber
                        databaseReference.child("Name").setValue(userName)
                        databaseReference.child("EmailID").setValue(userEmailID)
                        databaseReference.child("Age").setValue(userAge)
                        databaseReference.child("PhoneNumber").setValue(UserData.phoneNumber)
                        databaseReference.child("Vehicle Number").setValue(userVehicleNumber).addOnCompleteListener{
                            if(it.isSuccessful){
                                println("done saving data")
                                val screenName = "/"
                                userData.saveUserDataToSharedPreference()
                                userData.isLogInFirstTime()
                                result.success(screenName)
                            }
                        }
                    }catch (e:Exception){
                        println(e)
                        Toast.makeText(context,"Failed to save data",Toast.LENGTH_SHORT).show()
                    }
                    /** add code for failing to save data**/

                }
            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun provideSplashScreen(): SplashScreen? {
       // val flutterLogo :ImageView?=null
//        flutterLogo!!.setImageDrawable(resources.getDrawable(R.drawable.newflutter,null))
//        val splash = resources.getDrawable(R.drawable.splash_background,null)
//        val loadingDrawable = ProgressBar(context)
        return CustomSplashScreen()
    }
}