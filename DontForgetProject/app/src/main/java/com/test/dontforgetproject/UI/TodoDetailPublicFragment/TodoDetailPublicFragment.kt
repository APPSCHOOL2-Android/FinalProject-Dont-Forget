package com.test.dontforgetproject.UI.TodoDetailPublicFragment

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.UI.TodoDetailPersonalFragment.TodoDetailViewModel
import com.test.dontforgetproject.databinding.FragmentTodoDetailPublicBinding

class TodoDetailPublicFragment : Fragment() {

    lateinit var fragmentTodoDetailPublicBinding: FragmentTodoDetailPublicBinding
    lateinit var mainActivity: MainActivity

    lateinit var todoDetailViewModel: TodoDetailViewModel

    var todoIdx = 0L

    var time = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentTodoDetailPublicBinding = FragmentTodoDetailPublicBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        todoIdx = arguments?.getLong("todoIdx",0)!!

        todoDetailViewModel = ViewModelProvider(mainActivity)[TodoDetailViewModel::class.java]
        todoDetailViewModel.run {

            todoContent.observe(mainActivity) {
                fragmentTodoDetailPublicBinding.textInputEditTextTodoDetailPublic.setText(it.toString())
            }
            todoCategoryName.observe(mainActivity) {
                fragmentTodoDetailPublicBinding.buttonTodoDetailPublicCategory.text = it.toString()
            }
            todoDate.observe(mainActivity) {
                fragmentTodoDetailPublicBinding.textViewTodoDetailPublicDate.text = it.toString()
            }
            todoAlertTime.observe(mainActivity) {
                time = it.toString()
                if(time == "알림 없음") {
                    fragmentTodoDetailPublicBinding.textViewTodoDetailPublicAlert.text = "알림 없음"
                } else {
                    var alertTime = it.toString().split(":")
                    if (alertTime.get(0).toInt() >= 12) {
                        var hours = alertTime.get(0).toInt() - 12
                        fragmentTodoDetailPublicBinding.textViewTodoDetailPublicAlert.text =
                            "오후 ${hours}시 ${alertTime.get(1)}분"
                    } else {
                        fragmentTodoDetailPublicBinding.textViewTodoDetailPublicAlert.text =
                            "오전 ${alertTime.get(0)}시 ${alertTime.get(1)}분"
                    }
                }
            }
            todoLocationName.observe(mainActivity) {
                if(it.toString() == "위치 없음") {
                    fragmentTodoDetailPublicBinding.textViewTodoDetailPublicLocation.text = it.toString()
                } else {
                    fragmentTodoDetailPublicBinding.textViewTodoDetailPublicLocation.text =
                        it.toString().split("@").get(0)
                }
            }
            todoOwnerName.observe(mainActivity) {
                fragmentTodoDetailPublicBinding.textViewTodoDetailPublicMadeby.text = it.toString()
            }
            todoFontColor.observe(mainActivity) {
                fragmentTodoDetailPublicBinding.buttonTodoDetailPublicCategory.setTextColor(it.toInt())
            }
            todoBackgroundColor.observe(mainActivity) {
                fragmentTodoDetailPublicBinding.run {
                    buttonTodoDetailPublicCategory.setBackgroundColor(it.toInt())
                    textInputLayoutTodoDetailPublic.boxStrokeColor = it.toInt()
                    textInputLayoutTodoDetailPublic.hintTextColor = ColorStateList.valueOf(it.toInt())
                }
            }
        }
        todoDetailViewModel.getTodoInfo(todoIdx)

        fragmentTodoDetailPublicBinding.run {

            textInputEditTextTodoDetailPublic.isEnabled = false

            toolbarTodoDetailPublic.run{
                title = "할일 상세"

                // back 버튼 설정
                setNavigationIcon(R.drawable.ic_arrow_back_24px)

                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.TODO_DETAIL_PUBLIC_FRAGMENT)
                }
            }
        }
        return fragmentTodoDetailPublicBinding.root
    }
}