package com.test.dontforgetproject

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
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
            userImage = "",
            userIntroduce = "",
            userId = "",
            userFriendList = ArrayList<Friend>()
        )
        var loginedUserProfile = ""
        // 선택한 친구 인덱스, 친구이름
        var chosedFriendIdx : Long = 0
        var chosedFriendName = ""

        // 테마설정
        var selectedTheme: String = ThemeUtil.DEFAULT_MODE

        //위치 이름,위도,경도
        var locationName=""
        var locationLatitude=""
        var locationLongitude =""

        //저장할 위치 이름
        var locationStoredName = ""

        //변경된 카데고리 색상,이름
        var categoryname = ""
        var categoryColor = ""
        var categoryFontColor = ""

    }




}