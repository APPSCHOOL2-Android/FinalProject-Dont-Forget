package com.test.dontforgetproject.UI.TodoDetailPublicOwnerFragment

import android.content.DialogInterface
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.test.dontforgetproject.DAO.TodoClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.TodoRepository
import com.test.dontforgetproject.UI.TodoDetailPersonalFragment.TodoDetailPersonalViewModel
import com.test.dontforgetproject.databinding.FragmentTodoDetailPublicOwnerBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class TodoDetailPublicOwnerFragment : Fragment() {

    lateinit var fragmentTodoDetailPublicOwnerBinding: FragmentTodoDetailPublicOwnerBinding
    lateinit var mainActivity: MainActivity

    lateinit var todoDetailPersonalViewModel: TodoDetailPersonalViewModel

    var todoIdx = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentTodoDetailPublicOwnerBinding = FragmentTodoDetailPublicOwnerBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        todoDetailPersonalViewModel = ViewModelProvider(mainActivity)[TodoDetailPersonalViewModel::class.java]
        todoDetailPersonalViewModel.run {

            todoContent.observe(mainActivity) {
                fragmentTodoDetailPublicOwnerBinding.textInputEditTextTodoDetailPublicOwner.setText(it.toString())
            }
            todoCategoryName.observe(mainActivity) {
                fragmentTodoDetailPublicOwnerBinding.buttonTodoDetailPublicOwnerCategory.text = it.toString()
            }
            todoDate.observe(mainActivity) {
                fragmentTodoDetailPublicOwnerBinding.textViewTodoDetailPublicOwnerDate.text = it.toString()
            }
            todoAlertTime.observe(mainActivity) {
                fragmentTodoDetailPublicOwnerBinding.textViewTodoDetailPublicOwnerAlert.text = it.toString()
            }
            todoLocationName.observe(mainActivity) {
                fragmentTodoDetailPublicOwnerBinding.textViewTodoDetailPublicOwnerLocation.text = it.toString()
            }
        }
        todoDetailPersonalViewModel.getTodoInfo(todoIdx)

        fragmentTodoDetailPublicOwnerBinding.run {

            toolbarTodoDetailPublicOwner.run {
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
                    mainActivity.removeFragment(MainActivity.TODO_DETAIL_PUBLIC_OWNER_FRAGMENT)
                }
            }

            linearLayoutTodoDetailPublicOwnerDate.setOnClickListener {
                val materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
                materialDatePicker.addOnPositiveButtonClickListener {

                    //Show DateFormat
                    val dateformatter = SimpleDateFormat("yyyy-MM-dd")
                    val dates = dateformatter.format(Date(it))

                    textViewTodoDetailPublicOwnerDate.setText(dates)
                }

                materialDatePicker.show(mainActivity.supportFragmentManager,"Date")
            }

            linearLayoutTodoDetailPublicOwnerAlert.setOnClickListener {
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
                                textViewTodoDetailPublicOwnerAlert.text=  " 오후 ${hours}시 ${minute}분"
                            }else{
                                textViewTodoDetailPublicOwnerAlert.text= " 오전 ${hour}시 ${minute}분"
                            }
                        }
                    }
                    .show(mainActivity.supportFragmentManager,"Time")
            }


            buttonTodoDetailPublicOwnerEdit.setOnClickListener {

                var content = textInputEditTextTodoDetailPublicOwner.text.toString()
                var date = textViewTodoDetailPublicOwnerDate.text.toString()
                var time = textViewTodoDetailPublicOwnerAlert.text.toString()
                var locationName = textViewTodoDetailPublicOwnerLocation.text.toString()
                var locationLatitude = ""
                var locationLongitude = ""

                val todoDataClass = TodoClass(
                    todoIdx,
                    content,
                    todoDetailPersonalViewModel.todoIsChecked.value!!.toLong(),
                    todoDetailPersonalViewModel.todoCategoryIdx.value!!.toLong(),
                    todoDetailPersonalViewModel.todoCategoryName.value.toString(),
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

                val builder = MaterialAlertDialogBuilder(mainActivity)
                builder.setTitle("경고")
                builder.setMessage("수정하시면\n공유하고 있는 모든 인원에게\n변경되어 보여집니다.")
                builder.setNegativeButton("취소",null)
                builder.setPositiveButton("수정"){ dialogInterface: DialogInterface, i: Int ->
                    TodoRepository.modifyTodo(todoDataClass) {
                        Snackbar.make(fragmentTodoDetailPublicOwnerBinding.root, "수정이 완료되었습니다.", Snackbar.LENGTH_SHORT)
                        todoDetailPersonalViewModel.getTodoInfo(todoIdx)
                    }
                }
                builder.show()
            }

            buttonTodoDetailPublicOwnerDelete.setOnClickListener {
                val builder = MaterialAlertDialogBuilder(mainActivity)
                builder.setTitle("경고")
                builder.setMessage("삭제하시면\n공유하고 있는 모든 인원에게\n삭제되어 보여지지 않습니다.")
                builder.setNegativeButton("취소",null)
                builder.setPositiveButton("삭제"){ dialogInterface: DialogInterface, i: Int ->
                    TodoRepository.removeTodo(todoIdx) {

                    }
                    Snackbar.make(fragmentTodoDetailPublicOwnerBinding.root, "삭제가 완료되었습니다.", Snackbar.LENGTH_SHORT)
                    mainActivity.removeFragment(MainActivity.TODO_DETAIL_PUBLIC_OWNER_FRAGMENT)
                }
                builder.show()
            }
        }
        return fragmentTodoDetailPublicOwnerBinding.root
    }
}