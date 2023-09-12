package com.test.dontforgetproject.DAO

data class CategoryClass(
    var categoryIdx: Long,
    var categoryName: String,
    var categoryColor: Long,
    var categoryFontColor: Long,
    var categoryJoinUserIdxList: ArrayList<Long>?,
    var categoryJoinUserNameList: ArrayList<String>?,
    var categoryIsPublic: Long,
    var categoryOwnerIdx: Long,
    var categoryOwnerName: String
)