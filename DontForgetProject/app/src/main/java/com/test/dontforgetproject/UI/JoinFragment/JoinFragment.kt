package com.test.dontforgetproject.UI.JoinFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.Util.FirebaseUtil
import com.test.dontforgetproject.databinding.FragmentJoinBinding


class JoinFragment : Fragment() {
    lateinit var fragmentJoinBinding: FragmentJoinBinding
    lateinit var mainActivity: MainActivity
    lateinit var firebaseAuth : FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentJoinBinding = FragmentJoinBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentJoinBinding.run {
            toolbarJoin.run {
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener{
                    mainActivity.removeFragment(MainActivity.JOIN_FRAGMENT)
                }
                setTitle("회원가입")
            }
            firebaseAuth = FirebaseAuth.getInstance()
            var firebaseUtil = FirebaseUtil(firebaseAuth)
            var checkBoolean = false
            var userType = arguments?.getInt("UserType")
            // 구글 로그인일 경우
            if(userType == MyApplication.GOOGLE_LOGIN){
                textInputLayoutJoinEmail.visibility = View.GONE
                textInputLayoutJoinPassword.visibility = View.GONE
                textInputLayoutJoinPasswordCheck.visibility = View.GONE
            }
            // 입력 유효성 검사
            textInputLayoutJoinEmail.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    checkBoolean = checkValidation(textInputLayoutJoinEmail,"이메일")
                }
            }
            textInputLayoutJoinPassword.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    checkBoolean = checkValidation(textInputLayoutJoinPassword,"비밀번호")
                }
            }
            textInputLayoutJoinPasswordCheck.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    checkBoolean = checkValidation(textInputLayoutJoinPasswordCheck,"비밀번호 확인")
                }
            }
            textInputLayoutJoinName.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    checkBoolean = checkValidation(textInputLayoutJoinName,"이름")
                }
            }
            textInputLayoutJoinIntroduce.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    checkBoolean = checkValidation(textInputLayoutJoinIntroduce,"자기소개")
                }
            }

            // 회원가입 버튼 클릭
            buttonJoin.setOnClickListener {
                val email = textInputLayoutJoinEmail.editText?.text.toString()
                val password = textInputLayoutJoinPassword.editText?.text.toString()
                val passwordCheck = textInputLayoutJoinPasswordCheck.editText?.text.toString()
                val userIntroduce = textInputLayoutJoinIntroduce.editText?.text.toString()
                val userName = textInputLayoutJoinName.editText?.text.toString()
                val userImage = "None"
                if (checkBoolean && userType == MyApplication.EMAIL_LOGIN) {
                    if (password == passwordCheck) {
                        firebaseUtil.createAccount(email, password) { firebaseCheck ->
                            var currentUser = firebaseAuth.currentUser
                            var userId = currentUser?.uid
                            if (firebaseCheck == "성공") {
                                if (userId != null) {
                                    makeUser(userName,email,userImage,userIntroduce,userId)
                                }
                            }
                            // 이미 등록된 이메일일 경우
                            else if(firebaseCheck == "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account."){
                                if (currentUser != null) {
                                    if(currentUser.providerId == GoogleAuthProvider.PROVIDER_ID){
                                        Toast.makeText(requireContext(),"구글로 이미 등록된 이메일입니다.", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(requireContext(),"이미 등록된 이메일입니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            else Toast.makeText(requireContext(), "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(requireContext(), "비밀번호가 서로 다릅니다.", Toast.LENGTH_SHORT).show()
                    }
                }else if(checkBoolean && userType == MyApplication.GOOGLE_LOGIN){
                    var userId = firebaseAuth.currentUser?.uid
                    if (userId != null) {
                        makeUser(userName,email,userImage,userIntroduce,userId)
                    }
                }
                else if(!checkBoolean){
                    if(email.isEmpty()) textInputLayoutJoinEmail.requestFocus()
                    else if(password.isEmpty()) textInputLayoutJoinPassword.requestFocus()
                    else if(passwordCheck.isEmpty()) textInputLayoutJoinPasswordCheck.requestFocus()
                    else if(userName.isEmpty()) textInputLayoutJoinName.requestFocus()
                    else if(userIntroduce.isEmpty()) textInputLayoutJoinIntroduce.requestFocus()
                }
            }
            buttonJoinPhoto.setOnClickListener {

            }
        }
        return fragmentJoinBinding.root
    }


    // 유효성 검사 함수
    fun checkValidation(textInputLayout: TextInputLayout, type:String):Boolean{
        val check = textInputLayout.editText?.text.toString()
        val checkBoolean = check.isEmpty()

        if(checkBoolean){
            textInputLayout.error = "${type}을 입력해주세요"
        }else{
            if(type == "이메일"){
                val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                if(!check.matches(emailPattern.toRegex())){
                    textInputLayout.error = "이메일 형식이 잘못되었습니다."
                }
                else{
                    textInputLayout.error = null
                    textInputLayout.isErrorEnabled = false
                    return true
                }

            }else if(type == "비밀번호" || type == "비밀번호 확인"){
                if(check.length < 6){
                    textInputLayout.error = "6자리이상 입력해주세요"
                }else{
                    textInputLayout.error = null
                    textInputLayout.isErrorEnabled = false
                    return true
                }
            }
            else{
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
                return true
            }

        }
        return false
    }
    // firebase Realtime에 추가
    fun makeUser(userName:String,userEmail:String,userImage:String,userIntroduce:String,userId:String){
        UserRepository.getUserInfo {
            var userindex = (it.result.value as? Long) ?: 0L
            userindex++
            val userInfo = UserClass(userindex,userName,userEmail,userImage,userIntroduce,userId,null,null)
            UserRepository.setUserInfo(userInfo){
                UserRepository.setUserIdx(userindex){
                    Snackbar.make(fragmentJoinBinding.root, "저장되었습니다", Snackbar.LENGTH_SHORT).show()
                    mainActivity.removeFragment(MainActivity.JOIN_FRAGMENT)
                }
            }
        }
    }


}