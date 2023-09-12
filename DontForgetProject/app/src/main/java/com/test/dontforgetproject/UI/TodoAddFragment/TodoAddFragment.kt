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
import com.bumptech.glide.Glide.init
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker

import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
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

//        viewModel.run {
//        }
        todoAddBinding.run {

            //툴바
            toolbarTodoAdd.run {
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.TODO_ADD_FRAGMENT)
                }
                setTitle("할일 추가")
            }

            //카데고리
            linearlayoutTodoAddCategory.run {

                setOnClickListener {
                    //매개변수를 전달하는 메서드 생성
                    //todoAddbottom 생성자를 만들어서 이 fragment를 담아둠
                    //호출
                    var bottomDialog = TodoAddBottomDialog()
                    bottomDialog.show(mainActivity.supportFragmentManager,"카테고리")
                }

            }
            //날짜
            linearlayoutTodoAddDate.run {
                //현재날짜 가져오기
                var now = System.currentTimeMillis()
                var dateformats = SimpleDateFormat("yyyy년 MM월 dd일")
                var dateOne = dateformats.format(now)
                textViewTodoAddDate.setText(dateOne)

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


                    Log.d("Lim todo","${editTextTodoAdd.text}")
                    Log.d("Lim date","${date}")
                    Log.d("Lim time","${time}")

                    //todoList 추가하기
                    todoPlus()

                    //mainActivity.removeFragment(MainActivity.TODO_ADD_FRAGMENT)
                }
            }

        }
        return  todoAddBinding.root
    }

    fun todoPlus(){

    }

}
