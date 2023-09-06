package com.test.dontforgetproject.UI.TodoDetailPublicOwnerFragment

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
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentTodoDetailPublicOwnerBinding

class TodoDetailPublicOwnerFragment : Fragment() {

    lateinit var fragmentTodoDetailPublicOwnerBinding: FragmentTodoDetailPublicOwnerBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentTodoDetailPublicOwnerBinding = FragmentTodoDetailPublicOwnerBinding.inflate(inflater)
        mainActivity = activity as MainActivity

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

            buttonTodoDetailPublicOwnerEdit.setOnClickListener {
                buttonTodoDetailPublicOwnerEdit.visibility = View.GONE
                buttonTodoDetailPublicOwnerEditComplete.visibility = View.VISIBLE
            }

            buttonTodoDetailPublicOwnerEditComplete.setOnClickListener {
                buttonTodoDetailPublicOwnerEdit.visibility = View.VISIBLE
                buttonTodoDetailPublicOwnerEditComplete.visibility = View.GONE
            }
        }
        return fragmentTodoDetailPublicOwnerBinding.root
    }
}