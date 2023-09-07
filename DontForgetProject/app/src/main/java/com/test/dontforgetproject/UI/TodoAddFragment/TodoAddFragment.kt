package com.test.dontforgetproject.UI.TodoAddFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.StyleRes
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK

import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentTodoAddBinding
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.Calendar
import java.util.Date


class TodoAddFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var todoAddBinding: FragmentTodoAddBinding
    var date:String =""
    var time:String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        todoAddBinding = FragmentTodoAddBinding.inflate(layoutInflater)

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
                    var bottomDialog = TodoAddBottomDialog()
                    //bundle로 category name 가져옴
                    var bundle = Bundle()
                    bundle.getString("category","${textViewTodoAddCategory}.text}")
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
                        val dateformatter = SimpleDateFormat("yyyy년 MM월 dd일")
                        val dates = dateformatter.format(Date(it))
                        date = dates
                        Toast.makeText(mainActivity,"선택할 날짜는 ${date}입니다",Toast.LENGTH_SHORT).show()
                       textViewTodoAddDate.setText(date)
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
                                time = "${hour}:${minute}"
                                if ("${hour}".toInt()>12){
                                    textViewTodoAddAlert.text=  " 오후 ${hour}시 ${minute}분"
                                    Toast.makeText(mainActivity,"선택한 시간은 오전 ${hour}시 ${minute}분 입니다",Toast.LENGTH_SHORT).show()
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
            buttonTodoAddComplete.run {
                setOnClickListener {
                    mainActivity.removeFragment(MainActivity.TODO_ADD_FRAGMENT)
                }
            }

        }
        return  todoAddBinding.root
    }




}
