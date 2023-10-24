package com.test.dontforgetproject.UI.FriendsDetailFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dontforgetproject.DAO.CategoryClass
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.Repository.JoinFriendRepository

class FriendsDetailViewModel : ViewModel() {
    var friendUserName = MutableLiveData<String>()
    var friendUserEmail = MutableLiveData<String>()
    var friendUserImage = MutableLiveData<String>()
    var friendUserIntroduce = MutableLiveData<String>()
    var friendUserId = MutableLiveData<String>()

    var friendUserFriendList = MutableLiveData<MutableList<Friend>>()

    var categoryList = MutableLiveData<MutableList<CategoryClass>>()

    init {
        categoryList.value = mutableListOf<CategoryClass>()
        friendUserFriendList.value = mutableListOf<Friend>()
    }

    fun getFriendUserInfoByIdx(userIdx: Long) {
        JoinFriendRepository.getUserInfoByIdx(userIdx) {
            var newFriendList = mutableListOf<Friend>()
            for (c1 in it.result.children) {
                friendUserName.value = c1.child("userName").value as String
                friendUserEmail.value = c1.child("userEmail").value as String
                friendUserImage.value = c1.child("userImage").value as String
                friendUserIntroduce.value = c1.child("userIntroduce").value as String
                friendUserId.value = c1.child("userId").value as String

                var tempList = c1.child("userFriendList").value as ArrayList<HashMap<String, Any>>
                for(i in tempList){
                    var idx = i["friendIdx"] as Long
                    var name = i["friendName"] as String
                    var email = i["friendEmail"] as String

                    var friend = Friend(idx, name, email)
                    newFriendList.add(friend)
                }
            }
            friendUserFriendList.value = newFriendList
        }
    }

    fun getCategoryAll() {
        CategoryRepository.getAllCategory {
            var tempList = mutableListOf<CategoryClass>()
            for (c1 in it.result.children) {
                var categoryIdx = c1.child("categoryIdx").value as Long
                var categoryName = c1.child("categoryName").value as String
                var categoryColor = c1.child("categoryColor").value as Long
                var categoryFontColor = c1.child("categoryFontColor").value as Long
                var categoryJoinUserIdxList =
                    c1.child("categoryJoinUserIdxList").value as ArrayList<Long>
                var categoryJoinUserNameList =
                    c1.child("categoryJoinUserNameList").value as ArrayList<String>
                var categoryIsPublic = c1.child("categoryIsPublic").value as Long
                var categoryOwnerIdx = c1.child("categoryOwnerIdx").value as Long
                var categoryOwnerName = c1.child("categoryOwnerName").value as String

                var categoryClass = CategoryClass(
                    categoryIdx,
                    categoryName,
                    categoryColor,
                    categoryFontColor,
                    categoryJoinUserIdxList,
                    categoryJoinUserNameList,
                    categoryIsPublic,
                    categoryOwnerIdx,
                    categoryOwnerName
                )
                tempList.add(categoryClass)
            }
            tempList.reverse()
            categoryList.value = tempList
        }
    }
}