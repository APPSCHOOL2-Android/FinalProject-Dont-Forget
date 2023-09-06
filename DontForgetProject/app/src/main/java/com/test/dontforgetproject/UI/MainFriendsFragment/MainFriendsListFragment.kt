package com.test.dontforgetproject.UI.MainFriendsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentMainFriendsListBinding
import com.test.dontforgetproject.databinding.RowMainFriendsListBinding

class MainFriendsListFragment : Fragment() {

    lateinit var binding : FragmentMainFriendsListBinding
    lateinit var mainActivity : MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainFriendsListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run{

            // 검색창
            searchViewMainFriendsList.run{
                queryHint = "친구 검색"
            }
            
            // 친구 목록
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
    }

    inner class RecyclerAdapterFL : RecyclerView.Adapter<RecyclerAdapterFL.ViewHolderFL>(){
        inner class ViewHolderFL(rowMainFriendsBinding : RowMainFriendsListBinding) : RecyclerView.ViewHolder(rowMainFriendsBinding.root){
            var textViewRowMainFriendsName : TextView
            
            init{
                textViewRowMainFriendsName = rowMainFriendsBinding.textViewRowMainFriendsName
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
           return 3
        }

        override fun onBindViewHolder(holder: ViewHolderFL, position: Int) {
            holder.textViewRowMainFriendsName.text = "사람이름"
        }
    }
}