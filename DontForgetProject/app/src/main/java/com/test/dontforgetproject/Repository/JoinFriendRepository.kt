package com.test.dontforgetproject.Repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.test.dontforgetproject.DAO.JoinFriend

class JoinFriendRepository {

    companion object{
        // 친구추가 받는사람 code 이용하여 joinFriend 객체 생성하기
        fun getJoinFriendIdx(callback1: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("JoinFriendIdx")
            databaseRef.get().addOnCompleteListener(callback1)
        }

        fun setJoinFriendIdx(idx : Long, callback1: (Task<Void>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("JoinFriendIdx")
            databaseRef.get().addOnCompleteListener {
                it.result.ref.setValue(idx).addOnCompleteListener(callback1)
            }
        }

        fun addJoinFriend(joinFriend: JoinFriend,callback1:(Task<Void>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("joinFriendInfo")
            databaseRef.push().setValue(joinFriend).addOnCompleteListener(callback1)
        }

        // 모든 친구추가중 receiver 가 현재 유저의 email 인 친구추가 목록 불러오기
        // 나에게 걸린 친구추가를 불러오는것
        fun getJoinFriendByUserEmail(userEmail : String, callback1: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("joinFriendInfo")
            databaseRef.orderByChild("joinFriendReceiverEmail").equalTo(userEmail).get().addOnCompleteListener (callback1)
        }

        fun getJoinFriendByUserIdx(userIdx : Long, callback1: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("joinFriendInfo")
            databaseRef.orderByChild("joinFriendSenderIdx").equalTo(userIdx.toDouble()).get().addOnCompleteListener (callback1)
        }

        // 이메일로 해당 친구 이름 가져오기
        fun getUserInfoByEmail(userEmail:String, callback1: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("userInfo")
            databaseRef.orderByChild("userEmail").equalTo(userEmail).get().addOnCompleteListener(callback1)
        }

        // 인덱스로 해당친구 이메일 가져오기
        fun getUserInfoByIdx(userIdx : Long, callback1: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("userInfo")
            databaseRef.orderByChild("userIdx").equalTo(userIdx.toDouble()).get().addOnCompleteListener(callback1)
        }

        // 친구추가 or 거절시 해당 요청 삭제하기
        fun deleteJoinFriend(joinFriendIdx : Long,callback1: (Task<Void>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("joinFriendInfo")
            databaseRef.orderByChild("joinFriendIdx").equalTo(joinFriendIdx.toDouble()).get().addOnCompleteListener {
                for(c1 in it.result.children){
                    c1.ref.removeValue().addOnCompleteListener(callback1)
                }
            }
        }

        /* 회원탈퇴시 관련된 JoinFriend 모두 삭제 */
        // 1. userIdx 로 삭제
        // 삭제할계정이 보낸 친구추가 삭제
        fun deleteJoinFriendByUserIdx(userIdx : Long, callback1: (Task<Void>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("joinFriendInfo")
            databaseRef.orderByChild("joinFriendSenderIdx").equalTo(userIdx.toDouble()).get().addOnCompleteListener {
                for(c1 in it.result.children){
                    c1.ref.removeValue().addOnCompleteListener(callback1)
                }
            }
        }

        // 2. email 로 삭제
        // 삭제할계정의 email 로 보내진 친구추가 삭제
        fun deleteJoinFriendByEmail(userEmail : String, callback1: (Task<Void>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("joinFriendInfo")
            databaseRef.orderByChild("joinFriendReceiverEmail").equalTo(userEmail).get().addOnCompleteListener {
                for(c1 in it.result.children){
                    c1.ref.removeValue().addOnCompleteListener(callback1)
                }
            }
        }

        // 요거 쓰시면 됩니다.
        // userIdx, userEmail 입력하여 관련된 모든 JoinFriend삭제
        fun deleteJoinFriendByMyData(userIdx : Long, userEmail : String){
            deleteJoinFriendByUserIdx(userIdx){
                deleteJoinFriendByEmail(userEmail){
                }
            }
        }
    }
}