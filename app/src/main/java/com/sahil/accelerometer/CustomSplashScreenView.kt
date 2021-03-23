package com.sahil.accelerometer

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi

class CustomSplashScreenView: FrameLayout {

    private var rotateAngle:Float = 360.toFloat()
    private var flutterLogo:ImageView
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context):super(context){
        setBackgroundColor(Color.parseColor("#FFFFFF"))
         flutterLogo = ImageView(context)
        flutterLogo.setImageDrawable(resources.getDrawable(R.drawable.splash_background,context.theme))
         addView(flutterLogo,LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,
            Gravity.CENTER))
        animateFlutterLogo()
    }

    private fun animateFlutterLogo(){
        flutterLogo.animate().rotation(rotateAngle).setDuration(800).setInterpolator(
            LinearInterpolator()
        ).setListener(animationListener).start()
    }


   private val animationListener = object :Animator.AnimatorListener{
        override fun onAnimationRepeat(p0: Animator?) {
            Log.d("repeat","started Repeating")
        }

        override fun onAnimationEnd(p0: Animator?) {
            p0!!.removeAllListeners()
            rotateAngle += 360
            animateFlutterLogo()
        }

        override fun onAnimationCancel(p0: Animator?) {
            p0!!.removeAllListeners()
        }

        override fun onAnimationStart(p0: Animator?) {
          //  Log.d("animation","started")
            val s = p0
        }


    }
    fun fadeSplashScreen(){
        val fade = animate().alpha(0.0f).setDuration(400)
        fade.start()
    }
}