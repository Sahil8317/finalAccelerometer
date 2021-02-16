package com.sahil.accelerometer

class BluetoothDeviceData {
    var deviceName:String?=null
    var deviceAddress:String?=null

    constructor(deviceName:String,deviceAddress:String){
        this.deviceAddress = deviceAddress
        this.deviceName = deviceName
    }
}