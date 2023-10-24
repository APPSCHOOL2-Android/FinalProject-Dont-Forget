package com.test.dontforgetproject.UI.TodoAddFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dontforgetproject.DAO.TodoClass
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.Repository.CategoryRepository

class TodoAddFragmentViewModel :ViewModel(){

    var categoryInfo = MutableLiveData<MutableList<TodoClass>>()
    var name = MutableLiveData<String>()
    var categoryColor = MutableLiveData<Long>()
    var fontColor = MutableLiveData<Long>()
    var date = MutableLiveData<String>()
    var time = MutableLiveData<String>()
    var locate = MutableLiveData<String>()

    init {
        categoryInfo.value = mutableListOf<TodoClass>()
        name = MutableLiveData<String>()
        categoryColor = MutableLiveData<Long>()
        fontColor = MutableLiveData<Long>()
        date = MutableLiveData<String>()
        time = MutableLiveData<String>()
        locate = MutableLiveData<String>()
    }

    //카데고리 데이터 삽입
    fun getData(){

        var templist = mutableListOf<TodoClass>()
        var useridx = MyApplication.loginedUserInfo.userIdx

        CategoryRepository.getAllCategory {
            for (c1 in it.result.children){
                var idx = c1.child("categoryIdx").value as Long
                var name = c1.child("categoryName").value.toString()
                var color = c1.child("categoryColor").value as Long
                val categoryJoinUserIdxList =
                    c1.child("categoryJoinUserIdxList").value as ArrayList<Long>?
                var fontcolor = c1.child("categoryFontColor").value as Long
                var owneridx = c1.child("categoryOwnerIdx").value as Long
                var ownerName = c1.child("categoryOwnerName").value.toString()
                if(useridx !in categoryJoinUserIdxList!!){
                    continue
                }
                var datas = TodoClass(idx,"None",0,idx,name,fontcolor,color,"None","None","None","None",
                    "None",owneridx,ownerName)
                templist.add(datas)
                categoryInfo.value = templist
            }
        }
    }




    //초기화
    fun resetList(){
        categoryInfo.value = mutableListOf<TodoClass>()
        name = MutableLiveData<String>()
        categoryColor = MutableLiveData<Long>()
        fontColor = MutableLiveData<Long>()
        date = MutableLiveData<String>()
        time = MutableLiveData<String>()
        locate = MutableLiveData<String>()
    }

}