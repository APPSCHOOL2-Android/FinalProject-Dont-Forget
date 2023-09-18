package com.test.dontforgetproject.Repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.test.dontforgetproject.DAO.CategoryClass

class CategoryRepository {
    companion object {
        // 카테고리 인덱스 가져오기
        fun getCategoryIdx(callback1: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val categoryIdxRef = database.getReference("CategoryIdx")
            categoryIdxRef.get().addOnCompleteListener(callback1)
        }

        // 카테고리 인덱스 저장하기
        fun setCategoryIdx(cartIdx: Long, callback1: (Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val categoryIdxRef = database.getReference("CategoryIdx")
            categoryIdxRef.get().addOnCompleteListener {
                it.result.ref.setValue(cartIdx).addOnCompleteListener(callback1)
            }
        }

        // 전체 카테고리 정보 가져오기
        fun getAllCategory(callback1: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val categoryDataRef = database.getReference("categoryInfo")
            categoryDataRef.orderByChild("categoryIdx").get().addOnCompleteListener(callback1)
        }

        // 카테고리 인덱스를 통해 카테고리 정보 가져오기
        fun getCategoryByIdx(categoryIdx: Long, callback1: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val categoryDataRef = database.getReference("categoryInfo")
            categoryDataRef.orderByChild("categoryIdx").equalTo(categoryIdx.toDouble()).get().addOnCompleteListener(callback1)
        }

        // 카테고리 추가하기
        fun addCategoryInfo(categoryClass: CategoryClass, callback1: (Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val categoryDataRef = database.getReference("categoryInfo")

            categoryDataRef.push().setValue(categoryClass).addOnCompleteListener(callback1)
        }

        // 카테고리 삭제하기
        fun removeCategory(categoryIdx: Long, callback1: (Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val categoryDataRef = database.getReference("categoryInfo")

            categoryDataRef.orderByChild("categoryIdx").equalTo(categoryIdx.toDouble()).get()
                .addOnCompleteListener {
                    for (c1 in it.result.children) {
                        c1.ref.removeValue().addOnCompleteListener(callback1)
                    }
                }
        }

        // 카테고리 수정하기
        fun modifyCategory(categoryClass: CategoryClass, callback1: (Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val categoryDataRef = database.getReference("categoryInfo")

            categoryDataRef.orderByChild("categoryIdx")
                .equalTo(categoryClass.categoryIdx.toDouble()).get().addOnCompleteListener {
                for (a1 in it.result.children) {
                    a1.ref.child("categoryName").setValue(categoryClass.categoryName)
                    a1.ref.child("categoryColor").setValue(categoryClass.categoryColor)
                    a1.ref.child("categoryFontColor").setValue(categoryClass.categoryFontColor)
                    a1.ref.child("categoryJoinUserIdxList")
                        .setValue(categoryClass.categoryJoinUserIdxList)
                    a1.ref.child("categoryJoinUserNameList")
                        .setValue(categoryClass.categoryJoinUserNameList)
                        .addOnCompleteListener(callback1)
                }
            }
        }

        //유저 idx로 카데고리 찾기
        fun getCategoryInfoByIdx(idx:Long,callback1: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("categoryInfo")
            databaseRef.orderByChild("categoryOwnerIdx").equalTo(idx.toDouble()!!).get().addOnCompleteListener (callback1)
        }

        // 카테고리 idx를 통해 할일 삭제
        fun removeTodoByCategoryIdx(categoryIdx: Long, callback1: (Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val todoDataRef = database.getReference("todoInfo")

            todoDataRef.orderByChild("todoCategoryIdx").equalTo(categoryIdx.toDouble()).get()
                .addOnCompleteListener {
                    for (a1 in it.result.children) {
                        // 해당 데이터 삭제
                        a1.ref.removeValue().addOnCompleteListener(callback1)
                    }
                }
        }

        // 유저 idx를 통해 카테고리 삭제
        fun removeCategoryByUserIdx(userIdx: Long, callback1: (Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val todoDataRef = database.getReference("categoryInfo")

            todoDataRef.orderByChild("categoryOwnerIdx").equalTo(userIdx.toDouble()).get()
                .addOnCompleteListener {
                    for (a1 in it.result.children) {
                        // 해당 데이터 삭제
                        a1.ref.removeValue().addOnCompleteListener(callback1)
                    }
                }
        }
    }
}