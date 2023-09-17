package com.test.dontforgetproject.UI.FriendsDetailFragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.dontforgetproject.DAO.CategoryClass
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.databinding.DialogFriendsDetailBinding
import com.test.dontforgetproject.databinding.DialogFriendsDetailDeleteBinding
import com.test.dontforgetproject.databinding.FragmentFriendsDetailBinding
import com.test.dontforgetproject.databinding.RowFriendsDetailBinding

class FriendsDetailFragment : Fragment() {
    lateinit var binding: FragmentFriendsDetailBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: FriendsDetailViewModel

    // 친구 이메일
    lateinit var _FEmail : String
    lateinit var _FImage : String
    lateinit var _FIntroduce : String
    lateinit var _FId : String
    lateinit var _FFL : ArrayList<Friend>

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
            friendUserImage.observe(mainActivity) {
                _FImage = it
                // 프로필 사진
                UserRepository.getProfile(it) {
                    if (it.isSuccessful) {
                        val fileUri = it.result
                        Glide.with(mainActivity).load(fileUri).into(binding.imageViewFriendsDetail)
                    }
                    else{
//                        Glide.with(mainActivity).load(R.drawable.ic_person_24px).into(binding.imageViewFriendsDetail)
                        binding.imageViewFriendsDetail.setImageResource(R.drawable.ic_person_24px)
                    }
                }
            }
            friendUserId.observe(mainActivity){
                _FId = it
            }
            friendUserFriendList.observe(mainActivity){
                _FFL = it as ArrayList<Friend>
            }
            categoryList.observe(mainActivity) {
                MCL.clear()
                var tempList = it
                // 데이터 정제, 공유 카테고리만 골라내기
                for ((index, category) in tempList.withIndex()) {
                    if (category.categoryJoinUserIdxList?.contains(MyApplication.loginedUserInfo.userIdx)!! && category.categoryJoinUserIdxList?.contains(MyApplication.chosedFriendIdx)!!) {
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
//                addItemDecoration(MaterialDividerItemDecoration(context, MaterialDividerItemDecoration.VERTICAL))
            }

            // 친구삭제
            buttonFriendsDetailDelete.setOnClickListener {
                // 공유 카테고리가 있으면
                if (MCL.size > 0) {
                    var dialogFriendsDetailBinding =
                        DialogFriendsDetailBinding.inflate(layoutInflater)
                    val builder = MaterialAlertDialogBuilder(mainActivity)
                    builder.setView(dialogFriendsDetailBinding.root)
                    builder.setPositiveButton("확인", null)
                    builder.show()
                }
                // 공유 카테고리가 없으면
                else {
                    var dialogFriendsDetailDeleteBinding =
                        DialogFriendsDetailDeleteBinding.inflate(layoutInflater)
                    val builder = MaterialAlertDialogBuilder(mainActivity)
                    builder.setView(dialogFriendsDetailDeleteBinding.root)
                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("삭제") { dialogInterface: DialogInterface, i: Int ->
                        // Todo
                        // 1. MyApplication userInfo 에서 친구삭제
                        // 2. modify 해서 내info, 삭제된 친구info 저장
                        // 3. 화면 종료

                        /* 1. */
                        var temp = MyApplication.loginedUserInfo.userFriendList
                        for ((i, v) in temp.withIndex()) {
                            if (MyApplication.chosedFriendIdx == v.friendIdx) {
                                MyApplication.loginedUserInfo.userFriendList.removeAt(i)
                            }
                        }

                        /* 2. */
                        // 내정보
                        var MIdx = MyApplication.loginedUserInfo.userIdx
                        var MName = MyApplication.loginedUserInfo.userName
                        var MEmail = MyApplication.loginedUserInfo.userEmail

                        var MInfo = MyApplication.loginedUserInfo


                        // 친구정보
                        var FIdx = MyApplication.chosedFriendIdx
                        var FName = MyApplication.chosedFriendName
                        var FEmail = _FEmail

                        var FInfo = UserClass(
                            FIdx,
                            FName,
                            FEmail,
                            _FImage,
                            _FIntroduce,
                            _FId,
                            _FFL
                        )

                        // 친구 목록에서 내정보 삭제
                        for((i,v) in _FFL.withIndex()){
                            if(v.friendIdx == MIdx){
                                FInfo.userFriendList.removeAt(i)
                            }
                        }

                        // modify
                        UserRepository.modifyUserInfo(MInfo){}
                        UserRepository.modifyUserInfo(FInfo){}

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
                }
                else{
//                    Glide.with(mainActivity).load(R.drawable.ic_person_24px).into(binding.imageViewFriendsDetail)
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