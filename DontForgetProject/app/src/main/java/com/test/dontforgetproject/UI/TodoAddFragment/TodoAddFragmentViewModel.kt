package com.test.dontforgetproject.UI.TodoAddFragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide.init
import com.google.firebase.database.FirebaseDatabase
import com.test.dontforgetproject.DAO.CategoryClass
import com.test.dontforgetproject.DAO.TodoClass

import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.Repository.UserRepository

class TodoAddFragmentViewModel :ViewModel(){

    var categoryInfo = MutableLiveData<MutableList<TodoClass>>()
    var name = MutableLiveData<String>()
    var categoryColor = MutableLiveData<Long>()
    var fontColor = MutableLiveData<Long>()

    init {
        categoryInfo.value = mutableListOf<TodoClass>()
    }

    fun getData(){

        var templist = mutableListOf<TodoClass>()
        var useridx = MyApplication.loginedUserInfo.userIdx

        CategoryRepository.getCategoryInfoByIdx(useridx){
            for(c1 in it.result.children){
                var idx = c1.child("categoryIdx").value as Long
                var name = c1.child("categoryName").value.toString()
                var color = c1.child("categoryColor").value as Long
                var fontcolor = c1.child("categoryFontColor").value as Long
                var owneridx = c1.child("categoryOwnerIdx").value as Long
                var ownerName = c1.child("categoryOwnerName").value.toString()
                var datas = TodoClass(idx,"None",0,idx,name,fontcolor,color,"None","None","None","None",
                "None",owneridx,ownerName)

                templist.add(datas)
                categoryInfo.value = templist
            }
        }
    }

    fun resetList(){
        categoryInfo.value = mutableListOf<TodoClass>()

    }

}