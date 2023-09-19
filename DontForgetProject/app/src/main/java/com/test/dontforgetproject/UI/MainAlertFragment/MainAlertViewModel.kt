package com.test.dontforgetproject.UI.MainAlertFragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dontforgetproject.DAO.AlertClass
import com.test.dontforgetproject.Repository.AlertRepository
import com.test.dontforgetproject.Repository.TodoRepository

class MainAlertViewModel : ViewModel() {

    var alertIdx = MutableLiveData<Long>()
    var alertContent = MutableLiveData<String>()
    var alertReceiverIdx = MutableLiveData<Long>()
    var alertType = MutableLiveData<Long>()
    var alertList = MutableLiveData<MutableList<AlertClass>>()

    init {
        alertList.value = mutableListOf<AlertClass>()
    }

    fun getAlertInfo(todoIdx: Long) {

        AlertRepository.getAlertInfoByIdx(todoIdx) { it ->
            for (c1 in it.result.children) {
                alertIdx.value = c1.child("alertIdx").value as Long
                alertContent.value = c1.child("alertContent").value as String
                alertReceiverIdx.value = c1.child("alertReceiverIdx").value as Long
                alertType.value = c1.child("alertType").value as Long
            }
        }
    }

    fun getAlert(userIdx: Long) {

        val tempList = mutableListOf<AlertClass>()

        AlertRepository.getAlertInfoAll(userIdx) {
            for (c1 in it.result.children) {
                val alertIdx = c1.child("alertIdx").value as Long
                var alertContent = c1.child("alertContent").value as String
                val alertReceiverIdx = c1.child("alertReceiverIdx").value as Long
                val alertType = c1.child("alertType").value as Long

                val a1 = AlertClass(alertIdx, alertContent, alertReceiverIdx, alertType)
                tempList.add(a1)
            }

            alertList.value = tempList
        }
    }
}