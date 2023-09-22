package com.test.dontforgetproject.Repository

import android.net.Uri
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.test.dontforgetproject.DAO.UserClass

class UserRepository {

    companion object{
        // 사용자 info 저장
        fun setUserInfo(userInfo:UserClass,callback1: (Task<Void>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val userInfoData = database.getReference("userInfo")
            userInfoData.push().setValue(userInfo).addOnCompleteListener(callback1)

        }

        //사용자 인덱스 저장
        fun setUserIdx(userIdx:Long,callback1: (Task<Void>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val userIdxRef = database.getReference("UserIdx")

            userIdxRef.get().addOnCompleteListener{
                it.result.ref.setValue(userIdx).addOnCompleteListener {
                    callback1(it)
                }
            }
        }

        // 사용자 인덱스 가져오기
        fun getUserInfo(callback1: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val userSellerIdxRef = database.getReference("UserIdx")
            userSellerIdxRef.get().addOnCompleteListener(callback1)
        }

        // 사용자 ID를 가지고 사용자 정보를 가져옴
        fun getUserInfoById(userId:String,callback1: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val userDataRef = database.getReference("userInfo")
            userDataRef.orderByChild("userId").equalTo(userId).get().addOnCompleteListener(callback1)
        }



        // 사용자 정보 수정
        fun modifyUserInfo(userInfo: UserClass,callback1: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val userInfoData = database.getReference("userInfo")
            userInfoData.orderByChild("userIdx").equalTo(userInfo.userIdx.toDouble()).get().addOnCompleteListener {
                for(data in it.result.children){
                    data.ref.child("userIdx").setValue(userInfo.userIdx)
                    data.ref.child("userName").setValue(userInfo.userName)
                    data.ref.child("userEmail").setValue(userInfo.userEmail)
                    data.ref.child("userImage").setValue(userInfo.userImage)
                    data.ref.child("userIntroduce").setValue(userInfo.userIntroduce)
                    data.ref.child("userFriendList").setValue(userInfo.userFriendList)

                }
            }
        }
        // 사용자 정보 삭제
        fun deleteUserInfo(userInfoIdx:Long,callback1: (Task<Void>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val userInfoDataRef = database.getReference("userInfo")

            userInfoDataRef.orderByChild("userIdx").equalTo(userInfoIdx.toDouble()).get().addOnCompleteListener {
                for(c1 in it.result.children){
                    c1.ref.removeValue().addOnCompleteListener(callback1)
                }
            }
        }
        // 프로필 업로드
        fun setUploadProfile(fileName:String, uploadUri: Uri,callback1: (Task<UploadTask.TaskSnapshot>) -> Unit){
            val storage = FirebaseStorage.getInstance()
            var imageRef = storage.reference.child(fileName)
            imageRef.putFile(uploadUri!!).addOnCompleteListener(callback1)
        }
        // 프로필 가져오기
        fun getProfile(fileName : String, callback1:(Task<Uri>) -> Unit) {
            val storage = FirebaseStorage.getInstance()
            val fileRef = storage.reference.child(fileName)
            fileRef.downloadUrl.addOnCompleteListener(callback1)
        }
    }
}