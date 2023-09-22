
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.UI.MainMyPageFragment.MainMyPageViewModel
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
    @OptIn(DelicateCoroutinesApi::class)
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
                title = getString(R.string.myPage)
            }
            mainMyPageViewModel = ViewModelProvider(mainActivity)[MainMyPageViewModel::class.java]
            mainMyPageViewModel.run {
                userName.observe(mainActivity) {
                    fragmentMainMyPageBinding.textViewMainMyPageName.setText(it.toString())
                }
                userImage.observe(mainActivity){
                    GlobalScope.launch {
                        UserRepository.getProfile(it.toString()){
                            if(it.isSuccessful){
                                val fileUri = it.result
                                Glide.with(mainActivity).load(fileUri).diskCacheStrategy(
                                    DiskCacheStrategy.ALL).into(imageViewMyPageProfile)
                                MyApplication.loginedUserProfile = fileUri.toString()
                            }
                            loadingDialog.dismiss()
                        }

                    }

                }
                userEmail.observe(mainActivity) {
                    fragmentMainMyPageBinding.textViewMainMyPageEmail.setText(it.toString())
                }
                userIntoduce.observe(mainActivity) {
                    fragmentMainMyPageBinding.textViewMainMyPageIntroduce.setText(it.toString())
                }

            }
            mainMyPageViewModel.getUserInfo(MyApplication.loginedUserInfo)
            cardViewMainMyPageModify.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MY_PAGE_MODIFY_FRAGMENT, true, null)
            }
            cardViewMainMyPageTheme.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MY_PAGE_THEME_FRAGMENT, true, null)
            }
            cardViewMainMyPageLogout.setOnClickListener {
                val dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
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
                    MyApplication.loginedUserProfile = ""
                    mainActivity.removeFragment(MainActivity.MAIN_FRAGMENT)
                    mainActivity.replaceFragment(MainActivity.LOGIN_FRAGMENT, false, null)

                }
                builder.setNegativeButton("취소", null)
                builder.show()

            }
            cardViewMainMyPageWithDraw.setOnClickListener {
                val currentUser = firebaseAuth.currentUser
                val bundle = Bundle()
                // 사용자가 로그인한 제공업체(Provider) 목록 가져오기
                val providers = currentUser?.providerData
                // providers 목록을 반복하여 사용자가 연결된 제공업체 확인
                for (profile in providers!!) {
                    val providerId = profile.providerId
                    if (providerId == GoogleAuthProvider.PROVIDER_ID) {
                        bundle.putInt("UserType", MyApplication.GOOGLE_LOGIN)
                    } else if (providerId == EmailAuthProvider.PROVIDER_ID) {
                        bundle.putInt("UserType", MyApplication.EMAIL_LOGIN)
                    }
                }
                mainActivity.replaceFragment(MainActivity.MY_PAGE_WITH_DRAW_FRAGMENT,true,bundle)
            }

        }

        return fragmentMainMyPageBinding.root
    }

    override fun onResume() {
        super.onResume()
        FirebaseDatabase.getInstance().reference
            .child("userInfo")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    mainMyPageViewModel.getUserInfo(MyApplication.loginedUserInfo)
                }
            })
    }
}