package com.test.dontforgetproject

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.transition.MaterialSharedAxis
import com.test.dontforgetproject.MainActivity.Companion.LOGIN_FRAGMENT
import com.test.dontforgetproject.UI.CategoryAddPersonalFragment.CategoryAddPersonalFragment
import com.test.dontforgetproject.UI.CategoryAddPublicFragment.CategoryAddPublicFragment
import com.test.dontforgetproject.UI.CategoryOptionPersonalFragment.CategoryOptionPersonalFragment
import com.test.dontforgetproject.UI.CategoryOptionPublicFragment.CategoryOptionPublicFragment
import com.test.dontforgetproject.UI.CategoryOptionPublicOwnerFragment.CategoryOptionPublicOwnerFragment
import com.test.dontforgetproject.UI.FriendsDetailFragment.FriendsDetailFragment
import com.test.dontforgetproject.UI.JoinFragment.JoinFragment
import com.test.dontforgetproject.UI.LoginFindPwFragment.LoginFindPwFragment
import com.test.dontforgetproject.UI.LoginFragment.LoginFragment
import com.test.dontforgetproject.UI.MainAlertFragment.MainAlertFragment
import com.test.dontforgetproject.UI.MainCategoryFragment.MainCategoryFragment
import com.test.dontforgetproject.UI.MainFragment.MainFragment
import com.test.dontforgetproject.UI.MainFriendsFragment.MainFriendsFragment
import com.test.dontforgetproject.UI.MainHomeFragment.MainHomeFragment
import com.test.dontforgetproject.UI.MainMyPageFragment.MainMyPageFragment
import com.test.dontforgetproject.UI.MyPageModifyFragment.MyPageModifyFragment
import com.test.dontforgetproject.UI.MyPageThemeFragment.MyPageThemeFragment
import com.test.dontforgetproject.UI.TodoAddFragment.TodoAddFragment
import com.test.dontforgetproject.UI.TodoAddSearchFragment.TodoAddSearchFragment
import com.test.dontforgetproject.UI.TodoDetailPersonalFragment.TodoDetailPersonalFragment
import com.test.dontforgetproject.UI.TodoDetailPublicFragment.TodoDetailPublicFragment
import com.test.dontforgetproject.UI.TodoDetailPublicOwnerFragment.TodoDetailPublicOwnerFragment
import com.test.dontforgetproject.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
    var newFragment:Fragment? = null
    var oldFragment:Fragment? = null

    //변경된 카데고리 색상,이름
    var categoryname = ""
    var categoryColor = ""
    var categoryFontColor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        replaceFragment(LOGIN_FRAGMENT,false,null)
    }
    companion object{
        // 화면 분기
        val CATEGORY_ADD_PERSONAL_FRAGMENT = "CategoryAddPersonalFragment"
        val CATEGORY_ADD_PUBLIC_FRAGMENT = "CategoryAddPublicFragment"
        val CATEGORY_OPTION_PERSONAL_FRAGMENT = "CategoryOptionPersonalFragment"
        val CATEGORY_OPTION_PUBLIC_FRAGMENT = "CategoryOptionPublicFragment"
        val CATEGORY_OPTION_PUBLIC_OWNER_FRAGMENT = "CategoryOptionPublicOwnerFragment"
        val FRIENDS_DETAIL_FRAGMENT = "FriendsDetailFragment"
        val JOIN_FRAGMENT = "JoinFragment"
        val LOGIN_FIND_PW_FRAGMENT = "LoginFindPwFragment"
        val LOGIN_FRAGMENT = "LoginFragment"
        val MAIN_FRAGMENT = "MainFragment"
        val MY_PAGE_MODIFY_FRAGMENT = "MyPageModifyFragment"
        val MY_PAGE_THEME_FRAGMENT = "MyPageThemeFragment"
        val TODO_ADD_FRAGMENT = "TodoAddFragment"
        val TODO_ADD_SEARCH_FRAGMENT = "TodoAddSearchFragment"
        val TODO_DETAIL_PERSONAL_FRAGMENT = "TodoDetailPersonalFragment"
        val TODO_DETAIL_PUBLIC_FRAGMENT = "TodoDetailPublicFragment"
        val TODO_DETAIL_PUBLIC_OWNER_FRAGMENT = "TodoDetailPublicOwnerFragment"
    }
    fun replaceFragment(name:String, addToBackStack:Boolean, bundle:Bundle?){

        SystemClock.sleep(200)

        // Fragment 교체 상태로 설정한다.
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        // newFragment 에 Fragment가 들어있으면 oldFragment에 넣어준다.
        if(newFragment != null){
            oldFragment = newFragment
        }

        // 새로운 Fragment를 담을 변수
        newFragment = when(name){
            CATEGORY_ADD_PERSONAL_FRAGMENT -> CategoryAddPersonalFragment()
            CATEGORY_ADD_PUBLIC_FRAGMENT -> CategoryAddPublicFragment()
            CATEGORY_OPTION_PERSONAL_FRAGMENT -> CategoryOptionPersonalFragment()
            CATEGORY_OPTION_PUBLIC_FRAGMENT -> CategoryOptionPublicFragment()
            CATEGORY_OPTION_PUBLIC_OWNER_FRAGMENT -> CategoryOptionPublicOwnerFragment()
            FRIENDS_DETAIL_FRAGMENT -> FriendsDetailFragment()
            JOIN_FRAGMENT -> JoinFragment()
            LOGIN_FIND_PW_FRAGMENT -> LoginFindPwFragment()
            LOGIN_FRAGMENT -> LoginFragment()
            MAIN_FRAGMENT -> MainFragment()
            MY_PAGE_MODIFY_FRAGMENT -> MyPageModifyFragment()
            MY_PAGE_THEME_FRAGMENT -> MyPageThemeFragment()
            TODO_ADD_FRAGMENT -> TodoAddFragment()
            TODO_ADD_SEARCH_FRAGMENT -> TodoAddSearchFragment()
            TODO_DETAIL_PERSONAL_FRAGMENT -> TodoDetailPersonalFragment()
            TODO_DETAIL_PUBLIC_FRAGMENT -> TodoDetailPublicFragment()
            TODO_DETAIL_PUBLIC_OWNER_FRAGMENT -> TodoDetailPublicOwnerFragment()
            else -> Fragment()
        }

        newFragment?.arguments = bundle

        if(newFragment != null) {

            // 애니메이션 설정

            if(oldFragment != null){
                oldFragment?.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                oldFragment?.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                oldFragment?.enterTransition = null
                oldFragment?.returnTransition = null
            }

            newFragment?.exitTransition = null
            newFragment?.reenterTransition = null
            newFragment?.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            newFragment?.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

            // Fragment를 교채한다.
            fragmentTransaction.replace(R.id.container_main, newFragment!!)

            if (addToBackStack == true) {
                // Fragment를 Backstack에 넣어 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령이 동작하도록 한다.
            fragmentTransaction.commit()
        }
    }

    // Fragment를 BackStack에서 제거한다.
    fun removeFragment(name:String){
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    // 입력 요소에 포커스를 주는 메서드
    fun showSoftInput(view: View){
        view.requestFocus()

        val inputMethodManger = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        thread {
            SystemClock.sleep(200)
            inputMethodManger.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }
    // 키보드 내리는 메서드
    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }


}