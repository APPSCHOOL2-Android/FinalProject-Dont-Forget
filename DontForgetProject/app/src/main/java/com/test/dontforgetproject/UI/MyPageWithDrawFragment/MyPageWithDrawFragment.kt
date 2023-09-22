package com.test.dontforgetproject.UI.MyPageWithDrawFragment

import android.content.Context
import android.os.Bundle
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
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.AlertRepository
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.Repository.JoinFriendRepository
import com.test.dontforgetproject.Repository.TodoRepository
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.databinding.DialogNormalBinding
import com.test.dontforgetproject.databinding.FragmentMyPageWithDrawBinding

class MyPageWithDrawFragment : Fragment() {
    lateinit var fragmentMyPageWithDrawBinding: FragmentMyPageWithDrawBinding
    lateinit var mainActivity: MainActivity
    lateinit var firebaseAuth : FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMyPageWithDrawBinding = FragmentMyPageWithDrawBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentMyPageWithDrawBinding.run {
            textViewMyPageWithDrawEmail.setText(MyApplication.loginedUserInfo.userEmail)
            textViewMyPageWithDrawFindPassword.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("UserEmail", MyApplication.loginedUserInfo.userEmail)
                mainActivity.replaceFragment(MainActivity.LOGIN_FIND_PW_FRAGMENT,true,bundle)
            }
            firebaseAuth = FirebaseAuth.getInstance()
            val userType = arguments?.getInt("UserType")
            if(userType == MyApplication.GOOGLE_LOGIN){
                textInputLayoutMyPageWithDrawPassword.visibility = View.GONE
                textViewMyPageWithDraw.visibility = View.GONE
                textViewMyPageWithDrawFindPassword.visibility = View.GONE
            }
            buttonMyPageWithDraw.setOnClickListener {

                if(userType == MyApplication.EMAIL_LOGIN){
                    val email = textViewMyPageWithDrawEmail.text.toString()
                    val password = textInputEditTextMyPageWithDrawPassword.text.toString()
                    if(password.isNotEmpty()){
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity(), OnCompleteListener<AuthResult?> { task ->
                                if(task.isSuccessful){
                                    withDraw()
                                }else{
                                    textViewMyPageWithDraw2.visibility = View.VISIBLE
                                }
                            })

                    }else{
                        textInputEditTextMyPageWithDrawPassword.requestFocus()
                    }
                }else{
                    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id)) // 웹 클라이언트 ID
                        .requestEmail() // 이메일 권한 요청 (선택 사항)
                        .build()

                    val googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
                    val signInIntent = googleSignInClient.signInIntent
                    startActivityForResult(signInIntent, 9001)
                    withDraw()
                }
            }

        }
        return fragmentMyPageWithDrawBinding.root
    }
    fun withDraw(){
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // FirebaseAuth를 사용하여 사용자 삭제 시도
            currentUser.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 사용자 삭제가 성공하면 SharedPreferences에서 로그인 정보를 삭제합니다.
                    UserRepository.deleteUserInfo(MyApplication.loginedUserInfo.userIdx) {}
                    CategoryRepository.removeUserInPublicCategory(MyApplication.loginedUserInfo.userIdx)
                    AlertRepository.removeAlertByUserIdx(MyApplication.loginedUserInfo.userIdx) {}
                    TodoRepository.removeTodoByUserIdx(MyApplication.loginedUserInfo.userIdx) {}
                    CategoryRepository.removeCategoryByUserIdx(MyApplication.loginedUserInfo.userIdx) {}
                    JoinFriendRepository.deleteJoinFriendByMyData(
                        MyApplication.loginedUserInfo.userIdx,
                        MyApplication.loginedUserInfo.userEmail
                    )
                    val sharedPreferences = requireActivity().getSharedPreferences(
                        "MyAppPreferences",
                        Context.MODE_PRIVATE
                    )
                    val editor = sharedPreferences.edit()
                    editor.remove("isLoggedIn")
                    editor.remove("isLoggedUser")
                    editor.apply()

                    val googleSignInClient = GoogleSignIn.getClient(
                        requireActivity(),
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                    )
                    googleSignInClient.signOut()

                    Snackbar.make(fragmentMyPageWithDrawBinding.root, "회원탈퇴되었습니다.", Snackbar.LENGTH_SHORT).show()
                    // 로그인 화면으로 이동
                    mainActivity.replaceFragment(
                        MainActivity.LOGIN_FRAGMENT,
                        false,
                        null
                    )
                }

            }
        }

    }


}