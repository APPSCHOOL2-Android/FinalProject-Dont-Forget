package com.test.dontforgetproject.UI.TodoDetailPersonalFragment

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MainActivity.Companion.TODO_DETAIL_PERSONAL_FRAGMENT
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentTodoDetailPersonalBinding


class TodoDetailPersonalFragment : Fragment() {

    lateinit var fragmentTodoDetailPersonalBinding: FragmentTodoDetailPersonalBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentTodoDetailPersonalBinding = FragmentTodoDetailPersonalBinding.inflate(inflater)
        mainActivity = activity as MainActivity

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

            buttonTodoDetailPersonalEdit.setOnClickListener {

            }

            buttonTodoDetailPersonalDelete.setOnClickListener {
                val builder = MaterialAlertDialogBuilder(mainActivity)
                builder.setTitle("삭제")
                builder.setMessage("삭제하시겠습니까?")
                builder.setNegativeButton("취소",null)
                builder.setPositiveButton("삭제"){ dialogInterface: DialogInterface, i: Int ->
                    mainActivity.removeFragment(TODO_DETAIL_PERSONAL_FRAGMENT)
                }
                builder.show()
            }
        }

        return fragmentTodoDetailPersonalBinding.root
    }
}