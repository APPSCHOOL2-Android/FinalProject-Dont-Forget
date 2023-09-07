package com.test.dontforgetproject.DAO

data class TodoInfo(
    var todoIdx:Long,
    var todoContent:String,
    var todoIsChecked:Boolean,
    var todoCategoryIdx:Long,
    var todoCategoryName:String,
    var todoDate:String,
    var todoAlertTime:String,
    var todoLocation:String,
    var todoOwnerIdx:Long,
    var todoOwnerName:String

)