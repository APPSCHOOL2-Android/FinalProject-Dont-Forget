package com.test.dontforgetproject.UI.MainHomeFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dontforgetproject.DAO.CategoryClass
import com.test.dontforgetproject.DAO.TodoClass
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.Repository.TodoRepository

class MainHomeViewModel : ViewModel() {
    var categories = MutableLiveData<List<CategoryClass>>()
    var todoList = MutableLiveData<List<TodoClass>>()

    init {
        categories.value = mutableListOf()
        todoList.value = mutableListOf()
    }

    // 카테고리 목록
    fun getCategoryAll(userIdx: Long) {
        val categoryList = mutableListOf<CategoryClass>()

        CategoryRepository.getAllCategory {
            for (c1 in it.result.children) {
                var categoryIdx = c1.child("categoryIdx").value as Long
                var categoryName = c1.child("categoryName").value as String
                var categoryColor = c1.child("categoryColor").value as Long
                var categoryFontColor = c1.child("categoryFontColor").value as Long
                var categoryJoinUserIdxList =
                    c1.child("categoryJoinUserIdxList").value as ArrayList<Long>?
                var categoryJoinUserNameList =
                    c1.child("categoryJoinUserNameList").value as ArrayList<String>?
                var categoryIsPublic = c1.child("categoryIsPublic").value as Long
                var categoryOwnerIdx = c1.child("categoryOwnerIdx").value as Long
                var categoryOwnerName = c1.child("categoryOwnerName").value as String

                if (!categoryJoinUserIdxList?.contains(userIdx)!!) continue

                val category = CategoryClass(
                    categoryIdx,
                    categoryName,
                    categoryColor,
                    categoryFontColor,
                    categoryJoinUserIdxList,
                    categoryJoinUserNameList,
                    categoryIsPublic,
                    categoryOwnerIdx,
                    categoryOwnerName
                )
                categoryList.add(category)
            }
            categories.value = categoryList
        }
    }

    // 할 일 목록
    fun getTodoAll(categoryIdxList: List<Long>) {
        val tempList = mutableListOf<TodoClass>()

        TodoRepository.getTodoIdx {
            for (c1 in it.result.children) {
                var todoIdx = c1.child("todoIdx").value as Long
                var todoContent = c1.child("todoContent").value as String
                var todoIsChecked = c1.child("todoIsChecked").value as Long
                var todoCategoryIdx = c1.child("todoCategoryIdx").value as Long
                var todoCategoryName = c1.child("todoCategoryName").value as String
                var todoFontColor = c1.child("todoFontColor").value as Long
                var todoBackgroundColor = c1.child("todoBackgroundColor").value as Long
                var todoDate = c1.child("todoDate").value as String
                var todoAlertTime = c1.child("todoAlertTime").value as String
                var todoLocationName = c1.child("todoLocationName").value as String
                var todoLocationLatitude = c1.child("todoLocationLatitude").value as String
                var todoLocationLongitude = c1.child("todoLocationLongitude").value as String
                var todoOwnerIdx = c1.child("todoOwnerIdx").value as Long
                var todoOwnerName = c1.child("todoOwnerName").value as String

                if (!categoryIdxList.contains(todoCategoryIdx)) continue

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
                tempList.add(todo)
            }
            todoList.value = tempList
        }
    }
}