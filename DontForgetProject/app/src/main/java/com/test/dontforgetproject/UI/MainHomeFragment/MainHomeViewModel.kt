package com.test.dontforgetproject.UI.MainHomeFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.dontforgetproject.DAO.CategoryClass
import com.test.dontforgetproject.DAO.TodoClass
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.Repository.TodoRepository
import com.test.dontforgetproject.Util.LoadingDialog
import kotlinx.coroutines.launch

class MainHomeViewModel : ViewModel() {
    var categories = MutableLiveData<List<CategoryClass>>()
    var categories2 = MutableLiveData<List<CategoryClass>>()
    var todoList = MutableLiveData<List<TodoClass>>()
    var todoList2 = MutableLiveData<List<TodoClass>>()

    private var loadingDialog: LoadingDialog? = null

    init {
        categories.value = mutableListOf()
        categories2.value = mutableListOf()
        todoList.value = mutableListOf()
        todoList2.value = mutableListOf()
    }

    fun getTodo(): List<TodoClass> {
        return todoList2.value!!
    }

    fun getCategories(): List<CategoryClass> {
        return categories.value!!
    }

    private fun closeLoadingDialog() {
        // 로딩 다이얼로그가 null이 아니면 닫습니다.
        loadingDialog?.dismiss()
    }

    // 카테고리 목록
    fun getCategoryAll(userIdx: Long, loadingDialog: LoadingDialog): List<Long> {
        val categoryList = mutableListOf<CategoryClass>()
        val categoryIdxList = mutableListOf<Long>()

        this.loadingDialog = loadingDialog
        loadingDialog.show()

        viewModelScope.launch {
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
                    categoryIdxList.add(categoryIdx)
                }
                categories.value = categoryList
                categories2.value = categoryList

                closeLoadingDialog()
            }
        }
        return categoryIdxList
    }

    // 해당 카테고리
    fun getCategoryByCategoryIdx(categoryIdx: Long) {
        val categoryList = mutableListOf<CategoryClass>()

        CategoryRepository.getCategoryByIdx(categoryIdx) {
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

                val categoryTemp = CategoryClass(
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
                categoryList.add(categoryTemp)
            }
            categories2.value = categoryList
        }
    }

    // 해당 날짜의 할 일 목록
    fun getTodoByDate(date: String, categoryIdxList: List<Long>) {
        val tempList = mutableListOf<TodoClass>()

        TodoRepository.getTodoInfoByDate(date) {
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

    // 모든 할 일 목록
    fun getTodo(categoryIdxList: List<Long>) {
        val tempList = mutableListOf<TodoClass>()

        TodoRepository.getAllTodo {
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
            todoList2.value = tempList
        }
    }

    fun getTodoListForCategory(categoryIdx: Long): List<TodoClass> {
        return todoList.value?.filter { it.todoCategoryIdx == categoryIdx } ?: emptyList()
    }
}