package com.sahil.accelerometer

import android.app.ActionBar
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import io.flutter.embedding.android.DrawableSplashScreen
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.SplashScreen
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlin.time.seconds

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
       // val flutterLogo :ImageView?=null
//        flutterLogo!!.setImageDrawable(resources.getDrawable(R.drawable.newflutter,null))
        val splash = resources.getDrawable(R.drawable.splash_background,null)
        val loadingDrawable = ProgressBar(context)
        return CustomSplashScreen()
    }
}