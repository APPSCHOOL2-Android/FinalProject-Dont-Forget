package com.test.dontforgetproject.UI.MainCategoryFragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dontforgetproject.DAO.CategoryClass
import com.test.dontforgetproject.Repository.CategoryRepository

class MainCategoryViewModel() : ViewModel() {
    var categoryList = MutableLiveData<MutableList<CategoryClass>>()

    init {
        categoryList.value = mutableListOf<CategoryClass>()
    }

    fun reset() {
        categoryList.value = mutableListOf<CategoryClass>()
    }

    // 나의 카테고리 가져오기
    fun getMyCategory(userIdx: Long) {
        val tempCategoryList = mutableListOf<CategoryClass>()

        CategoryRepository.getAllCategory {
            for (c1 in it.result.children) {
                val categoryIdx = c1.child("categoryIdx").value as Long
                val categoryName = c1.child("categoryName").value as String
                val categoryColor = c1.child("categoryColor").value as Long
                val categoryFontColor = c1.child("categoryFontColor").value as Long
                val categoryJoinUserIdxList =
                    c1.child("categoryJoinUserIdxList").value as ArrayList<Long>?
                val categoryJoinUserNameList =
                    c1.child("categoryJoinUserNameList").value as ArrayList<String>?
                val categoryIsPublic = c1.child("categoryIsPublic").value as Long
                val categoryOwnerIdx = c1.child("categoryOwnerIdx").value as Long
                val categoryOwnerName = c1.child("categoryOwnerName").value as String

                if (userIdx !in categoryJoinUserIdxList!!) {
                    continue
                }

                val categoryClass = CategoryClass(
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
                tempCategoryList.add(categoryClass)
            }
            tempCategoryList.reverse()
            Log.i("cccc", tempCategoryList.toString())
            categoryList.value = tempCategoryList
        }
    }
}