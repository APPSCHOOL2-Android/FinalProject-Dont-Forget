package com.test.dontforgetproject.UI.FriendsDetailFragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.dontforgetproject.DAO.CategoryClass
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.JoinFriendRepository
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.Util.LoadingDialog
import com.test.dontforgetproject.databinding.DialogNormalBinding
import com.test.dontforgetproject.databinding.FragmentFriendsDetailBinding
import com.test.dontforgetproject.databinding.RowFriendsDetailBinding

class FriendsDetailFragment : Fragment() {
    lateinit var binding: FragmentFriendsDetailBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: FriendsDetailViewModel
    lateinit var loadingDialog: LoadingDialog

    // 친구 이메일
    lateinit var _FName : String
    lateinit var _FEmail: String
    lateinit var _FImage: String
    lateinit var _FIntroduce: String
    lateinit var _FId: String
    lateinit var _FFL: ArrayList<Friend>

    var MCL = mutableListOf<CategoryClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFriendsDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(mainActivity)[FriendsDetailViewModel::class.java]
        viewModel.run {
            friendUserName.observe(mainActivity) {
                _FName = it
                binding.textViewFriendsDetailName.text = it
            }
            friendUserEmail.observe(mainActivity) {
                _FEmail = it
                binding.textViewFriendsDetailEmail.text = it
            }
            friendUserIntroduce.observe(mainActivity) {
                _FIntroduce = it
                binding.textViewFriendsDetailIntroduce.text = it
            }
            friendUserId.observe(mainActivity) {
                _FId = it
            }
            friendUserFriendList.observe(mainActivity) {
                _FFL = it as ArrayList<Friend>
            }
            categoryList.observe(mainActivity) {
                MCL.clear()
                var tempList = it
                // 데이터 정제, 공유 카테고리만 골라내기
                for ((index, category) in tempList.withIndex()) {
                    if (category.categoryJoinUserIdxList?.contains(MyApplication.loginedUserInfo.userIdx)!! && category.categoryJoinUserIdxList?.contains(
                            MyApplication.chosedFriendIdx
                        )!!
                    ) {
                        MCL.add(category)
                    }
                }
                MCL.distinctBy {
                    it.categoryIdx
                }
                binding.recyclerFriendsDetail.adapter?.notifyDataSetChanged()
            }
        }
        viewModel.getFriendUserInfoByIdx(MyApplication.chosedFriendIdx)
        viewModel.getCategoryAll()

        binding.run {
            loadingDialog = LoadingDialog(requireContext())
            loadingDialog.show()
            viewModel.friendUserImage.observe(mainActivity) {
                _FImage = it
                // 프로필 사진
                UserRepository.getProfile(it) {
                    if (it.isSuccessful) {
                        val fileUri = it.result
                        Glide.with(mainActivity).load(fileUri).into(binding.imageViewFriendsDetail)
                        loadingDialog.dismiss()
                    } else {
                        binding.imageViewFriendsDetail.setImageResource(R.drawable.ic_person_24px)
                        loadingDialog.dismiss()
                    }
                }
            }

            // 툴바
            toolbarFriendsDetail.run {
                title = "친구"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.FRIENDS_DETAIL_FRAGMENT)
                }
            }

            // 카테고리 리스트
            recyclerFriendsDetail.run {
                adapter = RecyclerAdapterFD()
                layoutManager = LinearLayoutManager(mainActivity)
            }

            // 친구삭제
            buttonFriendsDetailDelete.setOnClickListener {
                // 공유 카테고리가 있으면
                if (MCL.size > 0) {
                    var dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
                    val builder = MaterialAlertDialogBuilder(mainActivity)

                    dialogNormalBinding.textViewDialogNormalTitle.text = "친구 삭제"
                    dialogNormalBinding.textViewDialogNormalContent.text =
                        "공유 하고 있는 카테고리가 있으면 삭제할 수 없습니다."

                    builder.setView(dialogNormalBinding.root)
                    builder.setPositiveButton("확인", null)
                    builder.show()
                }
                // 공유 카테고리가 없으면
                else {
                    var dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
                    val builder = MaterialAlertDialogBuilder(mainActivity)

                    dialogNormalBinding.textViewDialogNormalTitle.text = "친구 삭제"
                    dialogNormalBinding.textViewDialogNormalContent.text = "친구를 삭제합니다."
                    builder.setView(dialogNormalBinding.root)
                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("삭제") { dialogInterface: DialogInterface, i: Int ->
                        // 김받음(나) -> 김보냄(친구) 삭제

                        var badumIdx = MyApplication.loginedUserInfo.userIdx
                        var badumName = MyApplication.loginedUserInfo.userName
                        var badumEmail = MyApplication.loginedUserInfo.userEmail

                        var bonemIdx = MyApplication.chosedFriendIdx
                        var bonemName = _FName
                        var bonemEmail = _FEmail

                        var badumFriend = Friend(badumIdx, badumName, badumEmail)
                        var bonemFriend = Friend(bonemIdx, bonemName, bonemEmail)

                        // 김받음 정보
                        JoinFriendRepository.getUserInfoByIdx(badumIdx){
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

                                // 김받음에서 김보냄 삭제
                                var temp = badumInfo.userFriendList
                                var buff = ArrayList<Friend>()
                                for(i in temp){
                                    if(i.friendIdx == bonemIdx){
                                        buff.add(i)
                                    }
                                }
                                badumInfo.userFriendList.removeAll(buff)

                                UserRepository.modifyUserInfo(badumInfo){
                                    Toast.makeText(mainActivity, "친구 삭제 완료", Toast.LENGTH_SHORT).show()
                                    MyApplication.loginedUserInfo = badumInfo
                                }
                            }
                        }

                        // 김보냄 정보
                        JoinFriendRepository.getUserInfoByIdx(bonemIdx){
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

                                var bonemInfo = UserClass(
                                    userIdx,
                                    userName,
                                    userEmail,
                                    userImage,
                                    userIntroduce,
                                    userId,
                                    userFriendList as ArrayList<Friend>
                                )

                                // 김보냄(친구)에서 김받음(나) 삭제
                                var temp = bonemInfo.userFriendList
                                var buff = ArrayList<Friend>()
                                for(i in temp){
                                    if(i.friendIdx == badumIdx){
                                        buff.add(i)
                                    }
                                }
                                bonemInfo.userFriendList.removeAll(buff)

                                UserRepository.modifyUserInfo(bonemInfo){
                                    Toast.makeText(mainActivity, "친구 삭제 완료", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        /* 3. */
                        mainActivity.removeFragment(MainActivity.FRIENDS_DETAIL_FRAGMENT)
                    }
                    builder.show()
                }

            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.friendUserImage.observe(mainActivity) {
            _FImage = it
            // 프로필 사진
            UserRepository.getProfile(it) {
                if (it.isSuccessful) {
                    val fileUri = it.result
                    Glide.with(mainActivity).load(fileUri).into(binding.imageViewFriendsDetail)
                } else {
                    binding.imageViewFriendsDetail.setImageResource(R.drawable.ic_person_24px)
                }
            }
        }
    }


    inner class RecyclerAdapterFD : RecyclerView.Adapter<RecyclerAdapterFD.ViewHolderFD>() {
        inner class ViewHolderFD(rowFriendsDetailBinding: RowFriendsDetailBinding) :
            RecyclerView.ViewHolder(rowFriendsDetailBinding.root) {
            val textViewRowFriendsDetailCategory: TextView

            init {
                textViewRowFriendsDetailCategory =
                    rowFriendsDetailBinding.textViewRowFriendsDetailCategory
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFD {
            val rowFriendsDetailBinding = RowFriendsDetailBinding.inflate(layoutInflater)
            val viewHolderFD = ViewHolderFD(rowFriendsDetailBinding)


            // 가로부분 꽉차게
            rowFriendsDetailBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return viewHolderFD
        }

        override fun getItemCount(): Int {
            return MCL.size
        }

        override fun onBindViewHolder(holder: ViewHolderFD, position: Int) {
            holder.textViewRowFriendsDetailCategory.text = MCL[position].categoryName
            holder.textViewRowFriendsDetailCategory.setTextColor(MCL[position].categoryColor.toInt())
        }
    }
}