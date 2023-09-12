package com.test.dontforgetproject.UI.MainMyPageFragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.DialogMypageLogoutBinding
import com.test.dontforgetproject.databinding.DialogMypageWithdrawBinding
import com.test.dontforgetproject.databinding.FragmentMainMyPageBinding


class MainMyPageFragment : Fragment() {
    lateinit var fragmentMainMyPageBinding: FragmentMainMyPageBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMainMyPageBinding = FragmentMainMyPageBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentMainMyPageBinding.run {
            toolbarMainMyPage.run {
                setTitle(getString(R.string.myPage))
            }
            textViewMainMyPageEmail.text = MyApplication.loginedUserInfo.userEmail
            textViewMainMyPageIntroduce.text = MyApplication.loginedUserInfo.userIntroduce
            textViewMainMyPageName.text = MyApplication.loginedUserInfo.userName
            cardViewMainMyPageModify.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MY_PAGE_MODIFY_FRAGMENT,true,null)
            }
            cardViewMainMyPageTheme.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MY_PAGE_THEME_FRAGMENT,true,null)
            }
            cardViewMainMyPageLogout.setOnClickListener {
                val dialogMypageLogoutBinding = DialogMypageLogoutBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)
                builder.setView(dialogMypageLogoutBinding.root)
                builder.setPositiveButton("로그아웃") { dialog, which ->
                    val sharedPreferences = requireActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("isLoggedIn")
                    editor.remove("isLoggedUser")
                    editor.apply()
                    mainActivity.replaceFragment(MainActivity.LOGIN_FRAGMENT,true,null)
                }
                builder.setNegativeButton("취소",null)

                builder.show()

            }
            cardViewMainMyPageWithDraw.setOnClickListener {
                val dialogMypageWithdrawBinding = DialogMypageWithdrawBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)
                builder.setView(dialogMypageWithdrawBinding.root)
                builder.setNegativeButton("회원탈퇴") { dialog, which ->
                    mainActivity.replaceFragment(MainActivity.LOGIN_FRAGMENT,true,null)
                }
                builder.setPositiveButton("취소",null)
                builder.show()

            }
        }

        return fragmentMainMyPageBinding.root
    }

}