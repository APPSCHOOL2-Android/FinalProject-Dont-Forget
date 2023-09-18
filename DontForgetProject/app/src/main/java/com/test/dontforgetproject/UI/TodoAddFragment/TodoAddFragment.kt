package com.test.dontforgetproject.UI.TodoAddFragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.MaterialDatePicker

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import com.test.dontforgetproject.DAO.AlertClass
import com.test.dontforgetproject.DAO.TodoClass
import com.test.dontforgetproject.GeofenceBroadcastReceiver
import com.test.dontforgetproject.GeofenceManager
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.AlertRepository
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.Repository.TodoRepository

import com.test.dontforgetproject.Repository.UserRepository
import com.test.dontforgetproject.databinding.DialogNormalBinding
import com.test.dontforgetproject.databinding.FragmentTodoAddBinding
import java.text.SimpleDateFormat
import java.util.Date


class TodoAddFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var todoAddBinding: FragmentTodoAddBinding
    lateinit var viewModel: TodoAddFragmentViewModel
    lateinit var geofenceManager: GeofenceManager
    lateinit var geofenceBroadcastReceiver: GeofenceBroadcastReceiver
    //이름,위도,경도 결과 받아옴
    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

            if(it.resultCode == Activity.RESULT_OK){
                val intent = it.data
                if(intent!=null){
                    val place = Autocomplete.getPlaceFromIntent(intent)

                    var placeName = place.name
                    // ㅎㅎ포차
                    var placeDetail = place.address
                    // 대한민국 서울특별시 중랑구 상봉동 번지 1층 88-48

                    var temp = placeDetail + "@" + placeName
                    var temp2 = placeDetail + " , "+ placeName
                    Log.d("Lim log","${placeDetail}")

                    MyApplication.locationName = temp

                    MyApplication.locationStoredName = temp2

                    viewModel.locate.value = MyApplication.locationStoredName

                    //장소 위도
                    var latitude = place.latLng.latitude
                    MyApplication.locationLatitude = latitude.toString()

                    //장소 경도
                    var longitude = place.latLng.longitude
                    MyApplication.locationLongitude = longitude.toString()

                }else if(it.resultCode == Activity.RESULT_CANCELED){
                    Log.d(
                        "Lim TAG", "Place FAil"
                    )
                }
            }
        }


    var date:String =""
    var myDate :String = ""
    var time:String = ""
    var name :String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        todoAddBinding = FragmentTodoAddBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(mainActivity).get(TodoAddFragmentViewModel::class.java)
        geofenceManager = GeofenceManager(requireContext())
        geofenceBroadcastReceiver = GeofenceBroadcastReceiver()
        viewModel.run {
            viewModel.name.observe(mainActivity){
                todoAddBinding.textViewTodoAddCategory.text = String.format("%s",it)
            }
            viewModel.categoryColor.observe(mainActivity){
                todoAddBinding.cardviewTodoAddCategory.setCardBackgroundColor(it.toInt())
            }
            viewModel.fontColor.observe(mainActivity){
                todoAddBinding.textViewTodoAddCategory.setTextColor(it.toInt())
            }
            viewModel.date.observe(mainActivity){
                todoAddBinding.textViewTodoAddDate.setText(it)
            }
            viewModel.time.observe(mainActivity){
                todoAddBinding.textViewTodoAddAlert.setText(it)
            }
            viewModel.locate.observe(mainActivity){
                todoAddBinding.textViewTodoAddLocation.setText(it)
            }
        }


        todoAddBinding.run {
            //툴바
            toolbarTodoAdd.run {
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    viewModel.resetList()
                    mainActivity.removeFragment(MainActivity.TODO_ADD_FRAGMENT)
                }
                setTitle("할일 추가")
            }
            //카데고리
            linearlayoutTodoAddCategory.run {

                setOnClickListener {
                    //번들로 기존 카데고리 데이터 넘겨줌
                    var bundle = Bundle()
                    bundle.putString("category","${textViewTodoAddCategory.text}")

                    var bottomDialog = TodoAddBottomDialog()
                    bottomDialog.arguments = bundle
                    bottomDialog.show(mainActivity.supportFragmentManager,"카테고리")
                }

            }
            //날짜
            linearlayoutTodoAddDate.run {

                setOnClickListener {
                    val materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()
                    materialDatePicker.addOnPositiveButtonClickListener {

                        //보여주는 DateFormat
                        val dateformatter = SimpleDateFormat("yyyy년 MM월 dd일")
                        val dates = dateformatter.format(Date(it))
                        myDate = dates
                        //보내는 DateFormat
                        val sendDateFormats = SimpleDateFormat("yyyy-MM-dd")
                        val dateOne = sendDateFormats.format(Date(it))
                        date = dateOne

                        Toast.makeText(mainActivity,"선택한 날짜는 ${dates} 입니다",Toast.LENGTH_SHORT).show()
                       textViewTodoAddDate.setText(dates)
                        viewModel.date.value = dates
                    }

                    materialDatePicker.show(mainActivity.supportFragmentManager,"Date")

                }
            }
            //시간
            linearlayoutTodoAddAlert.run {
                setOnClickListener {
                    var materialTimePicker = MaterialTimePicker.Builder()
                    materialTimePicker
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setTitleText("Select Time")
                        .setHour(12)
                        .setMinute(30)
                        .setInputMode(INPUT_MODE_KEYBOARD)
                        .build().apply {
                            addOnPositiveButtonClickListener {

                                //시간
                                time = "${hour}:${minute}"

                                //시 숫자가 10보다 작을떄
                                if("${hour}".toInt()<10){
                                    time = "0${hour}:${minute}"
                                }

                                //분 숫자가 10보다 작을떄
                                if("${minute}".toInt()<10){
                                    time = "${hour}:0${minute}"
                                }

                                //시,분 숫자가 10보다 작을 떄
                                if("${hour}".toInt()<10 && "${minute}".toInt()<10){
                                    time = "0${hour}:0${minute}"
                                }

                                //오전,오후 분기
                                if ("${hour}".toInt()>=12){
                                    var hours = "${hour}".toInt()-12
                                    textViewTodoAddAlert.text=  " 오후 ${hours}시 ${minute}분"
                                    viewModel.time.value = textViewTodoAddAlert.text.toString()
                                    Toast.makeText(mainActivity,"선택한 시간은 오후 ${hours}시 ${minute}분 입니다",Toast.LENGTH_SHORT).show()
                                }else{
                                    textViewTodoAddAlert.text= " 오전 ${hour}시 ${minute}분"
                                    viewModel.time.value = textViewTodoAddAlert.text.toString()
                                    Toast.makeText(mainActivity,"선택한 시간은 오전 ${hour}시 ${minute}분 입니다",Toast.LENGTH_SHORT).show()
                                }

                            }
                        }
                        .show(mainActivity.supportFragmentManager,"Time")

                }
            }
            //위치
            linearlayoutTodoAddLocation.run {

                setOnClickListener {
                    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION // 또는 ACCESS_COARSE_LOCATION
                    val requestCode = 123 // 요청 코드 (임의의 숫자)

                    if (ContextCompat.checkSelfPermission(requireContext(), locationPermission) == PackageManager.PERMISSION_GRANTED) {
                        //구글맵 키 받아옴
                        val key = com.test.dontforgetproject.BuildConfig.googlemapkey

                        // plac api 초기화
                        Places.initialize(context,key)
                        val placesClient = Places.createClient(mainActivity)

                        val field = listOf(Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS_COMPONENTS,Place.Field.TYPES,Place.Field.ADDRESS)
                        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,field)
                            .setHint("주소를 입력해주세요")
                            .build(mainActivity)
                        startAutocomplete.launch(intent)
                    } else {
                        // 권한을 요청
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(locationPermission), requestCode)
                    }


                }

            }

            buttonTodoAddComplete.run {

                setOnClickListener {
                    //유효성 검사
                    if(editTextTodoAdd.text!!.isEmpty()){
                        var dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
                        val builder = MaterialAlertDialogBuilder(mainActivity)

                        dialogNormalBinding.textViewDialogNormalTitle.text = "경고"
                        dialogNormalBinding.textViewDialogNormalContent.text = "할일을 입력해주세요."

                        builder.setView(dialogNormalBinding.root)
//                        val builder= AlertDialog.Builder(mainActivity)
//                        builder.setTitle("경고")
//                        builder.setMessage("할일을 입력해주세요")
                        builder.setNegativeButton("취소"){ dialogInterface: DialogInterface, i: Int ->

                        }
                        builder.setPositiveButton("확인"){dialogInterface: DialogInterface, i: Int ->
                            mainActivity.showSoftInput(editTextTodoAdd)
                        }
                        builder.show()
                        return@setOnClickListener
                    }
                    if(textViewTodoAddCategory.text == "카데고리 없음"){
                        var dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
                        val builder = MaterialAlertDialogBuilder(mainActivity)

                        dialogNormalBinding.textViewDialogNormalTitle.text = "경고"
                        dialogNormalBinding.textViewDialogNormalContent.text = "카데고리를 선택해주세요."

                        builder.setView(dialogNormalBinding.root)
//                        val builder= AlertDialog.Builder(mainActivity)
//                        builder.setTitle("경고")
//                        builder.setMessage("카데고리를 선택해주세요")
                        builder.setNegativeButton("취소"){ dialogInterface: DialogInterface, i: Int ->

                        }
                        builder.setPositiveButton("확인"){dialogInterface: DialogInterface, i: Int ->

                        }
                        builder.show()
                        return@setOnClickListener
                    }
                    if(textViewTodoAddDate.text == "날짜 없음"){
                        var dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
                        val builder = MaterialAlertDialogBuilder(mainActivity)

                        dialogNormalBinding.textViewDialogNormalTitle.text = "경고"
                        dialogNormalBinding.textViewDialogNormalContent.text = "날짜를 선택해주세요."

                        builder.setView(dialogNormalBinding.root)
//                        val builder= AlertDialog.Builder(mainActivity)
//                        builder.setTitle("경고")
//                        builder.setMessage("날짜를 선택해주세요")
                        builder.setNegativeButton("취소"){ dialogInterface: DialogInterface, i: Int ->

                        }
                        builder.setPositiveButton("확인"){dialogInterface: DialogInterface, i: Int ->

                        }
                        builder.show()
                        return@setOnClickListener
                    }


                    var newDate= date
                    var newTime = time

                    TodoRepository.getTodoIdx {
                        var idx = it.result.value as Long
                        idx++

                        var content = editTextTodoAdd.text.toString()
                        var useridx = MyApplication.loginedUserInfo.userIdx
                        var name = MyApplication.categoryname
                        var backgroundColor = MyApplication.categoryColor
                        var fontColor = MyApplication.categoryFontColor
                        var dates = newDate

                        //알림, 장소 이름,위도,경도 없을시 None으로 변경
                        var time = newTime
                        if(time==""){
                            time = "알림 없음"
                        }

                        var locationName = MyApplication.locationName
                        if(locationName == ""){
                            locationName = "위치 없음"
                        }

                        var locationLongtitude = MyApplication.locationLongitude
                        if(locationLongtitude == ""){
                            locationLongtitude = "None"
                        }

                        var locationLatitude = MyApplication.locationLatitude
                        if(locationLatitude == ""){
                            locationLatitude = "None"
                        }

                        if(locationLatitude !="None" && locationLongtitude != "None"){
                            val location = Location("my_provider")
                            location.latitude = MyApplication.locationLatitude.toDouble() // 위도
                            location.longitude = MyApplication.locationLongitude.toDouble() // 경도
                            geofenceManager.addGeofence("$locationName", location)
                            geofenceManager.registerGeofence()

                        }

                        CategoryRepository.getAllCategory {
                            for (c1 in it.result.children){
                                val categoryJoinUserIdxList =
                                    c1.child("categoryJoinUserIdxList").value as ArrayList<Long>?
                                var names = c1.child("categoryName").value.toString()
                                if(useridx !in categoryJoinUserIdxList!!){
                                    continue
                                }
                                if(names == name) {
                                    var catgoryIdx = c1.child("categoryIdx").value as Long
                                    var owneridx = MyApplication.loginedUserInfo.userIdx
                                    var ownerName = MyApplication.loginedUserInfo.userName
                                    var categoryIsPublic = c1.child("categoryIsPublic").value as Long
                                    var newPublicdata = categoryIsPublic.toInt()
                                    Log.d("Lim log","${categoryIsPublic}")

                                    //개인 카테고리 추가시
                                    if(newPublicdata == 0){
                                        var newclass = TodoClass(idx, content, 0, catgoryIdx, name, fontColor.toLong(), backgroundColor.toLong(), dates,
                                            time, locationName, locationLatitude, locationLongtitude, owneridx, ownerName)

                                        TodoRepository.setTodoAddInfo(newclass){
                                            TodoRepository.setTodoIdx(idx){
                                                Toast.makeText(mainActivity, "저장되었습니다", Toast.LENGTH_SHORT).show()
                                                mainActivity.removeFragment(MainActivity.TODO_ADD_FRAGMENT)
                                                viewModel.resetList()
                                            }
                                        }
                                    }

                                    //공용 카데고리 추가시
                                    if(newPublicdata == 1){
                                        var newclass = TodoClass(idx, content, 0, catgoryIdx, name, fontColor.toLong(), backgroundColor.toLong(), dates,
                                            time, locationName, locationLatitude, locationLongtitude, owneridx, ownerName)
                                        TodoRepository.setTodoAddInfo(newclass) {
                                            TodoRepository.setTodoIdx(idx) {
                                                var text = "${names}에 ${myDate} 새 할일이 추가되었습니다"
                                                AlertRepository.getAlertIdx {
                                                    var idx = it.result.value as Long
                                                    idx++
                                                    var newclass2 = AlertClass(idx, text, useridx, 2)
                                                    AlertRepository.addAlertInfo(newclass2) {
                                                        AlertRepository.setAlertIdx(idx) {
                                                            Toast.makeText(mainActivity, "저장되었습니다", Toast.LENGTH_SHORT).show()
                                                            mainActivity.removeFragment(MainActivity.TODO_ADD_FRAGMENT)
                                                            viewModel.resetList()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }



                    }

                }
            }

        }
        return  todoAddBinding.root
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        viewModel.resetList()
//    }

}
