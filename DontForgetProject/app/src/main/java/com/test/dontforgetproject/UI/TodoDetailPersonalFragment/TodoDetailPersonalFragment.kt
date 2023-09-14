package com.test.dontforgetproject.UI.TodoDetailPersonalFragment

import android.content.DialogInterface
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.test.dontforgetproject.DAO.TodoClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MainActivity.Companion.TODO_DETAIL_PERSONAL_FRAGMENT
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.TodoRepository
import com.test.dontforgetproject.databinding.FragmentTodoDetailPersonalBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class TodoDetailPersonalFragment : Fragment() {

    lateinit var fragmentTodoDetailPersonalBinding: FragmentTodoDetailPersonalBinding
    lateinit var mainActivity: MainActivity

    lateinit var todoDetailPersonalViewModel: TodoDetailPersonalViewModel

    var todoIdx = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentTodoDetailPersonalBinding = FragmentTodoDetailPersonalBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        todoDetailPersonalViewModel = ViewModelProvider(mainActivity)[TodoDetailPersonalViewModel::class.java]
        todoDetailPersonalViewModel.run {

            todoContent.observe(mainActivity) {
                fragmentTodoDetailPersonalBinding.textInputEditTextTodoDetailPersonal.setText(it.toString())
            }
            todoCategoryName.observe(mainActivity) {
                fragmentTodoDetailPersonalBinding.buttonTodoDetailPersonalCategory.text = it.toString()
            }
            todoDate.observe(mainActivity) {
                fragmentTodoDetailPersonalBinding.textViewTodoDetailPersonalDate.text = it.toString()
            }
            todoAlertTime.observe(mainActivity) {
                fragmentTodoDetailPersonalBinding.textViewTodoDetailPersonalAlert.text = it.toString()
            }
            todoLocationName.observe(mainActivity) {
                fragmentTodoDetailPersonalBinding.textViewTodoDetailPersonalLocation.text = it.toString()
            }
        }
        todoDetailPersonalViewModel.getTodoInfo(todoIdx)

        fragmentTodoDetailPersonalBinding.run {

            toolbarTodoDetailPersonal.run{
                title = "할일 상세"

                // back 버튼 설정
                setNavigationIcon(R.drawable.ic_arrow_back_24px)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    navigationIcon?.colorFilter =
                        BlendModeColorFilter(Color.DKGRAY, BlendMode.SRC_ATOP)
                } else {
                    navigationIcon?.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP)
                }

                setNavigationOnClickListener {
                    mainActivity.removeFragment(TODO_DETAIL_PERSONAL_FRAGMENT)
                }
            }

            linearLayoutTodoDetailPersonalDate.setOnClickListener {
                val materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
                materialDatePicker.addOnPositiveButtonClickListener {

                    //Show DateFormat
                    val dateformatter = SimpleDateFormat("yyyy-MM-dd")
                    val dates = dateformatter.format(Date(it))

                    textViewTodoDetailPersonalDate.setText(dates)
                }

                materialDatePicker.show(mainActivity.supportFragmentManager,"Date")
            }

            linearLayoutTodoDetailPersonalAlert.setOnClickListener {
                var today = Calendar.getInstance()
                var currentHour = today.get(Calendar.HOUR)
                var currentMinute = today.get(Calendar.MINUTE)
                var materialTimePicker = MaterialTimePicker.Builder()
                materialTimePicker
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setTitleText("Select Time")
                    .setHour(currentHour)
                    .setMinute(currentMinute)
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .build().apply {
                        addOnPositiveButtonClickListener {

                            var time = ""

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
                                textViewTodoDetailPersonalAlert.text=  " 오후 ${hours}시 ${minute}분"
                            }else{
                                textViewTodoDetailPersonalAlert.text= " 오전 ${hour}시 ${minute}분"
                            }
                        }
                    }
                    .show(mainActivity.supportFragmentManager,"Time")
            }

            buttonTodoDetailPersonalEdit.setOnClickListener {
                var content = textInputEditTextTodoDetailPersonal.text.toString()
                var date = textViewTodoDetailPersonalDate.text.toString()
                var time = textViewTodoDetailPersonalAlert.text.toString()
                var locationName = textViewTodoDetailPersonalLocation.text.toString()
                var locationLatitude = ""
                var locationLongitude = ""

                val todoDataClass = TodoClass(
                    todoIdx,
                    content,
                    todoDetailPersonalViewModel.todoIsChecked.value!!.toLong(),
                    todoDetailPersonalViewModel.todoCategoryIdx.value!!.toLong(),
                    todoDetailPersonalViewModel.todoCategoryName.value!!.toString(),
                    todoDetailPersonalViewModel.todoFontColor.value!!.toLong(),
                    todoDetailPersonalViewModel.todoBackgroundColor.value!!.toLong(),
                    date,
                    time,
                    locationName,
                    locationLatitude,
                    locationLongitude,
                    todoDetailPersonalViewModel.todoOwnerIdx.value!!.toLong(),
                    todoDetailPersonalViewModel.todoOwnerName.value!!.toString()
                )

                // 할일 정보 저장
                TodoRepository.modifyTodo(todoDataClass) {

                }

                Snackbar.make(fragmentTodoDetailPersonalBinding.root, "수정이 완료되었습니다.", Snackbar.LENGTH_SHORT)
                todoDetailPersonalViewModel.getTodoInfo(todoIdx)
            }

            buttonTodoDetailPersonalDelete.setOnClickListener {
                val builder = MaterialAlertDialogBuilder(mainActivity)
                builder.setTitle("삭제")
                builder.setMessage("삭제하시겠습니까?")
                builder.setNegativeButton("취소",null)
                builder.setPositiveButton("삭제"){ dialogInterface: DialogInterface, i: Int ->
                    TodoRepository.removeTodo(todoIdx) {

                    }
                    Snackbar.make(fragmentTodoDetailPersonalBinding.root, "삭제가 완료되었습니다.", Snackbar.LENGTH_SHORT)
                    mainActivity.removeFragment(TODO_DETAIL_PERSONAL_FRAGMENT)
                }
                builder.show()
            }
        }

        return fragmentTodoDetailPersonalBinding.root
    }
}