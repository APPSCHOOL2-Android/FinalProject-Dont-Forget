package com.test.dontforgetproject

import android.app.Application
import android.preference.PreferenceManager
import com.test.dontforgetproject.Util.ThemeUtil
import com.test.dontforgetproject.Util.ThemeUtil.applyTheme


class MyApplication :Application(){
    companion object{
        // 분기를 제외한 것

        // 로그인
        var EMAIL_LOGIN = 0
        var GOOGLE_LOGIN = 1

        // 로그인 정보
        var isLogined :Boolean = false
//        var loginedUserInfo = UserInfo(
//            userIdx = null,
//            userLoginType = null,
//            userEmail = null,
//            userPw = null,
//            userNickname = null
//        )

        // theme 설정

    }

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themePref = sharedPreferences.getString("themePref", ThemeUtil.DEFAULT_MODE)

        applyTheme(themePref ?: ThemeUtil.DEFAULT_MODE)
    }




}