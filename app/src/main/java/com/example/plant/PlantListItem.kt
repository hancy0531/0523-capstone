package com.example.plant

class PlantListItem {
    private var itemId: Int = 0
    private var userEmail: String = ""
    private var plantName: String = ""
    private var plantDate: String = ""
    private var plantPoint: String = ""
    private var plantLocation: String = ""
    private var plantCycle: String = ""
    private var plantHour: Int = 0
    private var plantMinute: Int = 0
    private var plantTemp: String = ""
    private var plantHumid: String = ""
    private var tempAlarm: Int = 0
    private var humidAlarm: Int = 0
    private var imageUrl: String = ""
    private var enrollTime: String = ""

    fun setEnrollTime(time: String) {
        enrollTime = time
    }

    fun getEnrollTime(): String {
        return enrollTime
    }
    fun setItemId(id: Int){
        itemId = id
    }
    fun getItemId(): Int{
        return itemId
    }

    fun setUserEmail(email: String) {
        userEmail = email
    }

    fun getUserEmail(): String {
        return userEmail
    }

    fun setPlantName(name: String) {
        plantName = name
    }

    fun getPlantName(): String {
        return plantName
    }

    fun setPlantDate(date: String) {
        plantDate = date
    }

    fun getPlantDate(): String {
        return plantDate
    }

    fun setPlantPoint(point: String) {
        plantPoint = point
    }

    fun getPlantPoint(): String {
        return plantPoint
    }

    fun setPlantLocation(location: String) {
        plantLocation = location
    }

    fun getPlantLocation(): String {
        return plantLocation
    }

    fun setPlantCycle(cycle: String) {
        plantCycle = cycle
    }

    fun getPlantCycle(): String {
        return plantCycle
    }

    fun setPlantHour(hour: Int) {
        plantHour = hour
    }

    fun getPlantHour(): Int {
        return plantHour
    }

    fun setPlantMinute(minute: Int) {
        plantMinute = minute
    }

    fun getPlantMinute(): Int {
        return plantMinute
    }

    fun setPlantTemp(temp: String) {
        plantTemp = temp
    }

    fun getPlantTemp(): String {
        return plantTemp
    }

    fun setPlantHumid(humid: String) {
        plantHumid = humid
    }

    fun getPlantHumid(): String {
        return plantHumid
    }

    fun setTempAlarm(alarm: Int) {
        tempAlarm = alarm
    }

    fun getTempAlarm(): Int {
        return tempAlarm
    }

    fun setHumidAlarm(alarm: Int) {
        humidAlarm = alarm
    }

    fun getHumidAlarm(): Int {
        return humidAlarm
    }
    fun setImageUrl(url: String) {
        imageUrl = url
    }
    fun getImageUrl(): String {
        return imageUrl
    }
}
/*
class PlantListItem {
    private var plantName = ""
    private var plantImgSrc: Int = R.drawable.img_2
    private var itemNum = 0
    fun setName(name:String){
        plantName = name
    }
    fun getName():String{
        return plantName
    }
    fun setImgSrc(resourceId: Int) {
        plantImgSrc = resourceId
    }
    fun setNum(itemNumber: Int){
        itemNum = itemNumber
    }
    fun getNum():Int{
        return itemNum
    }
    fun getImgSrc(): Int {
        return plantImgSrc
    }
}*/