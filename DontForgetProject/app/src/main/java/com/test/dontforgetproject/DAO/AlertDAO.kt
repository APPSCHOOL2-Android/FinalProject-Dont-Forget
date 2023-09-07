package com.test.dontforgetproject.DAO

data class alertInfo(
    var alertIdx:Long,
    var alertContent:String,
    var alertReceiverIdx:Long,
    // 0: JoinFriend, 1: Time, 2: Date, 3: Location
    var alertType:Long

)