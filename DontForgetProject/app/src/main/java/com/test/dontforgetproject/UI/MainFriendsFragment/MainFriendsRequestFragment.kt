package com.test.dontforgetproject.UI.MainFriendsFragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.databinding.DialogMainFriendsRequestDenyBinding
import com.test.dontforgetproject.databinding.FragmentMainFriendsRequestBinding
import com.test.dontforgetproject.databinding.RowMainFriendsRequestBinding

class MainFriendsRequestFragment : Fragment() {
    lateinit var binding : FragmentMainFriendsRequestBinding
    lateinit var mainActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainFriendsRequestBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run{
            // 리싸이클러
            recyclerMainFriendsRequest.run{
                adapter = RecyclerAdapterFR()
                layoutManager = LinearLayoutManager(mainActivity)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    inner class RecyclerAdapterFR : RecyclerView.Adapter<RecyclerAdapterFR.ViewHolderFR>(){
        inner class ViewHolderFR(rowMainFriendsRequestBinding: RowMainFriendsRequestBinding) : RecyclerView.ViewHolder(rowMainFriendsRequestBinding.root){
            val textViewRowMainFriendsRequestName : TextView
            val buttonRowMainFriendsRequestAccept : TextView
            val buttonRowMainFriendsRequestDeny : TextView

            init{
                textViewRowMainFriendsRequestName = rowMainFriendsRequestBinding.textViewRowMainFriendsRequestName
                buttonRowMainFriendsRequestAccept = rowMainFriendsRequestBinding.buttonRowMainFriendsRequestAccept
                buttonRowMainFriendsRequestDeny = rowMainFriendsRequestBinding.buttonRowMainFriendsRequestDeny
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
            return 10
        }

        override fun onBindViewHolder(holder: ViewHolderFR, position: Int) {
            // 친구이름
            holder.textViewRowMainFriendsRequestName.text = "사람이름"

            // 수락
            holder.buttonRowMainFriendsRequestAccept.setOnClickListener {
                Toast.makeText(mainActivity, "친구 요청이 수락되었습니다", Toast.LENGTH_SHORT).show()
            }

            // 거절
            holder.buttonRowMainFriendsRequestDeny.setOnClickListener {
                var dialogbinding = DialogMainFriendsRequestDenyBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)
                builder.setView(dialogbinding.root)
                builder.setPositiveButton("거절"){ dialogInterface: DialogInterface, i: Int ->
                    true
                    Toast.makeText(mainActivity, "친구 요청이 거절되었습니다", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("취소",null)
                builder.show()
            }
        }
    }
}