package com.test.dontforgetproject.Util

import android.content.ContentValues
import android.util.Log
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.test.dontforgetproject.Repository.UserRepository

class FirebaseUtil(var firebaseAuth: FirebaseAuth){

    fun createAccount(email: String?, password: String?, callback: (String) -> Unit) {
        firebaseAuth = FirebaseAuth.getInstance()

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            // 이메일 또는 비밀번호가 빈 문자열이거나 null인 경우 오류 처리
            Log.e("firebase", "${email} , ${password}")
            callback("실패")
            return
        }

        // 올바른 이메일과 비밀번호를 사용하여 계정 생성 코드
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e("firebase", "성공")
                    callback("성공")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e("firebase", "실패: ${task.exception}")
                    callback("${task.exception}")
                }
            }
    }


    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("성공로그", "signInWithCredential:success")

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("실패로그", "signInWithCredential:failure", task.exception)

                }
            }
    }



}