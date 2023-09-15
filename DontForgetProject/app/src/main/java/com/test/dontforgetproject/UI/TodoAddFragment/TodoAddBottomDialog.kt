package com.test.dontforgetproject.UI.TodoAddFragment

import android.graphics.Color
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.DialogTodoAddBinding
import com.test.dontforgetproject.databinding.RowDialogTodoAddBinding


class TodoAddBottomDialog:BottomSheetDialogFragment() {

    lateinit var dialogTodoAddBinding: DialogTodoAddBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: TodoAddFragmentViewModel

    var name:String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mainActivity = activity as MainActivity
        dialogTodoAddBinding = DialogTodoAddBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(mainActivity).get(TodoAddFragmentViewModel::class.java)

        //bundle로 받을 데이터
        name = arguments?.getString("category","개인")!!

        dialogTodoAddBinding.run {
            viewModel.getData()
            viewModel.run {
                categoryInfo.observe(mainActivity){
                    dialogTodoAddBinding.recyclerviewTodoAddBottom.adapter?.notifyDataSetChanged()
                }
            }
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
                textOne = rowDialogTodoAddBinding.textViewRowtodoAddBottom
                rowDialogTodoAddBinding.textViewRowtodoAddBottom.setOnClickListener {

                    //다이어로그 종료시 사용자가 선택한 이름을 메인액티비티에 저장
                    var names = viewModel.categoryInfo.value?.get(adapterPosition)?.todoCategoryName.toString()
                    var categoryColors = viewModel.categoryInfo.value?.get(adapterPosition)?.todoBackgroundColor.toString()
                    var fontcolors = viewModel.categoryInfo.value?.get(adapterPosition)?.todoFontColor.toString()

                   MyApplication.categoryname = names
                    MyApplication.categoryColor = categoryColors
                    MyApplication.categoryFontColor = fontcolors

                    saveAction()

                    Toast.makeText(mainActivity,"선택한 카테고리는 ${viewModel.categoryInfo.value?.get(adapterPosition)?.todoCategoryName} 입니다",Toast.LENGTH_SHORT).show()

                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): allrecyclerviewAdapter.allviewholder {
            val rowPostListBinding = RowDialogTodoAddBinding.inflate(layoutInflater)
            val allViewHolder = allviewholder(rowPostListBinding)

            rowPostListBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return allViewHolder
        }

        override fun getItemCount(): Int {
           return viewModel.categoryInfo.value?.size!!
        }

        override fun onBindViewHolder(holder: allviewholder, position: Int) {
            holder.textOne.text = viewModel.categoryInfo.value!!.get(position).todoCategoryName

            //사용자가 선택한 카데고리를 표시
            if(holder.textOne.text == name){
               holder.textOne.setCheckMarkDrawable(R.drawable.ic_check_24px)
                holder.textOne.setTextColor(Color.parseColor("#7A97FF"))
                holder.textOne.isChecked = true
            }

            //리사이클러 데이터 누수방지
            holder.setIsRecyclable(false)
        }


    }

    //카데고리 데이터 변경
    fun saveAction(){
        viewModel.name.value = MyApplication.categoryname.toString()
        viewModel.fontColor.value = MyApplication.categoryFontColor.toLong()
        viewModel.categoryColor.value = MyApplication.categoryColor.toLong()
        dismiss()
    }

}