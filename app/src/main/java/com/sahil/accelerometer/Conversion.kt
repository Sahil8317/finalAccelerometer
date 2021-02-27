package com.sahil.accelerometer

import android.util.Log
import java.util.*

class Conversion {
    private var currentTimeStamp: Date?=null
    private var dataByteArray:ByteArray?=null

    constructor(currentTimeStamp:Date,dataByteArray: ByteArray){
        this.currentTimeStamp = currentTimeStamp
        this.dataByteArray = dataByteArray
        convertToCharacter()
    }
    /** function to convert data from ASCII to character**/

    private fun convertToCharacter(){
        if(dataByteArray!=null){
            var result:String = ""
            for(byte in dataByteArray!!){
                if(byte.toInt()!=44) {       // to avoid comma
                    val convertByte = byte.toChar()
                   // val floatValue = convertByte.toFloat()
                    result += convertByte.toString()
                }
            }
            isConverted=true
            Log.d("Converted",result)

        }else{
            isConverted=false
            Log.d("Unable","cannot convert array is null")
        }
    }

    companion object{
        var isConverted:Boolean = false

    }
}