package com.test.dontforgetproject.UI.CategoryAddPublicFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dontforgetproject.DAO.Friend

class CategoryAddPublicViewModel() : ViewModel() {
    var peopleList = MutableLiveData<MutableList<Friend>>()

    init {
        peopleList.value = mutableListOf<Friend>()
    }

    fun reset() {
        peopleList.value = mutableListOf<Friend>()
    }

    // 참여인원 추가하기
    fun addPeople(addPeopleList: MutableList<Friend>) {
        var tempList = mutableListOf<Friend>()
        tempList.addAll(peopleList.value!!)

        for (friend in addPeopleList) {
            tempList.add(friend)
        }

        peopleList.value = tempList
    }

    // 참여인원 삭제하기
    fun removePeople(position: Int) {
        peopleList.value?.removeAt(position)
    }
}