package com.test.dontforgetproject.UI.FriendsDetailFragment

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
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.DialogFriendsDetailBinding
import com.test.dontforgetproject.databinding.FragmentFriendsDetailBinding
import com.test.dontforgetproject.databinding.RowFriendsDetailBinding

class FriendsDetailFragment : Fragment() {
    lateinit var binding : FragmentFriendsDetailBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFriendsDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run{
            // 툴바
            toolbarFriendsDetail.run{
                title = "친구"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.FRIENDS_DETAIL_FRAGMENT)
                }
            }

            // 카테고리 리스트
            recyclerFriendsDetail.run{
                adapter = RecyclerAdapterFD()
                layoutManager = LinearLayoutManager(mainActivity)
//                addItemDecoration(MaterialDividerItemDecoration(context, MaterialDividerItemDecoration.VERTICAL))
            }

            // 친구삭제
            buttonFriendsDetailDelete.setOnClickListener {
                // 공유 카테고리가 있으면
                var dialogFriendsDetailBinding = DialogFriendsDetailBinding.inflate(layoutInflater)
                val builder = AlertDialog.Builder(mainActivity)
                builder.setView(dialogFriendsDetailBinding.root)
                builder.setPositiveButton("확인",null)
                builder.show()

                // 공유 카테고리가 없으면
//                mainActivity.removeFragment(MainActivity.FRIENDS_DETAIL_FRAGMENT)
            }
        }

        return binding.root
    }

    inner class RecyclerAdapterFD : RecyclerView.Adapter<RecyclerAdapterFD.ViewHolderFD>(){
        inner class ViewHolderFD(rowFriendsDetailBinding: RowFriendsDetailBinding) : RecyclerView.ViewHolder(rowFriendsDetailBinding.root){
            val textViewRowFriendsDetailCategory : TextView

            init{
                textViewRowFriendsDetailCategory = rowFriendsDetailBinding.textViewRowFriendsDetailCategory
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
            return 15
        }

        override fun onBindViewHolder(holder: ViewHolderFD, position: Int) {
            holder.textViewRowFriendsDetailCategory.text = "카테고리명"
        }
    }
}