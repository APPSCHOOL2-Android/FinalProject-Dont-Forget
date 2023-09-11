package com.test.dontforgetproject.DAO

data class AlertClass(
    var alertIdx : Long,
    var alertContent : String,
    var alertReceiverIdx : Long,
    val alertType : Long
)