package com.test.dontforgetproject.UI.MainFriendsFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
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

    // 친구목록 리스트
    var UFL = mutableListOf<Friend>()

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
                MyApplication.loginedUserInfo.userFriendList

                // 자기자신은 제거
                for((index,friend) in UFL.withIndex()){
                    if(friend.friendIdx == MyApplication.loginedUserInfo.userIdx){
                        UFL.removeAt(index)
                    }
                }

                binding.recyclerMainFriendsList.adapter?.notifyDataSetChanged()
            }
        }
        viewModel.getMyFriendListByIdx(MyApplication.loginedUserInfo.userIdx)

        binding.run{

            // 검색창
            searchViewMainFriendsList.run{

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
        binding.root.requestLayout()
        binding.recyclerMainFriendsList.adapter?.notifyDataSetChanged()
    }

    inner class RecyclerAdapterFL : RecyclerView.Adapter<RecyclerAdapterFL.ViewHolderFL>(){
        inner class ViewHolderFL(rowMainFriendsBinding : RowMainFriendsListBinding) : RecyclerView.ViewHolder(rowMainFriendsBinding.root){
            var textViewRowMainFriendsName : TextView
            
            init{
                textViewRowMainFriendsName = rowMainFriendsBinding.textViewRowMainFriendsName

                rowMainFriendsBinding.root.setOnClickListener {
                    MyApplication.chosedFriendIdx = UFL[adapterPosition].friendIdx
                    MyApplication.chosedFriendName = UFL[adapterPosition].friendName
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
           return UFL.size
        }

        override fun onBindViewHolder(holder: ViewHolderFL, position: Int) {
            holder.textViewRowMainFriendsName.text = UFL[position].friendName
        }
    }
}