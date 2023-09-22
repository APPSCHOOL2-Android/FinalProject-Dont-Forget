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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.DAO.UserClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.UI.MainMyPageFragment.MyPageModifyViewModel
import com.test.dontforgetproject.Util.LoadingDialog
import com.test.dontforgetproject.databinding.FragmentMyPageModifyBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.ArrayList

class MyPageModifyFragment : Fragment() {
    lateinit var fragmentMyPageModifyBinding: FragmentMyPageModifyBinding
    lateinit var mainActivity: MainActivity
    lateinit var albumLauncher: ActivityResultLauncher<Intent>
    lateinit var myPageModifyViewModel : MyPageModifyViewModel
    // 업로드할 이미지의 Uri
    var uploadUri: Uri? = null
    lateinit var user : UserClass
    lateinit var loadingDialog : LoadingDialog
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMyPageModifyBinding = FragmentMyPageModifyBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        // 앨범 설정
        albumLauncher = albumSetting(fragmentMyPageModifyBinding.imageViewMyPageModifyProfile)
        loadingDialog = LoadingDialog(requireContext())
        loadingDialog.show()
        fragmentMyPageModifyBinding.run {
            user = MyApplication.loginedUserInfo
            toolbarMyPageModify.run {
                title = getString(R.string.myPage_modify)
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MY_PAGE_MODIFY_FRAGMENT)
                }
            }

            myPageModifyViewModel = ViewModelProvider(mainActivity)[MyPageModifyViewModel::class.java]
            myPageModifyViewModel.run {
                userIdx.observe(mainActivity){
                    user.userIdx = it
                }
                userName.observe(mainActivity){
                    user.userName = it
                }
                userIntoduce.observe(mainActivity){
                    user.userIntroduce = it
                    fragmentMyPageModifyBinding.textInputEditTextMyPageModifyIntroduce.setText(it.toString())
                }
                userImage.observe(mainActivity){
                    if(MyApplication.loginedUserInfo.userImage != "None"){
                        UserRepository.getProfile(it.toString()){
                            if(it.isSuccessful){
                                val fileUri = it.result
                                Glide.with(mainActivity).load(fileUri).diskCacheStrategy(
                                    DiskCacheStrategy.ALL).into(imageViewMyPageModifyProfile)
                                MyApplication.loginedUserProfile = fileUri.toString()
                            }
                        }
                    }else{
                        imageViewMyPageModifyProfile.setImageResource(R.drawable.ic_person_24px)
                    }
                    loadingDialog.dismiss()

                    user.userImage = it.toString()
                }
                userEmail.observe(mainActivity){
                    user.userEmail = it.toString()
                }
                userId.observe(mainActivity){
                    user.userId = it.toString()
                }
                userFriendList.observe(mainActivity){
                    user.userFriendList = it as ArrayList<Friend>
                }
            }
            myPageModifyViewModel.getUserInfo(MyApplication.loginedUserInfo)
            Glide.with(requireActivity()).load(MyApplication.loginedUserProfile).into(imageViewMyPageModifyProfile)
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
                val newIntroduce = fragmentMyPageModifyBinding.textInputLayoutMyPageModifyIntroduce.editText?.text.toString()

                // 이미지를 변경하지 않을 경우 "None"으로 설정
                val newImage = if (uploadUri == null) {
                    user.userImage
                } else {
                    "image/img_${System.currentTimeMillis()}.jpg"
                }

                // 이미지가 변경되지 않으면 업로드하지 않고 이전 이미지를 사용
                if (newImage != user.userImage) {
                    UserRepository.setUploadProfile(newImage, uploadUri!!) { result ->
                        if (result.isSuccessful) {
                        } else {
                        }
                    }
                }

                if (newIntroduce.isNotEmpty()) {
                    val modifyUser = UserClass(
                        user.userIdx,
                        user.userName,
                        user.userEmail,
                        newImage,
                        newIntroduce,
                        user.userId,
                        user.userFriendList
                    )
                    UserRepository.modifyUserInfo(modifyUser) { result ->
                        if (result.isSuccessful) {
                            MyApplication.loginedUserInfo = modifyUser
                        } else {
                            Snackbar.make(fragmentMyPageModifyBinding.root, "오류 발생.", Snackbar.LENGTH_SHORT).show()
                        }
                    }

                    loadingDialog.show()
                    GlobalScope.launch {
                        delay(1000)
                        loadingDialog.dismiss()
                        Snackbar.make(fragmentMyPageModifyBinding.root, "수정되었습니다.", Snackbar.LENGTH_SHORT).show()
                        mainActivity.removeFragment(MainActivity.MY_PAGE_MODIFY_FRAGMENT)
                    }


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