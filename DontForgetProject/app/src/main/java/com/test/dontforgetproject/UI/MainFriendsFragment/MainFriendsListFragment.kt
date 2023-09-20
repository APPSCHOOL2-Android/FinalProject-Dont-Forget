package com.test.dontforgetproject.UI.MainFriendsFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentMainFriendsListBinding
import com.test.dontforgetproject.databinding.RowMainFriendsListBinding

class MainFriendsListFragment : Fragment() {

    lateinit var binding : FragmentMainFriendsListBinding
    lateinit var mainActivity : MainActivity

    lateinit var viewModel: MainFriendsViewModel

    // 친구목록 전체 리스트
    var UFL = mutableListOf<Friend>()

    // 현재 내가 보여줄 리스트
    var searchUFL = mutableListOf<Friend>()
//    var searchUFL = MutableLiveData<MutableList<Friend>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainFriendsListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // 친구목록 다시 불러오기
        viewModel = ViewModelProvider(mainActivity)[MainFriendsViewModel::class.java]
        viewModel.run{
            this.myFriendList.observe(mainActivity){
                UFL = it as ArrayList<Friend>
                UFL.distinctBy { it.friendEmail }
                searchUFL = UFL
                MyApplication.loginedUserInfo.userFriendList = UFL as ArrayList<Friend>

                // 자기자신은 제거
                for((index,friend) in UFL.withIndex()){
                    if(friend.friendIdx == MyApplication.loginedUserInfo.userIdx){
                        searchUFL.removeAt(index)
                    }
                }

                binding.recyclerMainFriendsList.adapter?.notifyDataSetChanged()
            }
        }
        viewModel.getMyFriendListByIdx(MyApplication.loginedUserInfo.userIdx)

        binding.run{
            // 아래로 드래그 새로고침
            swipeMainFriendList.setOnRefreshListener {
                binding.swipeMainFriendList.isRefreshing = false

                viewModel.getMyFriendListByIdx(MyApplication.loginedUserInfo.userIdx)
                viewModel.run{
                    this.myFriendList.observe(mainActivity){
                        UFL = it as ArrayList<Friend>
                        searchUFL = UFL
                        MyApplication.loginedUserInfo.userFriendList

                        // 자기자신은 제거
                        for((index,friend) in searchUFL.withIndex()){
                            if(friend.friendIdx == MyApplication.loginedUserInfo.userIdx){
                                searchUFL.removeAt(index)
                            }
                        }

                        binding.recyclerMainFriendsList.adapter?.notifyDataSetChanged()
                    }
                }
            }

            // 검색창
            searchViewMainFriendsList.run{
                queryHint = "친구이름을 입력해주세요"
                setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        //검색어 입력 순간마다의 이벤트...
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        //키보드에서 검색 버튼을 누르는 순간의 이벤트
                        val resultList = mutableListOf<Friend>()
                        for((i,v) in UFL.withIndex()){
                            if(v.friendName?.contains(query) == true){
                                resultList.add(v)
                            }
                        }
                        searchUFL = resultList
                        binding.recyclerMainFriendsList.adapter?.notifyDataSetChanged()

                        return true
                    }
                })
            }


            recyclerMainFriendsList.run{
                adapter = RecyclerAdapterFL()
                layoutManager = LinearLayoutManager(mainActivity)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d("AAAAAAAAAAAAAAAAAAAAA","onResume")
        viewModel.run{
            this.myFriendList.observe(mainActivity){
                UFL = it as ArrayList<Friend>
                searchUFL = UFL
                MyApplication.loginedUserInfo.userFriendList

                // 자기자신은 제거
                for((index,friend) in searchUFL.withIndex()){
                    if(friend.friendIdx == MyApplication.loginedUserInfo.userIdx){
                        searchUFL.removeAt(index)
                    }
                }

                binding.recyclerMainFriendsList.adapter?.notifyDataSetChanged()
            }
        }

        FirebaseDatabase.getInstance().reference
            .child("userInfo")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("lion", "실시간 탐지 에러 : $p0")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    viewModel.getMyFriendListByIdx(MyApplication.loginedUserInfo.userIdx)
                    viewModel.run{
                        this.myFriendList.observe(mainActivity){
                            UFL = it as ArrayList<Friend>
                            UFL.distinctBy { it.friendEmail }
                            searchUFL = UFL
                            MyApplication.loginedUserInfo.userFriendList = UFL as ArrayList<Friend>

                            // 자기자신은 제거
                            for((index,friend) in UFL.withIndex()){
                                if(friend.friendIdx == MyApplication.loginedUserInfo.userIdx){
                                    searchUFL.removeAt(index)
                                }
                            }

                            binding.recyclerMainFriendsList.adapter?.notifyDataSetChanged()
                        }
                    }
                    Log.d("lion", "실시간 탐지 성공 : $p0")
                }
            })
        binding.root.requestLayout()
    }

    inner class RecyclerAdapterFL : RecyclerView.Adapter<RecyclerAdapterFL.ViewHolderFL>(){
        inner class ViewHolderFL(rowMainFriendsBinding : RowMainFriendsListBinding) : RecyclerView.ViewHolder(rowMainFriendsBinding.root){
            var textViewRowMainFriendsName : TextView
            
            init{
                textViewRowMainFriendsName = rowMainFriendsBinding.textViewRowMainFriendsName

                rowMainFriendsBinding.root.setOnClickListener {
                    MyApplication.chosedFriendIdx = searchUFL[adapterPosition].friendIdx
                    MyApplication.chosedFriendName = searchUFL[adapterPosition].friendName
                    mainActivity.replaceFragment(MainActivity.FRIENDS_DETAIL_FRAGMENT,true,null)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFL {
            val rowMainFriendsBinding = RowMainFriendsListBinding.inflate(layoutInflater)
            val viewHolderFL = ViewHolderFL(rowMainFriendsBinding)

            
            // 가로부분 꽉차게
            rowMainFriendsBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return viewHolderFL
        }

        override fun getItemCount(): Int {
           return searchUFL.size
        }

        override fun onBindViewHolder(holder: ViewHolderFL, position: Int) {
            holder.textViewRowMainFriendsName.text = searchUFL[position].friendName
        }
    }
}