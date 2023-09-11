package com.test.dontforgetproject.DAO

data class TodoClass(
    var todoIdx: Long,
    var todoContent: String,
    var todoIsChecked: Long,
    var todoCategoryIdx: Long,
    var todoCategoryName: String,
    var todoDate: String,
    var todoAlertTime: String,
    var todoLocationLatitude: String,
    var todoLocationLongitude: String,
    var todoOwnerIdx: Long,
    var todoOwnerName: String
)