package com.test.dontforgetproject.UI.MyPageThemeFragment


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Util.ThemeUtil
import com.test.dontforgetproject.Util.ThemeUtil.applyTheme
import com.test.dontforgetproject.databinding.FragmentMyPageThemeBinding


class MyPageThemeFragment : Fragment() {
    lateinit var fragmentMyPageThemeBinding: FragmentMyPageThemeBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMyPageThemeBinding = FragmentMyPageThemeBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentMyPageThemeBinding.run {
            toolbarMyPageTheme.run {
                setTitle(getString(R.string.myPage_theme))
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MY_PAGE_THEME_FRAGMENT)
                }
            }
            radioGroupMyPageTheme.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId){
                    R.id.radioButton_myPageTheme_white->{
                        ThemeUtil.applyTheme(ThemeUtil.LIGHT_MODE)
                    }
                    R.id.radioButton_myPageTheme_dark->{
                        ThemeUtil.applyTheme(ThemeUtil.DARK_MODE)
                    }
                    R.id.radioButton_myPageTheme_system->{
                        ThemeUtil.applyTheme(ThemeUtil.DEFAULT_MODE)
                    }
                }
            }
        }
        return fragmentMyPageThemeBinding.root
    }

}