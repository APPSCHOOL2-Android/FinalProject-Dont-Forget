package com.test.dontforgetproject.UI.TodoDetailPersonalFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dontforgetproject.Repository.TodoRepository

class TodoDetailPersonalViewModel : ViewModel() {



    var todoContent = MutableLiveData<String>()
    var todoIsChecked = MutableLiveData<Long>()
    var todoCategoryIdx = MutableLiveData<Long>()
    var todoCategoryName = MutableLiveData<String>()
    var todoFontColor = MutableLiveData<Long>()
    var todoBackgroundColor = MutableLiveData<Long>()
    var todoDate = MutableLiveData<String>()
    var todoAlertTime = MutableLiveData<String>()
    var todoLocationName = MutableLiveData<String>()
    var todoLocationLatitude = MutableLiveData<String>()
    var todoLocationLongitude = MutableLiveData<String>()
    var todoOwnerIdx = MutableLiveData<Long>()
    var todoOwnerName = MutableLiveData<String>()

    fun getTodoInfo(todoIdx: Long) {

        TodoRepository.getTodoInfoByIdx(todoIdx) { it ->
            for (c1 in it.result.children) {
                todoContent.value = c1.child("todoContent").value as String
                todoIsChecked.value = c1.child("todoIsChecked").value as Long
                todoCategoryIdx.value = c1.child("todoCategoryIdx").value as Long
                todoCategoryName.value = c1.child("todoCategoryName").value as String
                todoFontColor.value = c1.child("todoFontColor").value as Long
                todoBackgroundColor.value = c1.child("todoBackgroundColor").value as Long
                todoDate.value = c1.child("todoDate").value as String
                todoAlertTime.value = c1.child("todoAlertTime").value as String
                todoLocationName.value = c1.child("todoLocationName").value as String
                todoLocationLatitude.value = c1.child("todoLocationLatitude").value as String
                todoLocationLongitude.value = c1.child("todoLocationLongitude").value as String
                todoOwnerIdx.value = c1.child("todoOwnerIdx").value as Long
                todoOwnerName.value = c1.child("todoOwnerName").value as String
            }
        }
    }
}