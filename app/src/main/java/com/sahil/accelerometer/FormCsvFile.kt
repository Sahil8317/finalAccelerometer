package com.sahil.accelerometer

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.sahil.accelerometer.database.AccelerometerData
import com.sahil.accelerometer.database.DatabaseRepository
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.net.URI
import java.util.ArrayList

class FormCsvFile {
    private var repository: DatabaseRepository?=null
    private var context:Context
   private  var readingList : List<AccelerometerData>
    constructor(context: Context){
        this.context = context
        repository = DatabaseRepository(context)
        readingList  = repository!!.readingList
        getLastIdInDatabase()
        exportDatabaseToCsvFile()

    }


    private fun getLastIdInDatabase(){
        try {
            val lstRow = readingList[readingList.size-1]
            val lastID = lstRow.id
            lstId = lastID
        }catch (e1:IOException){
            Log.d("error",e1.toString())
        }
    }

   private fun generateCsvFile(context: Context): File? {
        val csvFile = File(context.filesDir,"RiderData.csv")  // name of the csv file
        csvFile.createNewFile()
        return if(csvFile.exists()){
            csvFile
        }else{
            null
        }
    }

   private fun exportDatabaseToCsvFile(){
        //TODO Implement function to convert into csv file
       try {
           val fileName = generateCsvFile(context)
           csvFile = fileName!!
           if(fileName!=null){
               csvWriter().open(fileName){
                   writeRow(listOf("[id]","[Ax]","[Ay]","[Az]","[Temp]","[Gx]","[Gy]","[Gz]","[TimeStamp]"))
                   readingList.forEach {
                       writeRow(listOf(it.id,it.Ax,it.Ay,it.Az,it.Temp,it.Gx,it.Gy,it.Gz,it.timeStamp))
                   }
               }

           }else{
               Log.d("not created","File not created")
               Toast.makeText(context,"Cannot export database",Toast.LENGTH_SHORT).show()
           }
       }catch (ex:Exception){
           Log.d("can't export","error in exporting")
       }

    }

    fun openCsvFile(){
            try {
                val uri = FileProvider.getUriForFile(context,"com.sahil.accelerometer.provider", csvFile)
                val intent = Intent(Intent.ACTION_VIEW)
                val type = context.contentResolver.getType(uri)   // getting type of file(csv in our case)
                intent.setDataAndType(uri,type)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)

            }catch (e:Exception){
               Log.d("Cannot open","failed")
                Toast.makeText(context,"Cannot open file",Toast.LENGTH_SHORT).show()
            }


    }


    companion object{
        var converted:Boolean = false
        var lstId:Int = 0
       lateinit var  csvFile:File
    }
}