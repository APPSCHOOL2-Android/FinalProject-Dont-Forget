package com.test.dontforgetproject.UI.MainFriendsFragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.DAO.JoinFriend
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.Repository.JoinFriendRepository
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.databinding.DialogCategoryNormalBinding
import com.test.dontforgetproject.databinding.DialogMainFriendsRequestDenyBinding
import com.test.dontforgetproject.databinding.DialogNormalBinding
import com.test.dontforgetproject.databinding.FragmentMainFriendsRequestBinding
import com.test.dontforgetproject.databinding.RowMainFriendsRequestBinding

class MainFriendsRequestFragment : Fragment() {
    lateinit var binding: FragmentMainFriendsRequestBinding
    lateinit var mainActivity: MainActivity
    lateinit var mainFriendsListFragment: MainFriendsListFragment

    lateinit var viewModel: MainFriendsViewModel

    var requestList = mutableListOf<JoinFriend>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainFriendsRequestBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        mainFriendsListFragment = MainFriendsListFragment()

        viewModel = ViewModelProvider(mainActivity)[MainFriendsViewModel::class.java]
        viewModel.run {
            joinFriendList.observe(mainActivity) {
                requestList = it
                binding.recyclerMainFriendsRequest.adapter?.notifyDataSetChanged()

                if(requestList.size == 0) {
                    binding.run {
                        textViewMainFriendRequestZero.visibility = View.VISIBLE
                    }
                } else {
                    binding.run {
                        textViewMainFriendRequestZero.visibility = View.GONE
                    }
                }
            }
        }
        viewModel.getRequestList(MyApplication.loginedUserInfo.userEmail)

        binding.run {
            // 드래그시 새로고침
            swipeMainFriendsRequest.setOnRefreshListener {
                binding.swipeMainFriendsRequest.isRefreshing = false

                viewModel.getRequestList(MyApplication.loginedUserInfo.userEmail)
                viewModel.run {
                    joinFriendList.observe(mainActivity) {
                        requestList = it
                        binding.recyclerMainFriendsRequest.adapter?.notifyDataSetChanged()

                        if(requestList.size == 0) {
                            binding.run {
                                textViewMainFriendRequestZero.visibility = View.VISIBLE
                            }
                        } else {
                            binding.run {
                                textViewMainFriendRequestZero.visibility = View.GONE
                            }
                        }
                    }
                }
            }

            // 리싸이클러
            recyclerMainFriendsRequest.run {
                adapter = RecyclerAdapterFR()
                layoutManager = LinearLayoutManager(mainActivity)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
        viewModel.getRequestList(MyApplication.loginedUserInfo.userEmail)
        viewModel.run {
            joinFriendList.observe(mainActivity) {
                requestList = it
                binding.recyclerMainFriendsRequest.adapter?.notifyDataSetChanged()
            }
        }
        binding.recyclerMainFriendsRequest.adapter?.notifyDataSetChanged()
    }

    inner class RecyclerAdapterFR : RecyclerView.Adapter<RecyclerAdapterFR.ViewHolderFR>() {
        inner class ViewHolderFR(rowMainFriendsRequestBinding: RowMainFriendsRequestBinding) :
            RecyclerView.ViewHolder(rowMainFriendsRequestBinding.root) {
            val textViewRowMainFriendsRequestName: TextView
            val buttonRowMainFriendsRequestAccept: TextView
            val buttonRowMainFriendsRequestDeny: TextView

            init {
                textViewRowMainFriendsRequestName =
                    rowMainFriendsRequestBinding.textViewRowMainFriendsRequestName
                buttonRowMainFriendsRequestAccept =
                    rowMainFriendsRequestBinding.buttonRowMainFriendsRequestAccept
                buttonRowMainFriendsRequestDeny =
                    rowMainFriendsRequestBinding.buttonRowMainFriendsRequestDeny
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFR {
            val rowMainFriendsRequestBinding = RowMainFriendsRequestBinding.inflate(layoutInflater)
            val viewHolderFR = ViewHolderFR(rowMainFriendsRequestBinding)

            // 가로부분 꽉차게
            rowMainFriendsRequestBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return viewHolderFR
        }

        override fun getItemCount(): Int {
            return requestList.size
        }

        override fun onBindViewHolder(holder: ViewHolderFR, position: Int) {
            // 친구이름
            holder.textViewRowMainFriendsRequestName.text =
                requestList[position].joinFriendSenderName

            // 수락
            holder.buttonRowMainFriendsRequestAccept.setOnClickListener {
                // 수락시, 삭제시 해당 joinFriend 객체 Firebase 에서 삭제
                var deleteIdx = requestList[position].joinFriendIdx
                JoinFriendRepository.deleteJoinFriend(deleteIdx) {
                    requestList.removeAt(position)
                    binding.recyclerMainFriendsRequest.adapter?.notifyDataSetChanged()

                    // 내가 A 나에게 친구 요청 건사람이 B 일때 B->A 를 A 가 승낙할경우 A->B 도 같이 삭제


                    Toast.makeText(mainActivity, "친구추가 완료", Toast.LENGTH_SHORT).show()
                }

                // 수락시 내 친구리스트, 친구의 친구리스트에 각자의 userIdx, userName, userEmail 추가
                // 내 친구 리스트
                var newFriendIdx = requestList[position].joinFriendSenderIdx
                var newFriendName = requestList[position].joinFriendSenderName
                var newFriendEmail: String

                var myUserIdx = MyApplication.loginedUserInfo.userIdx
                var myUserName = MyApplication.loginedUserInfo.userName
                var myUserEmail = MyApplication.loginedUserInfo.userEmail

                var badum = Friend(myUserIdx, myUserName, myUserEmail)

                // 김받음(나) 에 김보냄(친구) 추가
                JoinFriendRepository.getUserInfoByIdx(newFriendIdx) {
                    for (c1 in it.result.children) {
                        newFriendEmail = c1.child("userEmail").value as String

                        var bonem = Friend(newFriendIdx, newFriendName, newFriendEmail)

                        // 김받음(나) 전체 데이터 호출
                        JoinFriendRepository.getUserInfoByIdx(myUserIdx) {
                            for (c1 in it.result.children) {
                                var userIdx = c1.child("userIdx").value as Long
                                var userName = c1.child("userName").value as String
                                var userEmail = c1.child("userEmail").value as String
                                var userImage = c1.child("userImage").value as String
                                var userIntroduce = c1.child("userIntroduce").value as String
                                var userId = c1.child("userId").value as String
                                var userFriendList = mutableListOf<Friend>()

                                var userFriendListHashMap =
                                    c1.child("userFriendList").value as ArrayList<HashMap<String, Any>>

                                for (i in userFriendListHashMap) {
                                    var idx = i["friendIdx"] as Long
                                    var name = i["friendName"] as String
                                    var email = i["friendEmail"] as String

                                    var friend = Friend(idx, name, email)
                                    userFriendList.add(friend)
                                }

                                var badumInfo = UserClass(
                                    userIdx,
                                    userName,
                                    userEmail,
                                    userImage,
                                    userIntroduce,
                                    userId,
                                    userFriendList as ArrayList<Friend>
                                )

                                badumInfo.userFriendList.add(bonem)

                                UserRepository.modifyUserInfo(badumInfo){
                                    Toast.makeText(mainActivity, "친구 추가 완료", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }

                // 김보냄(친구) 에 김받음(나) 추가
                JoinFriendRepository.getUserInfoByIdx(newFriendIdx) {
                    for(c1 in it.result.children){
                        var userIdx = c1.child("userIdx").value as Long
                        var userName = c1.child("userName").value as String
                        var userEmail = c1.child("userEmail").value as String
                        var userImage = c1.child("userImage").value as String
                        var userIntroduce = c1.child("userIntroduce").value as String
                        var userId = c1.child("userId").value as String
                        var userFriendList = mutableListOf<Friend>()

                        var userFriendListHashMap =
                            c1.child("userFriendList").value as ArrayList<HashMap<String, Any>>

                        for (i in userFriendListHashMap) {
                            var idx = i["friendIdx"] as Long
                            var name = i["friendName"] as String
                            var email = i["friendEmail"] as String

                            var friend = Friend(idx, name, email)
                            userFriendList.add(friend)
                        }

                        var bonemInfo = UserClass(
                            userIdx,
                            userName,
                            userEmail,
                            userImage,
                            userIntroduce,
                            userId,
                            userFriendList as ArrayList<Friend>
                        )

                        bonemInfo.userFriendList.add(badum)

                        UserRepository.modifyUserInfo(bonemInfo){
                            Toast.makeText(mainActivity, "친구 추가 완료", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
            // 삭제
            holder.buttonRowMainFriendsRequestDeny.setOnClickListener {
//                var dialogbinding = DialogMainFriendsRequestDenyBinding.inflate(layoutInflater)
//                val builder = MaterialAlertDialogBuilder(mainActivity)
//                builder.setView(dialogbinding.root)
                val dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)

                dialogNormalBinding.textViewDialogNormalTitle.text = "친구 요청 거절"
                dialogNormalBinding.textViewDialogNormalContent.text = "친구 요청을 삭제합니다."

                builder.setView(dialogNormalBinding.root)
                builder.setPositiveButton("삭제") { dialogInterface: DialogInterface, i: Int ->
                    true
                    // 수락시, 삭제시 해당 joinFriend 객체 Firebase 에서 삭제
                    var deleteIdx = requestList[position].joinFriendIdx
                    JoinFriendRepository.deleteJoinFriend(deleteIdx) {
                        requestList.removeAt(position)
                        binding.recyclerMainFriendsRequest.adapter?.notifyDataSetChanged()
                        Toast.makeText(mainActivity, "친구 요청을 삭제합니다", Toast.LENGTH_SHORT).show()
                    }
                }
                builder.setNegativeButton("취소", null)
                builder.show()
            }
        }
    }
}
