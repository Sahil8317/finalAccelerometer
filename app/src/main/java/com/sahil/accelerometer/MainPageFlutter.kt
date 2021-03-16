package com.sahil.accelerometer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.annotation.RequiresApi
import io.flutter.embedding.android.DrawableSplashScreen
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.SplashScreen
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainPageFlutter : FlutterActivity() {

    private val methodChannel = "com.sahil.accelerometer"

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

            }

        }
    }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun provideSplashScreen(): SplashScreen? {
        val splash = resources.getDrawable(R.drawable.newflutter,null)
        return DrawableSplashScreen(splash)
    }
}