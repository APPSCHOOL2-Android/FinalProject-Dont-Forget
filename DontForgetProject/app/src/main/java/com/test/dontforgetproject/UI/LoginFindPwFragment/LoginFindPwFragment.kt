package com.test.dontforgetproject.UI.LoginFindPwFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentLoginFindPwBinding


class LoginFindPwFragment : Fragment() {
    lateinit var fragmentLoginFindPwBinding: FragmentLoginFindPwBinding
    lateinit var mainActivity: MainActivity
    lateinit var firebaseAuth : FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLoginFindPwBinding = FragmentLoginFindPwBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        firebaseAuth = FirebaseAuth.getInstance()
        fragmentLoginFindPwBinding.run {
            toolbarLoginFindPw.run {
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener{
                    mainActivity.removeFragment(MainActivity.LOGIN_FIND_PW_FRAGMENT)
                }
                setTitle("비밀번호 찾기")

            }
            var userEmail = arguments?.getString("UserEmail")
            if(userEmail!=null){
                textInputEditTextLoginFindPwEmail.setText(userEmail)
                //Log.e("테스트","$userEmail")
            }
            textInputLayoutLoginFindPwEmail.editText?.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    val emailCheck = textInputLayoutLoginFindPwEmail.editText?.text.toString()
                    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                    if (emailCheck.isEmpty()) {
                        textInputLayoutLoginFindPwEmail.error = "이메일을 입력해주세요"
                    } else if (!emailCheck.matches(emailPattern.toRegex())) {
                        textInputLayoutLoginFindPwEmail.error = "이메일 형식이 잘못되었습니다."
                    } else {
                        textInputLayoutLoginFindPwEmail.error = null
                        textInputLayoutLoginFindPwEmail.isErrorEnabled = false
                    }
                }
            }
            buttonLoginFindPw.setOnClickListener {
                val email = textInputLayoutLoginFindPwEmail.editText?.text.toString()
                if(email.isEmpty()){
                    textInputLayoutLoginFindPwEmail.requestFocus()
                    return@setOnClickListener
                }
                FindPw(email, textInputLayoutLoginFindPwEmail)
            }
        }
        return fragmentLoginFindPwBinding.root
    }

    fun FindPw(email:String, textInputLayout: TextInputLayout){
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showSuccessMessage()
            }
            else {
                handleResetPasswordError(task.exception)
            }
        }
    }

    private fun showSuccessMessage() {
        Snackbar.make(fragmentLoginFindPwBinding.root, "재설정 이메일 발송\n이메일을 확인해주세요", Snackbar.LENGTH_SHORT).show()
        mainActivity.removeFragment(MainActivity.LOGIN_FIND_PW_FRAGMENT)
    }

    private fun handleResetPasswordError(exception: Exception?) {
        mainActivity.hideKeyboard()
        val errorMessage = exception?.message ?: "알 수 없는 오류가 발생했습니다."

        if(errorMessage.contains("formatted")){
            Snackbar.make(fragmentLoginFindPwBinding.root, "이메일 형식을 지켜주세요", Snackbar.LENGTH_SHORT).show()
        }else if(errorMessage.contains("no user")){
            Snackbar.make(fragmentLoginFindPwBinding.root, "계정이 없습니다.\n 이메일을 다시 확인해주세요", Snackbar.LENGTH_SHORT).show()
        }else{
            Snackbar.make(fragmentLoginFindPwBinding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }
    }
}



