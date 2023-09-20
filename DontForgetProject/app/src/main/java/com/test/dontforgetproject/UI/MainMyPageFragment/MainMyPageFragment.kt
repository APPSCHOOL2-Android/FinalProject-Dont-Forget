package com.test.dontforgetproject.UI.MainMyPageFragment

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.AlertRepository
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.Repository.JoinFriendRepository
import com.test.dontforgetproject.Repository.TodoRepository
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.Util.LoadingDialog
import com.test.dontforgetproject.databinding.DialogNormalBinding
import com.test.dontforgetproject.databinding.FragmentMainMyPageBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainMyPageFragment : Fragment() {
    lateinit var fragmentMainMyPageBinding: FragmentMainMyPageBinding
    lateinit var mainActivity: MainActivity
    lateinit var mainMyPageViewModel: MainMyPageViewModel
    lateinit var firebaseAuth : FirebaseAuth
    lateinit var loadingDialog: LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMainMyPageBinding = FragmentMainMyPageBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        firebaseAuth = FirebaseAuth.getInstance()
        fragmentMainMyPageBinding.run {
            loadingDialog = LoadingDialog(requireContext())
            loadingDialog.show()
            toolbarMainMyPage.run {
                setTitle(getString(R.string.myPage))
            }
            mainMyPageViewModel = ViewModelProvider(mainActivity)[MainMyPageViewModel::class.java]
            mainMyPageViewModel.run {
                userName.observe(mainActivity) {
                    fragmentMainMyPageBinding.textViewMainMyPageName.setText(it.toString())
                }
                userImage.observe(mainActivity) {
                    if (MyApplication.loginedUserInfo.userImage != "None") {
                        UserRepository.getProfile(it.toString()) {
                            if (it.isSuccessful) {
                                val fileUri = it.result
                                Glide.with(mainActivity).load(fileUri).diskCacheStrategy(
                                    DiskCacheStrategy.ALL
                                ).into(imageViewMyPageProfile)
                            }
                        }
                    } else {
                        imageViewMyPageProfile.setImageResource(R.drawable.ic_person_24px)
                    }
                    loadingDialog.dismiss()

                }
                userEmail.observe(mainActivity) {
                    fragmentMainMyPageBinding.textViewMainMyPageEmail.setText(it.toString())
                }
                userIntoduce.observe(mainActivity) {
                    fragmentMainMyPageBinding.textViewMainMyPageIntroduce.setText(it.toString())
                }
            }
            FirebaseDatabase.getInstance().reference
                .child("userInfo")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("lion", "실시간 탐지 에러 : $p0")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        mainMyPageViewModel.getUserInfo(MyApplication.loginedUserInfo)
                        Log.d("lion", "실시간 탐지 성공 : $p0")
                    }
                })

            mainMyPageViewModel.getUserInfo(MyApplication.loginedUserInfo)

            cardViewMainMyPageModify.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MY_PAGE_MODIFY_FRAGMENT, true, null)
            }
            cardViewMainMyPageTheme.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MY_PAGE_THEME_FRAGMENT, true, null)
            }
            cardViewMainMyPageLogout.setOnClickListener {
                var dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)

                dialogNormalBinding.textViewDialogNormalTitle.text = "로그아웃"
                dialogNormalBinding.textViewDialogNormalContent.text = "로그아웃 하시겠습니까?"

                builder.setView(dialogNormalBinding.root)

                builder.setPositiveButton("로그아웃") { dialog, which ->
                    // 자동 로그인 해제
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
                    firebaseAuth.signOut()
                    Glide.with(requireActivity()).clear(imageViewMyPageProfile)
                    MyApplication.isLogined = false
                    mainActivity.removeFragment(MainActivity.MAIN_FRAGMENT)
                    mainActivity.replaceFragment(MainActivity.LOGIN_FRAGMENT, false, null)

                }
                builder.setNegativeButton("취소", null)
                builder.show()

            }
            cardViewMainMyPageWithDraw.setOnClickListener {
                var dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)

                dialogNormalBinding.textViewDialogNormalTitle.text = "회원탈퇴"
                dialogNormalBinding.textViewDialogNormalContent.text =
                    "회원 탈퇴 후에는 계정과 관련된 모든 정보가 삭제됩니다."

                builder.setView(dialogNormalBinding.root)
                builder.setNegativeButton("회원탈퇴") { dialog, which ->
                    // 사용자가 이미 로그아웃 상태일 수 있으므로 FirebaseAuth의 currentUser를 사용할 때 null 여부를 확인
                    val currentUser = firebaseAuth.currentUser
                    if (currentUser != null) {
                        // FirebaseAuth를 사용하여 사용자 삭제 시도
                        currentUser.delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // 사용자 삭제가 성공하면 SharedPreferences에서 로그인 정보를 삭제합니다.
                                UserRepository.deleteUserInfo(MyApplication.loginedUserInfo.userIdx) {}
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

                                // Google 로그인 클라이언트에서 로그아웃도 시도합니다.
                                val googleSignInClient = GoogleSignIn.getClient(
                                    requireActivity(),
                                    GoogleSignInOptions.DEFAULT_SIGN_IN
                                )
                                googleSignInClient.signOut().addOnCompleteListener {}

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
                builder.setPositiveButton("취소", null)
                builder.show()
            }

        }

        return fragmentMainMyPageBinding.root
    }


}