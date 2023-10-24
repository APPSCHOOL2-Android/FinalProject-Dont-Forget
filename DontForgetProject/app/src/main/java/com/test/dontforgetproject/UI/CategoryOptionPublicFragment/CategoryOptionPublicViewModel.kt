package com.test.dontforgetproject.UI.CategoryOptionPublicFragment

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.DAO.TodoClass
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.Repository.TodoRepository

class CategoryOptionPublicViewModel() : ViewModel() {
    var categoryName = MutableLiveData<String>()
    var categoryColor = MutableLiveData<Int>()
    var categoryOwner = MutableLiveData<String>()

    var joinUserIdxList = MutableLiveData<MutableList<Long>>()
    var joinUserNameList = MutableLiveData<MutableList<String>>()
    var peopleList = MutableLiveData<MutableList<Friend>>()

    var todoList = MutableLiveData<MutableList<TodoClass>>()

    init {
        joinUserIdxList.value = mutableListOf<Long>()
        joinUserNameList.value = mutableListOf<String>()
        peopleList.value = mutableListOf<Friend>()

        todoList.value = mutableListOf<TodoClass>()
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


    // 해당 카테고리의 모든 할일 가져오기
    fun getTodoByCategory(categoryIdx: Long) {

        val tempTodoList = mutableListOf<TodoClass>()

        TodoRepository.getTodoInfoByCategory(categoryIdx) {
            for (c1 in it.result.children) {
                val todoIdx = c1.child("todoIdx").value as Long
                val todoContent = c1.child("todoContent").value as String
                val todoIsChecked = c1.child("todoIsChecked").value as Long
                val todoCategoryIdx = c1.child("todoCategoryIdx").value as Long
                val todoCategoryName = c1.child("todoCategoryName").value as String
                val todoFontColor = c1.child("todoFontColor").value as Long
                val todoBackgroundColor = c1.child("todoBackgroundColor").value as Long
                val todoDate = c1.child("todoDate").value as String
                val todoAlertTime = c1.child("todoAlertTime").value as String
                val todoLocationName = c1.child("todoLocationName").value as String
                val todoLocationLatitude = c1.child("todoLocationLatitude").value as String
                val todoLocationLongitude = c1.child("todoLocationLongitude").value as String
                val todoOwnerIdx = c1.child("todoOwnerIdx").value as Long
                val todoOwnerName = c1.child("todoOwnerName").value as String

                val todo = TodoClass(
                    todoIdx,
                    todoContent,
                    todoIsChecked,
                    todoCategoryIdx,
                    todoCategoryName,
                    todoFontColor,
                    todoBackgroundColor,
                    todoDate,
                    todoAlertTime,
                    todoLocationName,
                    todoLocationLatitude,
                    todoLocationLongitude,
                    todoOwnerIdx,
                    todoOwnerName
                )
                tempTodoList.add(todo)
            }

            todoList.value = tempTodoList
        }
    }
}