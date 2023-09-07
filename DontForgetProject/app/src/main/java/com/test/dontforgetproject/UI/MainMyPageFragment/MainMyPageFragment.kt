package com.test.dontforgetproject.UI.MainMyPageFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentMainBinding
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
            cardViewMainMyPageModify.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MY_PAGE_MODIFY_FRAGMENT,true,null)
            }
            cardViewMainMyPageTheme.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MY_PAGE_THEME_FRAGMENT,true,null)
            }
            cardViewMainMyPageLogout.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.LOGIN_FRAGMENT,true,null)
            }
            cardViewMainMyPageWithDraw.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.LOGIN_FRAGMENT,true,null)
            }
        }

        return fragmentMainMyPageBinding.root
    }

}