package com.test.dontforgetproject.UI.TodoAddFragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide.init
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker

import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import com.test.dontforgetproject.DAO.TodoClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.Repository.TodoRepository
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.databinding.FragmentTodoAddBinding
import java.text.SimpleDateFormat
import java.util.Date


class TodoAddFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var todoAddBinding: FragmentTodoAddBinding

    lateinit var viewModel: TodoAddFragmentViewModel

    var date:String =""
    var time:String = ""
    var name :String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        todoAddBinding = FragmentTodoAddBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(mainActivity).get(TodoAddFragmentViewModel::class.java)

        viewModel.run {
            resetList()
            viewModel.name.observe(mainActivity){
                todoAddBinding.textViewTodoAddCategory.text = String.format("%s",it)
            }
            viewModel.categoryColor.observe(mainActivity){
                todoAddBinding.cardviewTodoAddCategory.setCardBackgroundColor(it.toInt())
            }
            viewModel.fontColor.observe(mainActivity){
                todoAddBinding.textViewTodoAddCategory.setTextColor(it.toInt())
            }
        }
        todoAddBinding.run {

            //툴바
            toolbarTodoAdd.run {
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    viewModel.resetList()
                    mainActivity.removeFragment(MainActivity.TODO_ADD_FRAGMENT)
                }
                setTitle("할일 추가")
            }

            //카데고리
            linearlayoutTodoAddCategory.run {

                setOnClickListener {

                    var bottomDialog = TodoAddBottomDialog()
                    var bundle = Bundle()
                    bundle.putString("category","${textViewTodoAddCategory.text}")
                    bottomDialog.arguments = bundle
                    bottomDialog.show(mainActivity.supportFragmentManager,"카테고리")
                }

            }
            //날짜
            linearlayoutTodoAddDate.run {

                setOnClickListener {
                    val materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()
                    materialDatePicker.addOnPositiveButtonClickListener {

                        //Show DateFormat
                        val dateformatter = SimpleDateFormat("yyyy년 MM월 dd일")
                        val dates = dateformatter.format(Date(it))

                        //Send DateFormat
                        val sendDateFormats = SimpleDateFormat("yyyy-MM-dd")
                        val dateOne = sendDateFormats.format(Date(it))
                        date = dateOne

                        Toast.makeText(mainActivity,"선택한 날짜는 ${dates} 입니다",Toast.LENGTH_SHORT).show()
                       textViewTodoAddDate.setText(dates)
                    }

                    materialDatePicker.show(mainActivity.supportFragmentManager,"Date")

                }
            }
            //시간
            linearlayoutTodoAddAlert.run {
                setOnClickListener {
                    var materialTimePicker = MaterialTimePicker.Builder()
                    materialTimePicker
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setTitleText("Select Time")
                        .setHour(12)
                        .setMinute(30)

                        .setInputMode(INPUT_MODE_KEYBOARD)
                        .build().apply {
                            addOnPositiveButtonClickListener {
                                //시간
                                time = "${hour}:${minute}"

                                //시 숫자가 10보다 작을떄
                                if("${hour}".toInt()<10){
                                    time = "0${hour}:${minute}"
                                }
                                //분 숫자가 10보다 작을떄
                                if("${minute}".toInt()<10){
                                    time = "${hour}:0${minute}"
                                }
                                //시,분 숫자가 10보다 작을 떄
                                if("${hour}".toInt()<10 && "${minute}".toInt()<10){
                                    time = "0${hour}:0${minute}"
                                }

                                //오전,오후 분기
                                if ("${hour}".toInt()>=12){
                                    var hours = "${hour}".toInt()-12
                                    textViewTodoAddAlert.text=  " 오후 ${hours}시 ${minute}분"
                                    Toast.makeText(mainActivity,"선택한 시간은 오후 ${hours}시 ${minute}분 입니다",Toast.LENGTH_SHORT).show()
                                }else{
                                    textViewTodoAddAlert.text= " 오전 ${hour}시 ${minute}분"
                                    Toast.makeText(mainActivity,"선택한 시간은 오전 ${hour}시 ${minute}분 입니다",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .show(mainActivity.supportFragmentManager,"Time")

                }
            }
            //위치
            linearlayoutTodoAddLocation.run {
                setOnClickListener {
                    mainActivity.replaceFragment(MainActivity.TODO_ADD_SEARCH_FRAGMENT,true,null)
                }
            }
            //작성하기 button
            buttonTodoAddComplete.run {

                setOnClickListener {
                    //유효성 검사
                    if(editTextTodoAdd.text!!.isEmpty()){
                        val builder= AlertDialog.Builder(mainActivity)
                        builder.setTitle("경고")
                        builder.setMessage("할일을 입력해주세요")
                        builder.setNegativeButton("취소"){ dialogInterface: DialogInterface, i: Int ->

                        }
                        builder.setPositiveButton("확인"){dialogInterface: DialogInterface, i: Int ->
                            mainActivity.showSoftInput(editTextTodoAdd)
                        }
                        builder.show()
                    }
                    if(textViewTodoAddCategory.text == "카데고리 없음"){
                        val builder= AlertDialog.Builder(mainActivity)
                        builder.setTitle("경고")
                        builder.setMessage("카데고리를 선택해주세요")
                        builder.setNegativeButton("취소"){ dialogInterface: DialogInterface, i: Int ->

                        }
                        builder.setPositiveButton("확인"){dialogInterface: DialogInterface, i: Int ->

                        }
                        builder.show()
                    }
                    if(textViewTodoAddDate.text == "날짜 없음"){
                        val builder= AlertDialog.Builder(mainActivity)
                        builder.setTitle("경고")
                        builder.setMessage("날짜를 선택해주세요")
                        builder.setNegativeButton("취소"){ dialogInterface: DialogInterface, i: Int ->

                        }
                        builder.setPositiveButton("확인"){dialogInterface: DialogInterface, i: Int ->

                        }
                        builder.show()
                    }


                    var newDate= date
                    var newTime = time

                    TodoRepository.getTodoIdx {
                        var idx = it.result.value as Long
                        idx++
                        var content = editTextTodoAdd.text.toString()
                        var useridx = MyApplication.loginedUserInfo.userIdx
                        var name = mainActivity.categoryname
                        var backgroundColor = mainActivity.categoryColor
                        var fontColor = mainActivity.categoryFontColor
                        var dates = newDate
                        var time = newTime
                        if(time==""){
                            time = "None"
                        }
                        CategoryRepository.getCategoryInfoByIdx(useridx){

                            for(a1 in it.result.children){
                                var names = a1.child("categoryName").value.toString()
                                if(names == name){
                                    var catgoryIdx = a1.child("categoryIdx").value as Long
                                    var ownerIdx = a1.child("categoryOwnerIdx").value as Long
                                    var ownerName = a1.child("categoryOwnerName").value.toString()
                                    var newclass = TodoClass(idx,content,0,catgoryIdx,name,fontColor.toLong(),backgroundColor.toLong(),
                                        dates,time,"None","None","None",ownerIdx,ownerName)

                                    TodoRepository.setTodoAddInfo(newclass){
                                        TodoRepository.setTodoIdx(idx){
                                            Toast.makeText(mainActivity,"저장되었습니다",Toast.LENGTH_SHORT).show()
                                            mainActivity.removeFragment(MainActivity.TODO_ADD_FRAGMENT)
                                        }
                                    }

                                }
                            }
                        }
                    }

                }
            }

        }
        return  todoAddBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetList()
    }

}
