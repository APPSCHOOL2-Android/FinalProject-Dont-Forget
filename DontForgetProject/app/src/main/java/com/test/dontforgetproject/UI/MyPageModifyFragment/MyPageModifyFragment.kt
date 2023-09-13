package com.test.dontforgetproject.UI.MyPageModifyFragment

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
import com.firebase.ui.auth.data.model.User
import com.google.android.material.snackbar.Snackbar
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.UI.JoinFragment.JoinFragment
import com.test.dontforgetproject.databinding.FragmentMyPageModifyBinding

class MyPageModifyFragment : Fragment() {
    lateinit var fragmentMyPageModifyBinding: FragmentMyPageModifyBinding
    lateinit var mainActivity: MainActivity
    lateinit var albumLauncher: ActivityResultLauncher<Intent>
    // 업로드할 이미지의 Uri
    var uploadUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMyPageModifyBinding = FragmentMyPageModifyBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentMyPageModifyBinding.run {
            albumLauncher = albumSetting(fragmentMyPageModifyBinding.imageViewMyPageModifyProfile)
            toolbarMyPageModify.run {
                title = getString(R.string.myPage_modify)
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MY_PAGE_MODIFY_FRAGMENT)
                }
            }
            val user = MyApplication.loginedUserInfo
            val name = user.userName
            val introduce = user.userIntroduce
            val userImage = if (uploadUri == null) {
                "None"
            } else {
                "image/img_${System.currentTimeMillis()}.jpg"
            }
            textInputEditTextMyPageModifyName.setText(name)
            textInputEditTextMyPageModifyIntroduce.setText(introduce)

            // 사진 변경 클릭
            buttonMyPageModifyModifyPhoto.setOnClickListener {
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
            buttonMyPageModifyModifyComplete.setOnClickListener {
                val newUser = fragmentMyPageModifyBinding.textInputLayoutMyPageModifyName.editText?.text.toString()
                val newIntroduce = fragmentMyPageModifyBinding.textInputLayoutMyPageModifyIntroduce.editText?.text.toString()
                if(MyApplication.loginedUserInfo.userImage != "None"){
                    UserRepository.deleteProfile(MyApplication.loginedUserInfo.userImage)
                }
                var newImage = userImage
                if (newUser.isNotEmpty() && newIntroduce.isNotEmpty()) {
                    val modifyUser = UserClass(
                        user.userIdx,
                        newUser, // 수정된 사용자 이름
                        user.userEmail,
                        newImage,
                        newIntroduce, // 수정된 소개 내용
                        user.userId,
                        user.userFriendList
                    )
                    UserRepository.modifyUserInfo(modifyUser) {
                        if (it.isComplete) {
                            if(newImage != "None"){
                                UserRepository.setUploadProfile(newImage,uploadUri!!){

                                }
                            }
                            MyApplication.loginedUserInfo = modifyUser
                        } else {
                            Snackbar.make(fragmentMyPageModifyBinding.root, "오류 발생.", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    MyApplication.loginedUserInfo = modifyUser
                    Snackbar.make(fragmentMyPageModifyBinding.root, "수정되었습니다.", Snackbar.LENGTH_SHORT).show()
                    mainActivity.removeFragment(MainActivity.MY_PAGE_MODIFY_FRAGMENT)
                } else {
                    Snackbar.make(fragmentMyPageModifyBinding.root, "빈 칸을 채워주세요.", Snackbar.LENGTH_SHORT).show()
                }
            }

        }

        return fragmentMyPageModifyBinding.root
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