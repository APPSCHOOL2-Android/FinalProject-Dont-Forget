package com.test.dontforgetproject

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
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

        // 테마설정
        var selectedTheme: String = ThemeUtil.DEFAULT_MODE
    }
    // Application 클래스가 로드될 때 실행되는 초기화 블록


    override fun onCreate() {
        super.onCreate()

        // SharedPreferences에서 저장된 테마 읽어오기
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        selectedTheme = sharedPreferences.getString("theme", ThemeUtil.DEFAULT_MODE).toString()

        // 테마 설정 적용
        applyTheme(selectedTheme)
    }






}