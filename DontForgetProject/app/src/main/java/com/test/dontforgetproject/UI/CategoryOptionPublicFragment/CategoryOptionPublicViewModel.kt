package com.test.dontforgetproject.UI.CategoryOptionPublicFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dontforgetproject.Repository.CategoryRepository

class CategoryOptionPublicViewModel() : ViewModel() {
    var categoryName = MutableLiveData<String>()
    var categoryColor = MutableLiveData<Int>()
    var categoryOwner = MutableLiveData<String>()

    // 카테고리 정보 가져오기
    fun getCategoryInfo(categoryIdx: Long) {
        CategoryRepository.getCategoryByIdx(categoryIdx) {
            for (c1 in it.result.children) {
                val c1Name = c1.child("categoryName").value as String
                val c1Color = c1.child("categoryColor").value as Long
                val c1Owner = c1.child("categoryOwnerName").value as String

                categoryName.value = c1Name
                categoryColor.value = c1Color.toInt()
                categoryOwner.value = c1Owner
            }
        }
    }
}