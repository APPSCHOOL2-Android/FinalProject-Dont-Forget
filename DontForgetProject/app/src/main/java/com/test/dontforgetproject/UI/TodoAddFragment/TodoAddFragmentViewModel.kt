package com.test.dontforgetproject.UI.TodoAddFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.test.dontforgetproject.DAO.CategoryClass

class TodoAddFragmentViewModel :ViewModel(){

    var categoryInfo = MutableLiveData<MutableList<CategoryClass>>()

    init {
        categoryInfo.value = mutableListOf<CategoryClass>()
    }

}