package com.test.dontforgetproject.UI.MainHomeFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dontforgetproject.DAO.CategoryClass
import com.test.dontforgetproject.Repository.CategoryRepository

class MainHomeViewModel : ViewModel() {
    var categories = MutableLiveData<List<CategoryClass>>()

    init {
        categories.value = mutableListOf()
    }

    // 카테고리 목록
    fun getCategories(userIdx: Long) {
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
}