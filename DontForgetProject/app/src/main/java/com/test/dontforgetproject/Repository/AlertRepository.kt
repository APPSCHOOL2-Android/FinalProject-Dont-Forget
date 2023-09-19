package com.test.dontforgetproject.Repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.test.dontforgetproject.DAO.AlertClass

class AlertRepository {

    companion object {
        // 알림 인덱스 가져오기
        fun getAlertIdx(callback1:(Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            // 알림 인덱스 번호
            val alertIdxRef = database.getReference("AlertIdx")
            alertIdxRef.get().addOnCompleteListener(callback1)
        }

        // 알림 인덱스 저장
        fun setAlertIdx(alertIdx:Long, callback1: (Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val alertIdxRef = database.getReference("AlertIdx")

            alertIdxRef.get().addOnCompleteListener {
                it.result.ref.setValue(alertIdx).addOnCompleteListener(callback1)
            }
        }

        // 알림 정보 저장
        fun addAlertInfo(alertDataClass: AlertClass, callback1:(Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val alertDataRef = database.getReference("alertInfo")
            alertDataRef.push().setValue(alertDataClass).addOnCompleteListener(callback1)
        }

        // 해당 유저의 전체 알림 정보 가져오기
        fun getAlertInfoAll(userIdx: Long, callback1: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val alertDataRef = database.getReference("alertInfo")
            alertDataRef.orderByChild("alertReceiverIdx").equalTo(userIdx.toDouble()).get().addOnCompleteListener(callback1)
        }

        // 해당 인덱스 알림 정보 가져오기
        fun getAlertInfoByIdx(alertIdx: Long, callback1: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val alertDataRef = database.getReference("alertInfo")
            alertDataRef.orderByChild("alertIdx").equalTo(alertIdx.toDouble()).get().addOnCompleteListener(callback1)
        }

        // 해당 인덱스 알림 정보 삭제
        fun removeAlert(alertIdx:Long, callback1: (Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val testDataRef = database.getReference("alertInfo")

            testDataRef.orderByChild("alertIdx").equalTo(alertIdx.toDouble()).get().addOnCompleteListener {
                for(a1 in it.result.children) {
                    // 해당 데이터 삭제
                    a1.ref.removeValue().addOnCompleteListener(callback1)
                }
            }
        }

        // 해당 유저의 알림 정보 삭제
        fun removeAlertByUserIdx(userIdx:Long, callback1: (Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val testDataRef = database.getReference("alertInfo")

            testDataRef.orderByChild("alertReceiverIdx").equalTo(userIdx.toDouble()).get().addOnCompleteListener {
                for(a1 in it.result.children) {
                    // 해당 데이터 삭제
                    a1.ref.removeValue().addOnCompleteListener(callback1)
                }
            }
        }
    }
}