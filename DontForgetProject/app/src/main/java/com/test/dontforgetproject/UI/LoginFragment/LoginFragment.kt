package com.test.dontforgetproject.UI.LoginFragment

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
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
import com.test.dontforgetproject.databinding.FragmentLoginBinding
import com.test.dontforgetproject.databinding.FragmentMainBinding
import kotlin.math.log


class LoginFragment : Fragment() {
    lateinit var fragmentLoginBinding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity
    lateinit var firebaseAuth:FirebaseAuth


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
                        textInputLayoutLoginEmail.error = "이메일을 입력해주세요"
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
                    if(pwSize<6){
                        textInputLayoutLoginPassword.error = "비밀번호를 6자리 이상 입력해주세요."
                    }else{
                        textInputLayoutLoginPassword.error = null
                        textInputLayoutLoginPassword.isErrorEnabled = false
                    }
                }
            }
            firebaseAuth = FirebaseAuth.getInstance()
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
                val bundle = Bundle()
                bundle.putInt("UserType", MyApplication.GOOGLE_LOGIN)
                val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, 9001)
                Handler(Looper.getMainLooper()).postDelayed({
                    //실행할 코드
                }, 1000)

                mainActivity.replaceFragment(MainActivity.JOIN_FRAGMENT,true,bundle)

            }






        }

        return fragmentLoginBinding.root
    }
    fun checkLogin(email:String, password:String){
        fragmentLoginBinding.run {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), OnCompleteListener<AuthResult?> { task ->
                    if(task.isSuccessful){
                        var userId = firebaseAuth.currentUser?.uid
                        if (userId != null) {
                            UserRepository.getUserInfoById(userId){
                                // 가져온 데이터가 없을때
                                if(!it.result.exists()){
                                    val builder = MaterialAlertDialogBuilder(mainActivity)
                                    builder.setTitle("로그인 오류")
                                    builder.setMessage("존재하지 않는 아이디 입니다")
                                    builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                                        textInputEditTextLoginEmail.setText("")
                                        textInputEditTextLoginPassword.setText("")
                                        mainActivity.showSoftInput(
                                            textInputEditTextLoginEmail
                                        )
                                    }
                                    builder.show()
                                }
                                else{
                                    for(c1 in it.result.children){
                                        var userInfo = UserClass(
                                            c1.child("userIdx").value as Long,
                                            c1.child("userName").value as String,
                                            c1.child("userEmail").value as String,
                                            c1.child("userImage").value as String,
                                            c1.child("userIntroduce").value as String,
                                            c1.child("userId").value as String,
                                            c1.child("userFriendList").value as ArrayList<Friend>
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
                    }
                })
        }

        
    }


}