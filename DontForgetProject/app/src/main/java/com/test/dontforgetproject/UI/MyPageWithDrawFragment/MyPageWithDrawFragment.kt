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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.AlertRepository
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.Repository.JoinFriendRepository
import com.test.dontforgetproject.Repository.TodoRepository
import com.test.dontforgetproject.Repository.UserRepository
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
            toolbarMyPageWithDraw.run {
                title = "회원탈퇴"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MY_PAGE_WITH_DRAW_FRAGMENT)
                }
            }
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
                        .requestIdToken("194726734690-sm7nu0e0cjr12r4cu39547d10l09rf0r.apps.googleusercontent.com")
                        .requestEmail()
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
                    var myFriendList = MyApplication.loginedUserInfo.userFriendList
                    var myIdx = MyApplication.loginedUserInfo.userIdx

                    // 사용자의 친구들의 계정을 모두 불러와서 안에들어있는 내정보 삭제
                    for(friend in myFriendList){
                        if(myIdx != friend.friendIdx){
                            JoinFriendRepository.getUserInfoByIdx(friend.friendIdx){
                                for (c1 in it.result.children) {
                                    var userIdx = c1.child("userIdx").value as Long
                                    var userName = c1.child("userName").value as String
                                    var userEmail = c1.child("userEmail").value as String
                                    var userImage = c1.child("userImage").value as String
                                    var userIntroduce = c1.child("userIntroduce").value as String
                                    var userId = c1.child("userId").value as String
                                    var userFriendList = mutableListOf<Friend>()

                                    var userFriendListHashMap =
                                        c1.child("userFriendList").value as ArrayList<HashMap<String, Any>>

                                    for (i in userFriendListHashMap) {
                                        var idx = i["friendIdx"] as Long
                                        var name = i["friendName"] as String
                                        var email = i["friendEmail"] as String

                                        var friend = Friend(idx, name, email)
                                        userFriendList.add(friend)
                                    }

                                    var temp = mutableListOf<Friend>()
                                    for(v in userFriendList){
                                        if(v.friendIdx == myIdx){
                                            temp.add(v)
                                        }
                                    }
                                    userFriendList.removeAll(temp)

                                    var friendUserClass = UserClass(
                                        userIdx,
                                        userName,
                                        userEmail,
                                        userImage,
                                        userIntroduce,
                                        userId,
                                        userFriendList as ArrayList<Friend>
                                    )

                                    // 수정된 친구정보 반영
                                    UserRepository.modifyUserInfo(friendUserClass){}
                                }
                            }
                        }
                    }
                    UserRepository.deleteUserInfo(myIdx) {}
                    CategoryRepository.removeUserInPublicCategory(myIdx)
                    AlertRepository.removeAlertByUserIdx(myIdx) {}
                    TodoRepository.removeTodoByUserIdx(myIdx) {}
                    CategoryRepository.removeCategoryByUserIdx(myIdx) {}
                    JoinFriendRepository.deleteJoinFriendByMyData(
                        myIdx,
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