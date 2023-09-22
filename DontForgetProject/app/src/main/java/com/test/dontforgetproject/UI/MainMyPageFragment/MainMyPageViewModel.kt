package com.test.dontforgetproject.UI.MainMyPageFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.Repository.UserRepository

class MainMyPageViewModel : ViewModel() {

    var userIdx = MutableLiveData<Long>()
    var userName = MutableLiveData<String>()
    var userEmail = MutableLiveData<String>()
    var userImage = MutableLiveData<String>()
    var userId = MutableLiveData<String>()
    var userFriendList = MutableLiveData<ArrayList<Friend>>()
    var userIntoduce = MutableLiveData<String>()

    fun getUserInfo(userClass: UserClass) {

        UserRepository.getUserInfo {
            UserRepository.getUserInfoById(userClass.userId) {
                for (c1 in it.result.children) {
                    val newFriendList = mutableListOf<Friend>()
                    val newHashMap =
                        c1.child("userFriendList").value as ArrayList<HashMap<String, Any>>
                    for (i in newHashMap) {
                        val idx = i["friendIdx"] as Long
                        val name = i["friendName"] as String
                        val email = i["friendEmail"] as String

                        val friend = Friend(idx, name, email)
                        newFriendList.add(friend)
                    }
                    userIdx.value = c1.child("userIdx").value as Long
                    userName.value = c1.child("userName").value as String
                    userEmail.value = c1.child("userEmail").value as String
                    userId.value = c1.child("userId").value as String
                    userImage.value = c1.child("userImage").value as String
                    userFriendList.value = newFriendList as ArrayList<Friend>
                    userIntoduce.value = c1.child("userIntroduce").value as String
                }
            }
        }
    }
}