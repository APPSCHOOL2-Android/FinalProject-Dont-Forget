package com.test.dontforgetproject.Repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.test.dontforgetproject.DAO.TodoClass

class TodoRepository {

    companion object {

        // 할일 인덱스 가져오기
        fun getTodoIdx(callback1:(Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            // 게시글 인덱스 번호
            val todoIdxRef = database.getReference("TodoIdx")
            todoIdxRef.get().addOnCompleteListener(callback1)
        }

        // 할일 삭제
        fun removeTodo(todoIdx:Long, callback1: (Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val testDataRef = database.getReference("todoInfo")

            testDataRef.orderByChild("todoIdx").equalTo(todoIdx.toDouble()).get().addOnCompleteListener {
                for(a1 in it.result.children) {
                    // 해당 데이터 삭제
                    a1.ref.removeValue().addOnCompleteListener(callback1)
                }
            }
        }

        // 할일 수정
        fun modifyTodo(todoDataClass: TodoClass, callback1: (Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val todoDataRef = database.getReference("todoInfo")

            todoDataRef.orderByChild("todoIdx").equalTo(todoDataClass.todoIdx.toDouble()).get().addOnCompleteListener {
                for(a1 in it.result.children) {
                    a1.ref.child("todoContent").setValue(todoDataClass.todoContent)
                    a1.ref.child("todoDate").setValue(todoDataClass.todoDate)
                    a1.ref.child("todoAlertTime").setValue(todoDataClass.todoAlertTime)
                    a1.ref.child("todoLocationName").setValue(todoDataClass.todoLocationName)
                    a1.ref.child("todoLocationLatitude").setValue(todoDataClass.todoLocationLatitude)
                    a1.ref.child("todoLocationLongitude").setValue(todoDataClass.todoLocationLongitude).addOnCompleteListener(callback1)
                }
            }
        }

        // 해당 인덱스 할일 정보 가져오기
        fun getTodoInfoByIdx(todoIdx: Long, callback1: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val todoDataRef = database.getReference("todoInfo")
            todoDataRef.orderByChild("todoIdx").equalTo(todoIdx.toDouble()).get().addOnCompleteListener(callback1)
        }

        fun setTodoIdx(todoIdx: Long,callback1: (Task<Void>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("TodoIdx")
            databaseRef.get().addOnCompleteListener {
                it.result.ref.setValue(todoIdx).addOnCompleteListener {
                    callback1
                }
            }
        }

        fun setTodoAddInfo(todoClass: TodoClass,callback1: (Task<Void>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("todoInfo")
            databaseRef.push().setValue(todoClass).addOnCompleteListener (callback1)
        }
    }
}