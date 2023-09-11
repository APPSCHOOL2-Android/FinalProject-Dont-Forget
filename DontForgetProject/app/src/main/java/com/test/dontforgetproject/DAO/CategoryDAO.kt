package com.test.dontforgetproject.DAO

data class CategoryClass(
    var categoryIdx: Long,
    var categoryName: String,
    var categoryColor: String,
    var categoryFontColor: String,
    var categoryJoinUserIdxList: ArrayList<String>?,
    var categoryJoinUserNameList: ArrayList<String>?,
    var categoryIsPublic: Long,
    var categoryOwnerIdx: Long,
    var categoryOwnerName: String
)