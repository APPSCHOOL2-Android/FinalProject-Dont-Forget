package com.test.dontforgetproject.DAO

data class TodoClass(
    var todoIdx: Int,
    var todoContent: String,
    var todoIsChecked: Int,
    var todoCategoryIdx: Int,
    var todoCategoryName: String,
    var todoDate: String,
    var todoAlertTime: String,
    var todoLocationLatitude: String,
    var todoLocationLongitude: String,
    var todoOwnerIdx: Int,
    var todoOwnerName: String
)