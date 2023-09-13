package com.test.dontforgetproject.UI.MainFriendsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.dontforgetproject.DAO.JoinFriend
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.JoinFriendRepository
import com.test.dontforgetproject.databinding.FragmentMainFriendsMyRequestBinding
import com.test.dontforgetproject.databinding.RowMainFriendsListBinding
import com.test.dontforgetproject.databinding.RowMainFriendsMyRequestBinding

class MainFriendsMyRequestFragment : Fragment() {
    lateinit var binding : FragmentMainFriendsMyRequestBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel : MainFriendsViewModel
    var MRL = mutableListOf<JoinFriend>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainFriendsMyRequestBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(mainActivity)[MainFriendsViewModel::class.java]
        viewModel.run{
            myRequestList.observe(mainActivity){
                MRL = it
                binding.recyclerMainFriendsMyRequest.adapter?.notifyDataSetChanged()
            }
        }
        viewModel.getMyRequest(MyApplication.loginedUserInfo.userIdx)

        binding.run{
            recyclerMainFriendsMyRequest.run{
                adapter = RecyclerAdapterMR()
                layoutManager = LinearLayoutManager(mainActivity)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
        binding.recyclerMainFriendsMyRequest.adapter?.notifyDataSetChanged()
    }

    inner class RecyclerAdapterMR:RecyclerView.Adapter<RecyclerAdapterMR.ViewHolderMR>(){
        inner class ViewHolderMR(rowMainFriendsMyRequestBinding: RowMainFriendsMyRequestBinding) : RecyclerView.ViewHolder(rowMainFriendsMyRequestBinding.root){
            var textViewRowMainFriendsMyRequestName : TextView
            var textViewRowMainFriendsMyRequestEmail : TextView

            init{
                textViewRowMainFriendsMyRequestName = rowMainFriendsMyRequestBinding.textViewRowMainFriendsMyRequestName
                textViewRowMainFriendsMyRequestEmail = rowMainFriendsMyRequestBinding.textViewRowMainFriendsMyRequestEmail
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMR {
            val rowMainFriendsMyRequestBinding = RowMainFriendsMyRequestBinding.inflate(layoutInflater)
            val viewHolderMR = ViewHolderMR(rowMainFriendsMyRequestBinding)


            // 가로부분 꽉차게
            rowMainFriendsMyRequestBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return viewHolderMR
        }

        override fun getItemCount(): Int {
            return MRL.size
        }

        override fun onBindViewHolder(holder: ViewHolderMR, position: Int) {
            // 상대방 이름
            JoinFriendRepository.getNameByEmail(MRL[position].joinFriendReceiverEmail){
                for(c1 in it.result.children){
                    var userName = c1.child("userName").value as String
                    holder.textViewRowMainFriendsMyRequestName.text = userName
                }
            }

            // 상대방 이메일
            holder.textViewRowMainFriendsMyRequestEmail.text = MRL[position].joinFriendReceiverEmail
        }
    }
}