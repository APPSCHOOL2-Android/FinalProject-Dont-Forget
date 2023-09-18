package com.test.dontforgetproject.UI.TodoDetailPublicFragment

import android.content.res.ColorStateList
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
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.UI.TodoDetailPersonalFragment.TodoDetailPersonalViewModel
import com.test.dontforgetproject.databinding.FragmentTodoDetailPublicBinding

class TodoDetailPublicFragment : Fragment() {

    lateinit var fragmentTodoDetailPublicBinding: FragmentTodoDetailPublicBinding
    lateinit var mainActivity: MainActivity

    lateinit var todoDetailPersonalViewModel: TodoDetailPersonalViewModel

    var todoIdx = 0L

    var time = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentTodoDetailPublicBinding = FragmentTodoDetailPublicBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        todoIdx = arguments?.getLong("todoIdx",0)!!

        todoDetailPersonalViewModel = ViewModelProvider(mainActivity)[TodoDetailPersonalViewModel::class.java]
        todoDetailPersonalViewModel.run {

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
                var alertTime = it.toString().split(":")
                if(alertTime.get(0).toInt()>=12) {
                    var hours = alertTime.get(0).toInt()-12
                    fragmentTodoDetailPublicBinding.textViewTodoDetailPublicAlert.text = "오후 ${hours}시 ${alertTime.get(1)}분"
                } else {
                    fragmentTodoDetailPublicBinding.textViewTodoDetailPublicAlert.text = "오전 ${alertTime.get(0)}시 ${alertTime.get(1)}분"
                }
            }
            todoLocationName.observe(mainActivity) {
                fragmentTodoDetailPublicBinding.textViewTodoDetailPublicLocation.text = it.toString().split("@").get(0)
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
        todoDetailPersonalViewModel.getTodoInfo(todoIdx)

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