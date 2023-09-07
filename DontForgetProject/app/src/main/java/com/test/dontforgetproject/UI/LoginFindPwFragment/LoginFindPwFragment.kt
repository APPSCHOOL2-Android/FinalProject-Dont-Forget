package com.test.dontforgetproject.UI.LoginFindPwFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentLoginFindPwBinding


class LoginFindPwFragment : Fragment() {
    lateinit var fragmentLoginFindPwBinding: FragmentLoginFindPwBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLoginFindPwBinding = FragmentLoginFindPwBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentLoginFindPwBinding.run {
            toolbarLoginFindPw.run {
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener{
                    mainActivity.removeFragment(MainActivity.LOGIN_FIND_PW_FRAGMENT)
                }
                setTitle("비밀번호 찾기")

            }

            var email = textInputLayoutLoginFindPwEmail.editText?.toString()
            buttonLoginFindPw.setOnClickListener {

            }
        }
        return fragmentLoginFindPwBinding.root
    }


}