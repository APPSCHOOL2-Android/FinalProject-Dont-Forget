package com.test.dontforgetproject.DAO

data class UserClass(
    var userIdx: Long,
    var userName: String,
    var userEmail: String,
    var userImage: String,
    var userIntroduce: String,
    var userCode: String,
    var userId: String,
    var userFriendIdxList: ArrayList<Long>?,
    var userFriendNameList: ArrayList<String>?
)