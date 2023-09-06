package com.test.dontforgetproject

import android.app.Application


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



    }





}