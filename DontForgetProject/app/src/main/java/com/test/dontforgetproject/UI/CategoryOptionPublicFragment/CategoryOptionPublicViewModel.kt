package com.test.dontforgetproject.UI.CategoryOptionPublicFragment

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.Repository.CategoryRepository

class CategoryOptionPublicViewModel() : ViewModel() {
    var categoryName = MutableLiveData<String>()
    var categoryColor = MutableLiveData<Int>()
    var categoryOwner = MutableLiveData<String>()

    var joinUserIdxList = MutableLiveData<MutableList<Long>>()
    var joinUserNameList = MutableLiveData<MutableList<String>>()
    var peopleList = MutableLiveData<MutableList<Friend>>()

    init {
        joinUserIdxList.value = mutableListOf<Long>()
        joinUserNameList.value = mutableListOf<String>()
        peopleList.value = mutableListOf<Friend>()
    }

    fun reset() {
        categoryName.value = ""
        categoryColor.value = Color.WHITE
        categoryOwner.value = ""

        joinUserIdxList.value = mutableListOf<Long>()
        joinUserNameList.value = mutableListOf<String>()
        peopleList.value = mutableListOf<Friend>()
    }

    // 카테고리 정보 가져오기
    fun getCategoryInfo(categoryIdx: Long) {
        CategoryRepository.getCategoryByIdx(categoryIdx) {
            for (c1 in it.result.children) {
                val c1Name = c1.child("categoryName").value as String
                val c1Color = c1.child("categoryColor").value as Long
                val c1Owner = c1.child("categoryOwnerName").value as String
                val c1JoinUserIdxList = c1.child("categoryJoinUserIdxList").value as ArrayList<Long>
                val c1JoinUserNameList = c1.child("categoryJoinUserNameList").value as ArrayList<String>

                // 자기 자신을 제거
                c1JoinUserIdxList.removeAt(0)
                c1JoinUserNameList.removeAt(0)

                categoryName.value = c1Name
                categoryColor.value = c1Color.toInt()
                categoryOwner.value = c1Owner
                joinUserIdxList.value = c1JoinUserIdxList
                joinUserNameList.value = c1JoinUserNameList
            }
        }
    }

    // 참여인원 추가하기
    fun addPeople(idxList: MutableList<Long>, nameList: MutableList<String>) {
        var tempIdxList = mutableListOf<Long>()
        var tempNameList = mutableListOf<String>()

        tempIdxList.addAll(joinUserIdxList.value!!)
        tempNameList.addAll(joinUserNameList.value!!)

        for (idx in idxList) {
            tempIdxList.add(idx)
        }
        for (name in nameList) {
            tempNameList.add(name)
        }

        joinUserIdxList.value = tempIdxList
        joinUserNameList.value = tempNameList
    }

    // 참여인원 삭제하기
    fun removePeople(position: Int) {
        joinUserIdxList.value?.removeAt(position)
        joinUserNameList.value?.removeAt(position)
    }
}