package com.test.dontforgetproject.Util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class FirebaseUtil(var firebaseAuth: FirebaseAuth){

    fun createAccount(email: String?, password: String?, callback: (String) -> Unit) {
        firebaseAuth = FirebaseAuth.getInstance()

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            // 이메일 또는 비밀번호가 빈 문자열이거나 null인 경우 오류 처리
            callback("실패")
            return
        }

        // 올바른 이메일과 비밀번호를 사용하여 계정 생성 코드
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    callback("성공")
                } else {
                    // If sign in fails, display a message to the user.
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

                } else {
                    // If sign in fails, display a message to the user.

                }
            }
    }



}