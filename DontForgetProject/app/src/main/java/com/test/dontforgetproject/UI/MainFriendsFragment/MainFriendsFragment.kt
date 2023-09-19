package com.test.dontforgetproject.UI.MainFriendsFragment

import android.app.Application
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.test.dontforgetproject.DAO.AlertClass
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.DAO.JoinFriend
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.AlertRepository
import com.test.dontforgetproject.Repository.JoinFriendRepository
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.databinding.DialogMainFriendsBinding
import com.test.dontforgetproject.databinding.FragmentMainFriendsBinding

class MainFriendsFragment : Fragment() {

    lateinit var binding: FragmentMainFriendsBinding
    lateinit var mainActivity: MainActivity

    // 탭레이아웃
    var tabName = arrayOf("친구목록", "친구 요청함", "내가 보낸 요청")
    val fragmentList = mutableListOf<Fragment>()

    // 뷰페이저
    lateinit var mainFriendsListFragment: MainFriendsListFragment
    lateinit var mainFriendsRequestFragment: MainFriendsRequestFragment
    lateinit var mainFriendsMyRequestFragment: MainFriendsMyRequestFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainFriendsBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        mainFriendsListFragment = MainFriendsListFragment()
        mainFriendsRequestFragment = MainFriendsRequestFragment()
        mainFriendsMyRequestFragment = MainFriendsMyRequestFragment()

        binding.run {

            // 툴바
            toolbarMainFriends.run {
                title = "친구목록"
                inflateMenu(R.menu.menu_main_friends)

                setOnMenuItemClickListener {
                    when (it.itemId) {
                        // 친구추가
                        R.id.item_menuMainFriend_add -> {
                            var dialogMainFriendsBinding =
                                DialogMainFriendsBinding.inflate(layoutInflater)
                            val builder = MaterialAlertDialogBuilder(mainActivity)

                            builder.setView(dialogMainFriendsBinding.root)
                            dialogMainFriendsBinding.editTextDialogMainFriends.requestFocus()

                            builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                                true

                                // 객체생성
                                var joinFriendSenderIdx = MyApplication.loginedUserInfo.userIdx
                                var joinFriendSenderName = MyApplication.loginedUserInfo.userName
                                var joinFriendReceiverEmail = dialogMainFriendsBinding.editTextDialogMainFriends.text.toString()

                                // 이미 친구인 경우
                                // 1. toast 메시지 발생
                                var isAlreadyFriend = false
                                var myFriendList = MyApplication.loginedUserInfo.userFriendList
                                for(i in myFriendList){
                                    if(i.friendEmail == joinFriendReceiverEmail){
                                        isAlreadyFriend = true
                                        Toast.makeText(mainActivity, "이미 친구인 유저입니다", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                // 친구가 아닌경우
                                if(isAlreadyFriend == false){
                                    // 이메일 유효성 검사
                                    // 존재하지 않는 유저인 경우
                                    var isExistUser = false

                                    // 보낸 요청이 이미 내 요청에 들어와 있는 경우
                                    // 1. 해당 친구요청 삭제
                                    // 2. 친구추가
                                    JoinFriendRepository.getUserInfoByEmail(joinFriendReceiverEmail) {
                                        for (c1 in it.result.children) {
                                            // 해당 유저가 존재
                                            isExistUser = true

                                            var receiverIdx = c1.child("userIdx").value as Long
                                            var receiverName = c1.child("userName").value as String
                                            var receiverEmail = c1.child("userEmail").value as String
                                            var receiverImage = c1.child("userImage").value as String
                                            var receiverIntroduce =
                                                c1.child("userIntroduce").value as String
                                            var receiverId = c1.child("userId").value as String
                                            var receiverFriendListHashMap =
                                                c1.child("userFriendList").value as ArrayList<HashMap<String, Any>>

                                            var receiverFriendList = mutableListOf<Friend>()
                                            for (i in receiverFriendListHashMap) {
                                                var idx = i["friendIdx"] as Long
                                                var name = i["friendName"] as String
                                                var email = i["friendEmail"] as String

                                                var friend = Friend(idx, name, email)
                                                receiverFriendList.add(friend)
                                            }

                                            JoinFriendRepository.getJoinFriendByUserIdx(receiverIdx) {
                                                var isAlreadyFriendRequest = false
                                                for (c1 in it.result.children) {
                                                    var JFREmail = c1.child("joinFriendReceiverEmail").value as String
                                                    var joinFriendIdx = c1.child("joinFriendIdx").value as Long

                                                    // 친구요청이 서로 중복인 경우
                                                    if (JFREmail == MyApplication.loginedUserInfo.userEmail) {
                                                        isAlreadyFriendRequest = true
                                                        Toast.makeText(
                                                            mainActivity,
                                                            "이미 내게 요청을 보낸 친구입니다. 친구 추가가 완료 됩니다",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        // 해당 joinFriend 삭제
                                                        JoinFriendRepository.deleteJoinFriend(joinFriendIdx) {
                                                            // MainFriendRequestFragment 리싸이클러뷰 notifysetchange
                                                            for((index, value) in mainFriendsRequestFragment.requestList.withIndex()){
                                                                if(value.joinFriendIdx == joinFriendIdx){
                                                                    mainFriendsRequestFragment.requestList.removeAt(index)
                                                                    mainFriendsRequestFragment.binding.recyclerMainFriendsRequest.adapter?.notifyDataSetChanged()
                                                                }
                                                            }

                                                            // 친구추가
                                                            var SIdx =
                                                                MyApplication.loginedUserInfo.userIdx
                                                            var SName =
                                                                MyApplication.loginedUserInfo.userName
                                                            var SEmail =
                                                                MyApplication.loginedUserInfo.userEmail
                                                            var SFriend = Friend(SIdx, SName, SEmail)

                                                            var RIdx = receiverIdx
                                                            var RName = receiverName
                                                            var REmail = receiverEmail
                                                            var RFriend = Friend(RIdx, RName, REmail)

                                                            // 1. 내계정의 친구 목록에 친구추가
                                                            var SUserInfo = MyApplication.loginedUserInfo
                                                            SUserInfo.userFriendList.add(RFriend)
                                                            UserRepository.modifyUserInfo(SUserInfo){
                                                                // MainFriendsListFragment notify
                                                                mainFriendsListFragment.UFL.add(RFriend)
                                                                mainFriendsListFragment.binding.recyclerMainFriendsList.adapter?.notifyDataSetChanged()
                                                            }

                                                            // 2. 친구계정의 친구목록에 나추가
                                                            var RUserInfo = UserClass(
                                                                receiverIdx,
                                                                receiverName,
                                                                receiverEmail,
                                                                receiverImage,
                                                                receiverIntroduce,
                                                                receiverId,
                                                                receiverFriendList as ArrayList<Friend>
                                                            )
                                                            RUserInfo.userFriendList.add(SFriend)
                                                            UserRepository.modifyUserInfo(RUserInfo){

                                                            }
                                                        }
                                                    }
                                                }

                                                // 친구 요청이 서로 중복이 아닌경우
                                                var isAlreadyMakeRequestToSamePerson = false
                                                if(isAlreadyFriendRequest == false){
                                                    // 내가 이전에 보냈던 요청인 경우
                                                    JoinFriendRepository.getJoinFriendByUserEmail(joinFriendReceiverEmail){
                                                        for(c1 in it.result.children){
                                                            isAlreadyMakeRequestToSamePerson = true
                                                            Toast.makeText(
                                                                mainActivity,
                                                                "이미 친구 요청이 진행중인 유저입니다",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }

                                                        // 내가 이전에 보냈던 요청이 아닌경우
                                                        // 1. 친구요청 생성 및 내가 보낸 요청업데이트
                                                        if(isAlreadyMakeRequestToSamePerson == false){
                                                            JoinFriendRepository.getJoinFriendIdx {
                                                                var joinFriendIdx = it.result.value as Long
                                                                joinFriendIdx++

                                                                val joinFriend = JoinFriend(
                                                                    joinFriendIdx,
                                                                    joinFriendSenderIdx,
                                                                    joinFriendSenderName,
                                                                    joinFriendReceiverEmail
                                                                )
                                                                JoinFriendRepository.addJoinFriend(joinFriend) {
                                                                    JoinFriendRepository.setJoinFriendIdx(joinFriendIdx) {

                                                                        // TODO MainFriendMyRequestFragment 리싸이클러뷰 notifysetchange
                                                                        // 아직 구현 미완
                                                                        mainFriendsMyRequestFragment.MRL.add(joinFriend)

                                                                        // TODO ALert 생성
                                                                        AlertRepository.getAlertIdx {
                                                                            var alertIdx = it.result.value as Long
                                                                            alertIdx ++

                                                                            var alertContent = MyApplication.loginedUserInfo.userName + " 님이 친구 요청을 보냈습니다"
                                                                            var alertReceiverIdx = receiverIdx
                                                                            var alertType : Long = 0

                                                                            var alert = AlertClass(alertIdx, alertContent, alertReceiverIdx, alertType)

                                                                            AlertRepository.addAlertInfo(alert){
                                                                                Toast.makeText(
                                                                                    mainActivity,
                                                                                    "친구 요청이 완료 되었습니다!",
                                                                                    Toast.LENGTH_SHORT
                                                                                ).show()
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if(isExistUser == false){
                                            Toast.makeText(
                                                mainActivity,
                                                "존재하지 않는 유저입니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }

                            builder.setNegativeButton("취소", null)
                            builder.show()
                        }
                    }
                    true
                }
            }

            // 탭레이아웃
            fragmentList.add(mainFriendsListFragment)
            fragmentList.add(mainFriendsRequestFragment)
            fragmentList.add(mainFriendsMyRequestFragment)

            viewPagerMainFriends.adapter = TabAdapterClass(mainActivity)
            val tabLayoutMediator =
                TabLayoutMediator(
                    tabsMainFriends,
                    viewPagerMainFriends
                ) { tab: TabLayout.Tab, i: Int ->
                    tab.text = tabName[i]
                }
            tabLayoutMediator.attach()

        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.run {
            viewPagerMainFriends.requestLayout()
        }
    }

    // 탭레이아웃 어댑터
    inner class TabAdapterClass(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return fragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

    }

}