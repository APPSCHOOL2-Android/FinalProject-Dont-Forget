package com.test.dontforgetproject.UI.MainMyPageFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.databinding.DialogMypageLogoutBinding
import com.test.dontforgetproject.databinding.DialogMypageWithdrawBinding
import com.test.dontforgetproject.databinding.FragmentMainMyPageBinding
import java.util.ArrayList


class MainMyPageFragment : Fragment() {
    lateinit var fragmentMainMyPageBinding: FragmentMainMyPageBinding
    lateinit var mainActivity: MainActivity
    lateinit var mainMyPageViewModel: MainMyPageViewModel
    lateinit var firebaseAuth : FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMainMyPageBinding = FragmentMainMyPageBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        firebaseAuth = FirebaseAuth.getInstance()
        fragmentMainMyPageBinding.run {
            toolbarMainMyPage.run {
                setTitle(getString(R.string.myPage))
            }
            imageViewMyPageProfile.setImageResource(R.drawable.ic_person_24px)
            mainMyPageViewModel = ViewModelProvider(mainActivity)[MainMyPageViewModel::class.java]
            mainMyPageViewModel.run {
                userName.observe(mainActivity){
                    fragmentMainMyPageBinding.textViewMainMyPageName.setText(it.toString())
                }
                userImage.observe(mainActivity){ it ->
                    if(it.toString() != "None"){
                        UserRepository.getProfile(it.toString()){task->
                            if(task.isSuccessful){
                                val fileUri = task.result
                                Glide.with(mainActivity).load(fileUri).into(imageViewMyPageProfile)
                            }
                        }
                    }
                }
                userEmail.observe(mainActivity){
                    fragmentMainMyPageBinding.textViewMainMyPageEmail.setText(it.toString())
                }
                userIntoduce.observe(mainActivity){
                    fragmentMainMyPageBinding.textViewMainMyPageIntroduce.setText(it.toString())
                }

            }
            mainMyPageViewModel.getUserInfo(MyApplication.loginedUserInfo)

            cardViewMainMyPageModify.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MY_PAGE_MODIFY_FRAGMENT,true,null)
            }
            cardViewMainMyPageTheme.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MY_PAGE_THEME_FRAGMENT,true,null)
            }
            cardViewMainMyPageLogout.setOnClickListener {
                val dialogMypageLogoutBinding = DialogMypageLogoutBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)
                builder.setView(dialogMypageLogoutBinding.root)
                builder.setPositiveButton("로그아웃") { dialog, which ->
                    // 자동 로그인 해제
                    val sharedPreferences = requireActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("isLoggedIn")
                    editor.remove("isLoggedUser")
                    editor.apply()
                    val googleSignInClient = GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN)
                    googleSignInClient.signOut()
                    firebaseAuth.signOut()
                    MyApplication.isLogined = false
                    mainActivity.removeFragment(MainActivity.MAIN_FRAGMENT)
                    mainActivity.replaceFragment(MainActivity.LOGIN_FRAGMENT,false,null)

                }
                builder.setNegativeButton("취소",null)
                builder.show()

            }
            cardViewMainMyPageWithDraw.setOnClickListener {
                val dialogMypageWithdrawBinding = DialogMypageWithdrawBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)
                builder.setView(dialogMypageWithdrawBinding.root)
                builder.setNegativeButton("회원탈퇴") { dialog, which ->
                    UserRepository.deleteUserInfo(MyApplication.loginedUserInfo.userIdx){
                        if(it.isSuccessful){
                            var currentUser = firebaseAuth.currentUser!!
                            currentUser.delete().addOnCompleteListener {task ->
                                if(task.isSuccessful) {
                                    val sharedPreferences = requireActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.remove("isLoggedIn")
                                    editor.remove("isLoggedUser")
                                    editor.apply()
                                    val googleSignInClient = GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    googleSignInClient.signOut().addOnCompleteListener {}
                                    mainActivity.replaceFragment(MainActivity.LOGIN_FRAGMENT,false,null)
                                }
                            }
                        }
                    }
                }
                builder.setPositiveButton("취소",null)
                builder.show()
            }
        }

        return fragmentMainMyPageBinding.root
    }


}