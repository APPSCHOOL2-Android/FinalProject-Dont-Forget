package com.test.dontforgetproject.DAO

data class UserInfo(
    var userIdx:Long,
    var userName:String,
    var userEmail:String,
    var userImage:String,
    var userProduce:String,
    var userLoginType:Long,
    var userLogin:Long,
    var userCode:String,
    var userId:Long,
    var userPw:String,
    var userFriendList:ArrayList<String>,
    var userFriendNameList:ArrayList<String>

)