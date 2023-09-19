package com.test.dontforgetproject.UI.LoginFragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.Util.FirebaseUtil
import com.test.dontforgetproject.databinding.DialogNormalBinding
import com.test.dontforgetproject.databinding.FragmentLoginBinding
import com.test.dontforgetproject.databinding.FragmentMainBinding
import kotlin.math.log


class LoginFragment : Fragment() {
    lateinit var fragmentLoginBinding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity
    lateinit var firebaseAuth:FirebaseAuth
    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var firebaseUtil: FirebaseUtil
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentLoginBinding.run {

            textInputLayoutLoginEmail.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    var emailCheck = textInputLayoutLoginEmail.editText?.text.toString()
                    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                    if (emailCheck.isEmpty()) {
                        textInputLayoutLoginEmail.error = "이메일을 입력해주세요."
                    } else if (!emailCheck.matches(emailPattern.toRegex())) {
                        textInputLayoutLoginEmail.error = "이메일 형식이 잘못되었습니다."
                    } else {
                        textInputLayoutLoginEmail.error = null
                        textInputLayoutLoginEmail.isErrorEnabled = false
                    }
                }
            }
            textInputLayoutLoginPassword.editText?.onFocusChangeListener = View.OnFocusChangeListener{_,hasFocus->
                if(!hasFocus){
                    val pwCheck = textInputLayoutLoginPassword.editText?.text.toString()
                    val pwSize = pwCheck.length
                    val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]*\$".toRegex()
                    if(pwSize<6){
                        textInputLayoutLoginPassword.error = "비밀번호를 6자리 이상 입력해주세요."
                    }else if(!pwCheck.matches(passwordPattern)){
                        textInputLayoutLoginPassword.error = "6~16자의 영문 대/소문자, 숫자를 사용해 주세요."
                    }
                    else{
                        textInputLayoutLoginPassword.error = null
                        textInputLayoutLoginPassword.isErrorEnabled = false
                    }
                }
            }
            firebaseAuth = FirebaseAuth.getInstance()
            firebaseUtil = FirebaseUtil(firebaseAuth)
            // 로그인버튼
            buttonLogin.setOnClickListener {
                var email = textInputLayoutLoginEmail.editText?.text.toString()
                var password = textInputLayoutLoginPassword.editText?.text.toString()
                if(email.isNotEmpty()&&password.isNotEmpty()){
                    checkLogin(email,password)
                }else if ( email.isEmpty()) textInputLayoutLoginEmail.requestFocus()
                else if( password.isEmpty()) textInputLayoutLoginPassword.requestFocus()
            }
            // 가입하기 텍스트
             textViewLoginJoin.setOnClickListener {
                 val bundle = Bundle()
                 bundle.putInt("UserType", MyApplication.EMAIL_LOGIN)
                 mainActivity.replaceFragment(MainActivity.JOIN_FRAGMENT,true,bundle)
             }
            // 비밀번호 찾기 텍스트
            textviewLoginFindPassword.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.LOGIN_FIND_PW_FRAGMENT,true,null)
            }
            buttonLoginGoogleLogin.setOnClickListener {
                firebaseAuth = FirebaseAuth.getInstance()
                val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)) // 웹 클라이언트 ID
                    .requestEmail() // 이메일 권한 요청 (선택 사항)
                    .build()

                val googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, 9001)
            }


        }

        return fragmentLoginBinding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 9001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseUtil.firebaseAuthWithGoogle(account?.idToken!!)
                firebaseAuth.signInWithCustomToken(account.idToken!!).addOnCompleteListener(requireActivity(),
                    OnCompleteListener {
                        if(task.isSuccessful){
                            var userId = firebaseAuth.currentUser?.uid
                            if (userId != null) {
                                UserRepository.getUserInfoById(userId){
                                    if(!it.result.exists()){
                                        val bundle = Bundle()
                                        bundle.putInt("UserType", MyApplication.GOOGLE_LOGIN)
                                        bundle.putString("UserEmail","${account.email}")
                                        mainActivity.replaceFragment(MainActivity.JOIN_FRAGMENT,true,bundle)
                                    }
                                    else{
                                        for(c1 in it.result.children){
                                            var newFriendList = mutableListOf<Friend>()
                                            var newHashMap = c1.child("userFriendList").value as ArrayList<HashMap<String,Any>>
                                            for( i in newHashMap){
                                                var idx = i["friendIdx"] as Long
                                                var name = i["friendName"] as String
                                                var email = i["friendEmail"] as String

                                                var friend = Friend(idx, name, email)
                                                newFriendList.add(friend)
                                            }
                                            var userInfo = UserClass(
                                                c1.child("userIdx").value as Long,
                                                c1.child("userName").value as String,
                                                c1.child("userEmail").value as String,
                                                c1.child("userImage").value as String,
                                                c1.child("userIntroduce").value as String,
                                                c1.child("userId").value as String,
                                                newFriendList as ArrayList<Friend>
                                            )
                                            MyApplication.loginedUserInfo = userInfo
                                            MyApplication.isLogined = true
                                            val sharedPreferences = requireActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
                                            val editor = sharedPreferences.edit()
                                            editor.putBoolean("isLoggedIn", true) // 로그인 상태를 true로 설정
                                            editor.putString("isLoggedUser", userId) // 로그인 상태를 true로 설정
                                            editor.apply()
                                            Snackbar.make(fragmentLoginBinding.root, "로그인 되었습니다", Snackbar.LENGTH_SHORT).show()
                                            mainActivity.removeFragment(MainActivity.LOGIN_FRAGMENT)
                                            mainActivity.replaceFragment(MainActivity.MAIN_FRAGMENT,false,null)
                                        }
                                    }
                                }
                            }
                            else{
                                val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(getString(R.string.default_web_client_id)) // 웹 클라이언트 ID
                                    .requestEmail() // 이메일 권한 요청 (선택 사항)
                                    .build()

                                val googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
                                val signInIntent = googleSignInClient.signInIntent
                                startActivityForResult(signInIntent, 9001)
                            }
                        }

                    })



            } catch (e: ApiException) {
                // Google 로그인 실패
            }
        }

    }

    fun checkLogin(email:String, password:String){
        fragmentLoginBinding.run {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        // 로그인 성공
                        var userId = firebaseAuth.currentUser?.uid
                        if (userId != null) {
                            UserRepository.getUserInfoById(userId) { dataSnapshot ->
                                // 가져온 데이터가 없을 때
                                if (!dataSnapshot.result.exists()) {
                                    var dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
                                    val builder = MaterialAlertDialogBuilder(mainActivity)

                                    dialogNormalBinding.textViewDialogNormalTitle.text = "로그인 오류"
                                    dialogNormalBinding.textViewDialogNormalContent.text = "존재하지 않는 아이디 입니다."

                                    builder.setView(dialogNormalBinding.root)

                                    builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                                        textInputEditTextLoginEmail.setText("")
                                        textInputEditTextLoginPassword.setText("")
                                        mainActivity.showSoftInput(
                                            textInputEditTextLoginEmail
                                        )
                                    }
                                    builder.show()
                                } else {
                                    // 로그인 성공 시 처리
                                    for (c1 in dataSnapshot.result.children) {
                                        var newFriendList = mutableListOf<Friend>()
                                        var newHashMap = c1.child("userFriendList").value as ArrayList<HashMap<String, Any>>
                                        for (i in newHashMap) {
                                            var idx = i["friendIdx"] as Long
                                            var name = i["friendName"] as String
                                            var email = i["friendEmail"] as String

                                            var friend = Friend(idx, name, email)
                                            newFriendList.add(friend)
                                        }
                                        var userInfo = UserClass(
                                            c1.child("userIdx").value as Long,
                                            c1.child("userName").value as String,
                                            c1.child("userEmail").value as String,
                                            c1.child("userImage").value as String,
                                            c1.child("userIntroduce").value as String,
                                            c1.child("userId").value as String,
                                            newFriendList as ArrayList<Friend>
                                        )
                                        MyApplication.loginedUserInfo = userInfo
                                        MyApplication.isLogined = true
                                        val sharedPreferences = requireActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
                                        val editor = sharedPreferences.edit()
                                        editor.putBoolean("isLoggedIn", true) // 로그인 상태를 true로 설정
                                        editor.putString("isLoggedUser", userId) // 로그인 상태를 true로 설정
                                        editor.apply()
                                        Snackbar.make(fragmentLoginBinding.root, "로그인 되었습니다", Snackbar.LENGTH_SHORT).show()
                                        mainActivity.removeFragment(MainActivity.LOGIN_FRAGMENT)
                                        mainActivity.replaceFragment(MainActivity.MAIN_FRAGMENT, false, null)
                                    }
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener { exception ->
                    mainActivity.hideKeyboard()
                    fragmentLoginBinding.textViewLoginError.visibility = View.VISIBLE
                }
        }
    }


}