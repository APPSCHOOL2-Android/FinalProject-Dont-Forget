package com.test.dontforgetproject.UI.LoginFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentLoginBinding
import com.test.dontforgetproject.databinding.FragmentMainBinding


class LoginFragment : Fragment() {
    lateinit var fragmentLoginBinding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentLoginBinding.run {
            var email = textInputLayoutLoginEmail.editText?.text.toString()
            var password = textInputLayoutLoginPassword.editText?.text.toString()
            // 로그인버튼
            buttonLogin.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MAIN_FRAGMENT,false,null)
            }
            // 가입하기 텍스트
             textViewLoginJoin.setOnClickListener {
                 val bundle = Bundle()
                 bundle.putInt("UserType", 1)
                 mainActivity.replaceFragment(MainActivity.JOIN_FRAGMENT,true,bundle)
             }
            // 비밀번호 찾기 텍스트
            textviewLoginFindPassword.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.LOGIN_FIND_PW_FRAGMENT,true,null)
            }
            buttonLoginGoogleLogin.setOnClickListener{
                val bundle = Bundle()
                bundle.putInt("UserType", 0)
                val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)) // 웹 클라이언트 ID
                    .requestEmail() // 이메일 권한 요청 (선택 사항)
                    .build()

                val googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, 9001)
                mainActivity.replaceFragment(MainActivity.JOIN_FRAGMENT,true,bundle)
            }

        }

        return fragmentLoginBinding.root
    }

}