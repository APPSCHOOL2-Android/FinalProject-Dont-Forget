package com.test.dontforgetproject.UI.MainFriendsFragment

import android.graphics.Paint.Join
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dontforgetproject.DAO.JoinFriend
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.Repository.JoinFriendRepository

class MainFriendsViewModel : ViewModel() {
    var joinFriendList = MutableLiveData<MutableList<JoinFriend>>()
    var myRequestList = MutableLiveData<MutableList<JoinFriend>>()

    init {
        joinFriendList.value = mutableListOf<JoinFriend>()
        myRequestList.value = mutableListOf<JoinFriend>()
    }

    // 이메일로 내게온 친구요청 불러오기
    fun getRequestList(userEmail: String) {
        JoinFriendRepository.getJoinFriendByUserEmail(userEmail) {
            var tempList = mutableListOf<JoinFriend>()
            for (c1 in it.result.children) {
                var joinFriendIdx = c1.child("joinFriendIdx").value as Long
                var joinFriendSenderIdx = c1.child("joinFriendSenderIdx").value as Long
                var joinFriendSenderName = c1.child("joinFriendSenderName").value as String
                var joinFriendReceiverEmail = c1.child("joinFriendReceiverEmail").value as String

                var p1 = JoinFriend(
                    joinFriendIdx,
                    joinFriendSenderIdx,
                    joinFriendSenderName,
                    joinFriendReceiverEmail
                )

                tempList.add(p1)
            }

            tempList.reverse()
            joinFriendList.value = tempList
        }
    }

    //

    // 이메일로 내게온 친구요청 불러오기
    fun getMyRequest(userIdx: Long) {
        JoinFriendRepository.getJoinFriendByUserIdx(userIdx) {
            var tempList = mutableListOf<JoinFriend>()
            for (c1 in it.result.children) {
                var joinFriendIdx = c1.child("joinFriendIdx").value as Long
                var joinFriendSenderIdx = c1.child("joinFriendSenderIdx").value as Long
                var joinFriendSenderName = c1.child("joinFriendSenderName").value as String
                var joinFriendReceiverEmail = c1.child("joinFriendReceiverEmail").value as String

                var p1 = JoinFriend(
                    joinFriendIdx,
                    joinFriendSenderIdx,
                    joinFriendSenderName,
                    joinFriendReceiverEmail
                )

                tempList.add(p1)
            }

            tempList.reverse()
            myRequestList.value = tempList
        }
    }
}