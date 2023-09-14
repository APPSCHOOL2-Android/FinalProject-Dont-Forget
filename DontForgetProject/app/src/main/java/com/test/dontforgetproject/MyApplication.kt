package com.test.dontforgetproject

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.Util.ThemeUtil
import com.test.dontforgetproject.Util.ThemeUtil.applyTheme
import java.util.ArrayList


class MyApplication :Application(){
    var mainActivity = MainActivity
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

        // 선택한 친구 인덱스, 친구이름
        var chosedFriendIdx : Long = 0
        var chosedFriendName = ""

        // 테마설정
        var selectedTheme: String = ThemeUtil.DEFAULT_MODE

    }

    override fun onCreate() {
        super.onCreate()
        applyTheme(selectedTheme)
    }




}