package com.sahil.accelerometer

import android.content.Context
import android.util.Log
import com.sahil.accelerometer.database.AccelerometerData
import com.sahil.accelerometer.database.DatabaseRepository
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Conversion {
    private var repository:DatabaseRepository?=null
    private var context:Context?=null
    private var dataList = ArrayList<String>()
    private var floatDataList = ArrayList<Float>()

    constructor(context: Context,dataList:ArrayList<String>){
        this.dataList = dataList
        this.context = context
        //convertToCharacter()
    }
    /** function to convert data from character to Float**/
     fun insertDataInDatabase():Boolean{
        try {
            if(dataList.isNotEmpty() ){
                if(convertToFloat()){
                    Log.d("starting,","insertion started")
                        val tb = SimpleDateFormat("ddMMyyHHmmss")
                        val date = Date()
                        val accData = AccelerometerData(
                            0,
                            floatDataList[0],
                            floatDataList[1],
                            floatDataList[2],
                            floatDataList[3],
                            floatDataList[4],
                            floatDataList[5],
                            floatDataList[6],
                            tb.format(date),
                        )
                        repository = DatabaseRepository(context!!)
                        repository!!.addData(accData) // inserting data in database
                        Log.d("done","insertion done")
                       // Toast.makeText(context!!,"done inserting",Toast.LENGTH_SHORT).show()
                        dataList.clear()
                        floatDataList.clear()
                        return true
                }else{
                    return false
                }

            }else{
                return false
            }

        }catch (e1:IOException){
            Log.d("error",e1.toString())
            return false
        }


    }


    private fun convertToFloat():Boolean{
        if(dataList.isNotEmpty()){
            for(data in dataList){
             val floatValue =  getValue(data)
                floatDataList.add(floatValue)
            }
        }
        //dataList.clear()
        return floatDataList.isNotEmpty()    // indicating that all values are converted
    }
    private fun getValue(d:String):Float{
        val errorVale = 1.0
        return try{
            var b = d.split("-")
            if(b[1] == ""){
                b = d.split("--")
            }
            b[1].toFloat()
        }catch(ex:IOException){
            Log.d("Error",ex.toString())
            errorVale.toFloat()
        }

    }

}