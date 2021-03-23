package com.sahil.accelerometer

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi

class CustomSplashScreen :  io.flutter.embedding.android.SplashScreen {

    lateinit var  customView:CustomSplashScreenView
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun createSplashView(context: Context, savedInstanceState: Bundle?): View? {
        customView = CustomSplashScreenView((context))
        return customView
    }

    override fun transitionToFlutter(onTransitionComplete: Runnable) {
        customView.fadeSplashScreen()
  }

}
