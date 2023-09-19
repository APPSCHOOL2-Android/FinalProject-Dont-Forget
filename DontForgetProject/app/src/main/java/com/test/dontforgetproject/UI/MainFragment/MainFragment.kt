package com.test.dontforgetproject.UI.MainFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.UI.MainAlertFragment.MainAlertFragment
import com.test.dontforgetproject.UI.MainCategoryFragment.MainCategoryFragment
import com.test.dontforgetproject.UI.MainFriendsFragment.MainFriendsFragment
import com.test.dontforgetproject.UI.MainHomeFragment.MainHomeFragment
import com.test.dontforgetproject.UI.MainMyPageFragment.MainMyPageFragment
import com.test.dontforgetproject.databinding.FragmentMainBinding


class MainFragment : Fragment() {
    lateinit var fragmentMainBinding: FragmentMainBinding
    lateinit var mainActivity : MainActivity

    var oldBottom = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMainBinding = FragmentMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentMainBinding.run {
            bottomNavigationViewMainFragment.run {
                selectedItemId = R.id.navigation_home
                setOnItemSelectedListener {
                    when(it.itemId){
                        R.id.navigation_home->{
                            replaceFragment(MAIN_HOME_FRAGMENT,true,false)
                            oldBottom = "home"
                        }
                        R.id.navigation_alert->{
                            replaceFragment(MAIN_ALERT_FRAGMENT,true,false)
                            oldBottom = "alert"
                        }
                        R.id.navigation_category->{
                            replaceFragment(MAIN_CATEGORY_FRAGMENT,true,false)
                            oldBottom = "category"
                        }
                        R.id.navigation_friend->{
                            replaceFragment(MAIN_FRIENDS_FRAGMENT,true,false)
                            oldBottom = "friend"
                        }
                        R.id.navigation_myPage->{
                            replaceFragment(MAIN_MY_PAGE_FRAGMENT,true,false)
                            oldBottom = "myPage"
                        }
                    }
                    true
                }
            }

        }
        return fragmentMainBinding.root
    }
    companion object {
        fun newInstance() = MainFragment()

        val MAIN_ALERT_FRAGMENT = "MainAlertFragment"
        val MAIN_CATEGORY_FRAGMENT = "MainCategoryFragment"
        val MAIN_FRIENDS_FRAGMENT = "MainFriendsFragment"
        val MAIN_HOME_FRAGMENT = "MainHomeFragment"
        val MAIN_MY_PAGE_FRAGMENT = "MainMyPageFragment"

    }
    fun replaceFragment(name: String, addToBackStack: Boolean, animate: Boolean) {
        // Fragment 교체 상태로 설정
        val fragmentTransaction = mainActivity.supportFragmentManager.beginTransaction()

        // 새로운 Fragment를 담을 변수
        var newFragment = when (name) {
            MAIN_ALERT_FRAGMENT -> MainAlertFragment()
            MAIN_CATEGORY_FRAGMENT -> MainCategoryFragment()
            MAIN_FRIENDS_FRAGMENT -> MainFriendsFragment()
            MAIN_HOME_FRAGMENT -> MainHomeFragment()
            MAIN_MY_PAGE_FRAGMENT -> MainMyPageFragment()
            else -> {
                Fragment()
            }
        }

        if (newFragment != null) {
            // Fragment 교체
            fragmentTransaction.replace(R.id.fragmentContainerView_MainFragment, newFragment)

            if (animate == true) {
                // 애니메이션 설정
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            }

            if (addToBackStack == true) {
                // 이전으로 돌아가는 기능 이용하기 위해 Fragment Backstack에 넣어주기
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령 동작
            fragmentTransaction.commit()
        }
    }

    fun removeFragment(name: String) {
        mainActivity.supportFragmentManager.popBackStack(name, 0)
    }
    override fun onResume() {
        super.onResume()
        when(oldBottom) {
            "myPage" -> fragmentMainBinding.bottomNavigationViewMainFragment.selectedItemId = R.id.navigation_myPage
            "category" -> fragmentMainBinding.bottomNavigationViewMainFragment.selectedItemId = R.id.navigation_category
            "friend" -> fragmentMainBinding.bottomNavigationViewMainFragment.selectedItemId = R.id.navigation_friend
            "home" -> fragmentMainBinding.bottomNavigationViewMainFragment.selectedItemId = R.id.navigation_home
            "alert" -> fragmentMainBinding.bottomNavigationViewMainFragment.selectedItemId = R.id.navigation_alert
            else -> fragmentMainBinding.bottomNavigationViewMainFragment.selectedItemId = R.id.navigation_home
        }

    }


}