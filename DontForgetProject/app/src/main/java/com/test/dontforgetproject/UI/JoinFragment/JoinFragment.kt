package com.test.dontforgetproject.UI.JoinFragment

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.Util.FirebaseUtil
import com.test.dontforgetproject.databinding.DialogNormalBinding
import com.test.dontforgetproject.databinding.FragmentJoinBinding


class JoinFragment : Fragment() {
    lateinit var fragmentJoinBinding: FragmentJoinBinding
    lateinit var mainActivity: MainActivity
    private lateinit var firebaseAuth : FirebaseAuth
    lateinit var albumLauncher: ActivityResultLauncher<Intent>
    // 업로드할 이미지의 Uri
    var uploadUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentJoinBinding = FragmentJoinBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        // 앨범 설정
        albumLauncher = albumSetting(fragmentJoinBinding.imageViewJoinProfile)
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
            if (userType == MyApplication.GOOGLE_LOGIN) {
                val userEmail = arguments?.getString("UserEmail")
                fragmentJoinBinding.textInputLayoutJoinEmail.editText?.apply {
                    setText(userEmail)
                    isEnabled = false
                }
                fragmentJoinBinding.textInputLayoutJoinPassword.visibility = View.GONE
                fragmentJoinBinding.textInputLayoutJoinPasswordCheck.visibility = View.GONE
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
                val userImage = if (uploadUri == null) {
                    "None"
                } else {
                    "image/img_${System.currentTimeMillis()}.jpg"
                }
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
                            else if(firebaseCheck.contains( "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")) {
                                val dialogNormalBinding =
                                    DialogNormalBinding.inflate(layoutInflater)
                                val builder = MaterialAlertDialogBuilder(mainActivity)
                                dialogNormalBinding.textViewDialogNormalTitle.text = "중복확인"
                                dialogNormalBinding.textViewDialogNormalContent.text = "이미 존재하는 이메일 입니다"
                                builder.setView(dialogNormalBinding.root)
                                mainActivity.hideKeyboard()
                                builder.setPositiveButton("확인") { dialog, which ->
                                    textInputEditTextJoinEmail.requestFocus()
                                }
                                builder.show()
                            }
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
            // 사진 추가 버튼
            buttonJoinPhoto.setOnClickListener {
                // 앨범에서 사진을 선택할 수 있는 Activity를 실행한다.
                val newIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                // 실행할 액티비티의 마임타입 설정(이미지로 설정해준다)
                newIntent.setType("image/*")
                // 선택할 파일의 타입을 지정(안드로이드  OS가 이미지에 대한 사전 작업을 할 수 있도록)
                val mimeType = arrayOf("image/*")
                newIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                // 액티비티를 실행한다.
                albumLauncher.launch(newIntent)
            }
        }
        return fragmentJoinBinding.root
    }


    // 유효성 검사 함수
    fun checkValidation(textInputLayout: TextInputLayout, type:String):Boolean{
        val check = textInputLayout.editText?.text.toString()
        val checkBoolean = check.isEmpty()

        if(checkBoolean){
            textInputLayout.error = "${type}을 입력해주세요."
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
                val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]*\$".toRegex()
                if(check.length < 6){
                    textInputLayout.error = "6자리이상 입력해주세요."
                }else if(!check.matches(passwordPattern)){
                    textInputLayout.error = "6~16자의 영문 대/소문자, 숫자를 사용해 주세요."
                }
                else{
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
            var friendList = ArrayList<Friend>()
            friendList.add(Friend(userindex,userName,userEmail))

            val userInfo = UserClass(userindex,userName,userEmail,userImage,userIntroduce,userId,friendList)
            UserRepository.setUserInfo(userInfo){
                UserRepository.setUserIdx(userindex){
                    if(uploadUri!=null){
                        UserRepository.setUploadProfile(userImage,uploadUri!!){
                        }
                    }
                    Snackbar.make(fragmentJoinBinding.root, "저장되었습니다.", Snackbar.LENGTH_SHORT).show()
                    mainActivity.removeFragment(MainActivity.JOIN_FRAGMENT)
                }
            }
        }
    }

    // 앨범 설정
    fun albumSetting(previewImageView: ImageView): ActivityResultLauncher<Intent> {
        val albumContract = ActivityResultContracts.StartActivityForResult()
        val albumLauncher = registerForActivityResult(albumContract) {
            if (it?.resultCode == AppCompatActivity.RESULT_OK) {
                // 선택한 이미지에 접근할 수 있는 Uri 객체를 추출한다.
                if (it.data?.data != null) {
                    uploadUri = it.data?.data

                    // 안드로이드 10 (Q) 이상이라면...
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // 이미지를 생성할 수 있는 디코더를 생성한다.
                        val source = ImageDecoder.createSource(mainActivity.contentResolver, uploadUri!!)
                        // Bitmap객체를 생성한다.
                        val bitmap = ImageDecoder.decodeBitmap(source)

                        previewImageView.setImageBitmap(bitmap)
                    } else {
                        // 컨텐츠 프로바이더를 통해 이미지 데이터 정보를 가져온다.
                        val cursor = mainActivity.contentResolver.query(uploadUri!!, null, null, null, null)
                        if (cursor != null) {
                            cursor.moveToNext()

                            // 이미지의 경로를 가져온다.
                            val idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                            val source = cursor.getString(idx)

                            // 이미지를 생성하여 보여준다.
                            val bitmap = BitmapFactory.decodeFile(source)
                            previewImageView.setImageBitmap(bitmap)
                        }
                    }
                }
            }
        }
        return albumLauncher
    }
}