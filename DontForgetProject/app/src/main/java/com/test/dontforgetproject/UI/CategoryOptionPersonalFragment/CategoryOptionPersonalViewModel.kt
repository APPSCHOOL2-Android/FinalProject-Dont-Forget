package com.test.dontforgetproject.UI.CategoryOptionPersonalFragment

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dontforgetproject.DAO.CategoryClass
import com.test.dontforgetproject.DAO.TodoClass
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.Repository.TodoRepository

class CategoryOptionPersonalViewModel() : ViewModel() {
    var categoryName = MutableLiveData<String>()
    var categoryColor = MutableLiveData<Int>()

    var todoList = MutableLiveData<MutableList<TodoClass>>()

    init {
        todoList.value = mutableListOf<TodoClass>()
    }

    fun reset() {
        categoryName.value = ""
        categoryColor.value = Color.WHITE
    }

    // 카테고리 정보 가져오기
    fun getCategoryInfo(categoryIdx: Long) {
        CategoryRepository.getCategoryByIdx(categoryIdx) {
            for (c1 in it.result.children) {
                val c1Name = c1.child("categoryName").value as String
                val c1Color = c1.child("categoryColor").value as Long

                categoryName.value = c1Name
                categoryColor.value = c1Color.toInt()
            }
        }
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