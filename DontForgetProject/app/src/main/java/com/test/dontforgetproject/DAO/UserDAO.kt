package com.test.dontforgetproject.DAO

data class UserClass(
    var userIdx: Long,
    var userName: String,
    var userEmail: String,
    var userImage: String,
    var userIntroduce: String,
    var userId: String,
    var userFriendList: ArrayList<Friend>
)

data class Friend(
    val friendIdx : Long,
    val friendName : String,
    val friendEmail : String
)



