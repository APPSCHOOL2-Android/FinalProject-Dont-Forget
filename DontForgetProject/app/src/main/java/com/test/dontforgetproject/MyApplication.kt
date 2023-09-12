package com.test.dontforgetproject

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.Util.ThemeUtil
import com.test.dontforgetproject.Util.ThemeUtil.applyTheme
import java.util.ArrayList


class MyApplication :Application(){
    companion object{
        // 분기를 제외한 것

        // 로그인
        var EMAIL_LOGIN = 1
        var GOOGLE_LOGIN = 2

        // 로그인 정보
        var isLogined :Boolean = false
        var loginedUserInfo = UserClass(
            userIdx = 0,
            userName = "",
            userEmail = "",
            userImage = "None",
            userIntroduce = "",
            userId = "",
            userFriendList = ArrayList<Friend>()
        )
        var userType = 0

        // 테마설정
        var selectedTheme: String = ThemeUtil.DEFAULT_MODE
    }

    override fun onCreate() {
        super.onCreate()

        // SharedPreferences에서 저장된 테마 읽어오기
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        selectedTheme = sharedPreferences.getString("theme", ThemeUtil.DEFAULT_MODE).toString()

        // 테마 설정 적용
        applyTheme(selectedTheme)
    }






}