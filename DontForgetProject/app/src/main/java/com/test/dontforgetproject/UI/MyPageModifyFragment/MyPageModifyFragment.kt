package com.test.dontforgetproject.UI.MyPageModifyFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.databinding.FragmentMyPageModifyBinding

class MyPageModifyFragment : Fragment() {
    lateinit var fragmentMyPageModifyBinding: FragmentMyPageModifyBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMyPageModifyBinding = FragmentMyPageModifyBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentMyPageModifyBinding.run {
            toolbarMyPageModify.run {
                title = getString(R.string.myPage_modify)
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MY_PAGE_MODIFY_FRAGMENT)
                }
            }
            val user = MyApplication.loginedUserInfo
            val name = user.userName
            val introduce = user.userIntroduce
            textInputEditTextMyPageModifyName.setText(name)
            textInputEditTextMyPageModifyIntroduce.setText(introduce)

            // 사진 변경 클릭
            buttonMyPageModifyModifyPhoto.setOnClickListener {

            }
            buttonMyPageModifyModifyComplete.setOnClickListener {
                val newUser = fragmentMyPageModifyBinding.textInputLayoutMyPageModifyName.editText?.text.toString()
                val newIntroduce = fragmentMyPageModifyBinding.textInputLayoutMyPageModifyIntroduce.editText?.text.toString()

                if (newUser.isNotEmpty() && newIntroduce.isNotEmpty()) {
                    val modifyUser = UserClass(
                        user.userIdx,
                        newUser, // 수정된 사용자 이름
                        user.userEmail,
                        user.userImage,
                        newIntroduce, // 수정된 소개 내용
                        user.userId,
                        user.userFriendList
                    )
                    UserRepository.modifyUserInfo(modifyUser) {
                        if (it.isComplete) {
                            MyApplication.loginedUserInfo = modifyUser
                        } else {
                            Snackbar.make(fragmentMyPageModifyBinding.root, "오류 발생.", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    MyApplication.loginedUserInfo = modifyUser
                    Snackbar.make(fragmentMyPageModifyBinding.root, "수정되었습니다.", Snackbar.LENGTH_SHORT).show()
                    mainActivity.removeFragment(MainActivity.MY_PAGE_MODIFY_FRAGMENT)
                } else {
                    Snackbar.make(fragmentMyPageModifyBinding.root, "빈 칸을 채워주세요.", Snackbar.LENGTH_SHORT).show()
                }
            }

        }

        return fragmentMyPageModifyBinding.root
    }


}