package com.test.dontforgetproject.UI.TodoDetailPersonalFragment

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

            buttonTodoDetailPersonalEditComplete.visibility = View.GONE

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
                linearLayoutTodoDetailPersonalEdit.visibility = View.GONE
                buttonTodoDetailPersonalEditComplete.visibility = View.VISIBLE
            }

            buttonTodoDetailPersonalEditComplete.setOnClickListener {
                linearLayoutTodoDetailPersonalEdit.visibility = View.VISIBLE
                buttonTodoDetailPersonalEditComplete.visibility = View.GONE
            }
        }

        return fragmentTodoDetailPersonalBinding.root
    }
}