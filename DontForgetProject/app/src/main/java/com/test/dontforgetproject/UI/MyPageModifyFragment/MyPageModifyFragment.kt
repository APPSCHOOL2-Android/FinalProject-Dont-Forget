package com.test.dontforgetproject.UI.MyPageModifyFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
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
                setTitle(getString(R.string.myPage_modify))
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MY_PAGE_MODIFY_FRAGMENT)
                }
            }
            var name = ""
            var introduce = ""
            //editTextTextMyPageModifyName.setText(name)
            //editTextTextMyPageModifyIntroduce.setText(introduce)

            // 사진 변경 클릭
            buttonMyPageModifyModifyPhoto.setOnClickListener {

            }
            buttonMyPageModifyModifyComplete.setOnClickListener {
               // var newName = editTextTextMyPageModifyName.text.toString()
                //var newIntroduce = editTextTextMyPageModifyIntroduce.text.toString()

                mainActivity.removeFragment(MainActivity.MY_PAGE_MODIFY_FRAGMENT)
            }
        }

        return fragmentMyPageModifyBinding.root
    }


}