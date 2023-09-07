package com.test.dontforgetproject.UI.TodoAddFragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.DialogTodoAddBinding
import com.test.dontforgetproject.databinding.RowDialogTodoAddBinding

class TodoAddBottomDialog:BottomSheetDialogFragment() {

    lateinit var dialogTodoAddBinding: DialogTodoAddBinding
    lateinit var mainActivity: MainActivity

    // 가짜 데이터
    var list = listOf<String>(
        "개인","6조 프로젝트","프로젝트1","프로젝트2"
    )


    var name:String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        dialogTodoAddBinding = DialogTodoAddBinding.inflate(layoutInflater)

        name = arguments?.getString("category","개인")!!


        dialogTodoAddBinding.run {

            recyclerviewTodoAddBottom.run {
                adapter = allrecyclerviewAdapter()
                layoutManager = LinearLayoutManager(context)
            }
        }
        return dialogTodoAddBinding.root
    }

    inner class allrecyclerviewAdapter:RecyclerView.Adapter<allrecyclerviewAdapter.allviewholder>(){
        inner class allviewholder(rowDialogTodoAddBinding: RowDialogTodoAddBinding):RecyclerView.ViewHolder(rowDialogTodoAddBinding.root){

            var textOne:CheckedTextView

            init {
                textOne = rowDialogTodoAddBinding.textViewToaddRowBottom
                rowDialogTodoAddBinding.textViewToaddRowBottom.setOnClickListener {
                    //다이어로그 종료시 사용자가 선택한 이름을 번들에 저장
                    var bundle = Bundle()
                    bundle.putString("name","${list.get(adapterPosition)}")
                    Log.d("Lim names2","${list.get(adapterPosition)}")
                    Toast.makeText(mainActivity,"선택한 카테고리는 ${list.get(adapterPosition)}입니다",Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): allviewholder {
            val rowPostListBinding = RowDialogTodoAddBinding.inflate(layoutInflater)
            val allViewHolder = allviewholder(rowPostListBinding)

            rowPostListBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return allViewHolder
        }

        override fun getItemCount(): Int {
           return list.size
        }

        override fun onBindViewHolder(holder: allviewholder, position: Int) {
            holder.textOne.text = list.get(position)
            //bundle에서 가져온 데이터가 리사이클러뷰 데이터와 일치시 체크마크 표시 및 색상 표시
            if(holder.textOne.text== name){
               holder.textOne.setCheckMarkDrawable(R.drawable.ic_check_24px)
                holder.textOne.setTextColor(Color.parseColor("#7A97FF"))
                holder.textOne.isChecked = true
            }else{
                holder.textOne.setTextColor(Color.parseColor("#FF000000"))
            }
        }
    }

}