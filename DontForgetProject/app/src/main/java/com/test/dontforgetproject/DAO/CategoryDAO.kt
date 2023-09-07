package com.test.dontforgetproject.DAO

data class categoryInfo(
    var categoryIdx:Long,
    var categoryName:String,
    var categoryColor:String,
    var categoryJoinUserIdxList:ArrayList<String>,
    var categoryJoinUserNameList:ArrayList<String>,
    var categoryOwnerIdx:Long,
    var categoryOwnerName:String,
    // 0: 개인, 1: 단체
    var categoryIsPublic:Long


)