package com.example.plant

class UserItem {
    private var uid: String = ""
    private var uemail: String = ""
    private var joindate: String = ""
    private var ubirth: String = ""
    private var uimageurl: String = ""
    private var uintroduce: String = ""
    fun setUimageurl(imageUrl: String){
        uimageurl = imageUrl
    }
    fun getUimageurl(): String{
        return uimageurl
    }
    fun setUintroduce(introduce: String){
        uintroduce = introduce
    }
    fun getUintroduce(): String{
        return uintroduce
    }
    fun setUid(userId: String){
        uid = userId
    }
    fun getUid(): String{
        return uid
    }
    fun setUemail(userEmail:String){
        uemail = userEmail
    }
    fun getUemail(): String{
        return uemail
    }
    fun setJoinDate(userJoinDate: String){
        joindate = userJoinDate
    }
    fun getJoinDate(): String{
        return joindate
    }
    fun setUbirth(userBirth:String){
        ubirth = userBirth
    }
    fun getUbirth(): String{
        return ubirth
    }
}

