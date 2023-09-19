package com.test.dontforgetproject.UI.TodoDetailPublicOwnerFragment

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.test.dontforgetproject.AlarmFunctions
import com.test.dontforgetproject.DAO.TodoClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MainActivity.Companion.TODO_DETAIL_PUBLIC_OWNER_FRAGMENT
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.TodoRepository
import com.test.dontforgetproject.UI.TodoDetailPersonalFragment.TodoDetailPersonalViewModel
import com.test.dontforgetproject.databinding.DialogNormalBinding
import com.test.dontforgetproject.databinding.FragmentTodoDetailPublicOwnerBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class TodoDetailPublicOwnerFragment : Fragment() {

    lateinit var fragmentTodoDetailPublicOwnerBinding: FragmentTodoDetailPublicOwnerBinding
    lateinit var mainActivity: MainActivity

    lateinit var todoDetailPersonalViewModel: TodoDetailPersonalViewModel

    lateinit var alarmFunctions: AlarmFunctions

    var todoIdx = 0L

    var placeAddress = ""
    var latitude = ""
    var longitude = ""
    var time = ""

    //이름,위도,경도 결과 받아옴
    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

            if(it.resultCode == Activity.RESULT_OK) {
                val intent = it.data
                if(intent!=null){
                    val place = Autocomplete.getPlaceFromIntent(intent)

                    var placeName = place.name
                    var placeDetail = place.address

                    placeAddress = placeDetail + "@" + placeName
                    Log.d("lion","${placeAddress}")
                    fragmentTodoDetailPublicOwnerBinding.textViewTodoDetailPublicOwnerLocation.text = placeDetail

                    //장소 위도
                    latitude = place.latLng.latitude.toString()
                    Log.d("lion","${latitude}")

                    //장소 경도
                    longitude = place.latLng.longitude.toString()
                    Log.d("lion","${longitude}")

                    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION // 또는 ACCESS_COARSE_LOCATION
                    val requestCode = 123 // 요청 코드 (임의의 숫자)

                    if (ContextCompat.checkSelfPermission(requireContext(), locationPermission) == PackageManager.PERMISSION_GRANTED) {
                        // 이미 위치 권한이 허용되어 있음
                        // 권한이 필요한 기능 수행
                    } else {
                        // 권한을 요청
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(locationPermission), requestCode)
                    }

                }
            }
            else if(it.resultCode == Activity.RESULT_CANCELED) {
                Log.d("lion", "Place Fail")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentTodoDetailPublicOwnerBinding = FragmentTodoDetailPublicOwnerBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        todoIdx = arguments?.getLong("todoIdx",0)!!

        todoDetailPersonalViewModel = ViewModelProvider(mainActivity)[TodoDetailPersonalViewModel::class.java]
        todoDetailPersonalViewModel.run {

            todoContent.observe(mainActivity) {
                fragmentTodoDetailPublicOwnerBinding.textInputEditTextTodoDetailPublicOwner.setText(it.toString())
            }
            todoCategoryName.observe(mainActivity) {
                fragmentTodoDetailPublicOwnerBinding.buttonTodoDetailPublicOwnerCategory.text = it.toString()
            }
            todoDate.observe(mainActivity) {
                fragmentTodoDetailPublicOwnerBinding.textViewTodoDetailPublicOwnerDate.text = it.toString()
            }
            todoAlertTime.observe(mainActivity) {
                time = it.toString()
                if(time == "알림 없음") {
                    fragmentTodoDetailPublicOwnerBinding.textViewTodoDetailPublicOwnerAlert.text = "알림 없음"
                } else {
                    var alertTime = it.toString().split(":")
                    if (alertTime.get(0).toInt() >= 12) {
                        var hours = alertTime.get(0).toInt() - 12
                        fragmentTodoDetailPublicOwnerBinding.textViewTodoDetailPublicOwnerAlert.text =
                            "오후 ${hours}시 ${alertTime.get(1)}분"
                    } else {
                        fragmentTodoDetailPublicOwnerBinding.textViewTodoDetailPublicOwnerAlert.text =
                            "오전 ${alertTime.get(0)}시 ${alertTime.get(1)}분"
                    }
                }
            }
            todoLocationName.observe(mainActivity) {
                if(it.toString() == "위치 없음") {
                    fragmentTodoDetailPublicOwnerBinding.textViewTodoDetailPublicOwnerLocation.text = it.toString()
                } else {
                    fragmentTodoDetailPublicOwnerBinding.textViewTodoDetailPublicOwnerLocation.text =
                        it.toString().split("@").get(0)
                }
                placeAddress = todoDetailPersonalViewModel.todoLocationName.value.toString()
            }
            todoLocationLatitude.observe(mainActivity) {
                latitude = it.toString()
            }
            todoLocationLongitude.observe(mainActivity) {
                longitude = it.toString()
            }
            todoFontColor.observe(mainActivity) {
                fragmentTodoDetailPublicOwnerBinding.buttonTodoDetailPublicOwnerCategory.setTextColor(it.toInt())
            }
            todoBackgroundColor.observe(mainActivity) {
                fragmentTodoDetailPublicOwnerBinding.run {
                    buttonTodoDetailPublicOwnerCategory.setBackgroundColor(it.toInt())
                    textInputLayoutTodoDetailPublicOwner.boxStrokeColor = it.toInt()
                    textInputLayoutTodoDetailPublicOwner.hintTextColor = ColorStateList.valueOf(it.toInt())
                }
            }
        }
        todoDetailPersonalViewModel.getTodoInfo(todoIdx)

        fragmentTodoDetailPublicOwnerBinding.run {

            toolbarTodoDetailPublicOwner.run {
                title = "할일 상세"

                // back 버튼 설정
                setNavigationIcon(R.drawable.ic_arrow_back_24px)

                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.TODO_DETAIL_PUBLIC_OWNER_FRAGMENT)
                }
            }

            linearLayoutTodoDetailPublicOwnerDate.setOnClickListener {
                val materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
                materialDatePicker.addOnPositiveButtonClickListener {

                    //Show DateFormat
                    val dateformatter = SimpleDateFormat("yyyy-MM-dd")
                    val dates = dateformatter.format(Date(it))

                    textViewTodoDetailPublicOwnerDate.setText(dates)
                }

                materialDatePicker.show(mainActivity.supportFragmentManager,"Date")
            }

            linearLayoutTodoDetailPublicOwnerAlert.setOnClickListener {
                var today = Calendar.getInstance()
                var currentHour = today.get(Calendar.HOUR)
                var currentMinute = today.get(Calendar.MINUTE)
                var materialTimePicker = MaterialTimePicker.Builder()
                materialTimePicker
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setTitleText("Select Time")
                    .setHour(currentHour)
                    .setMinute(currentMinute)
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .build().apply {
                        addOnPositiveButtonClickListener {

                            var time = ""

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
                                textViewTodoDetailPublicOwnerAlert.text=  " 오후 ${hours}시 ${minute}분"
                            }else{
                                textViewTodoDetailPublicOwnerAlert.text= " 오전 ${hour}시 ${minute}분"
                            }
                        }
                    }
                    .show(mainActivity.supportFragmentManager,"Time")
            }

            linearLayoutTodoDetailPublicOwnerLocation.setOnClickListener {
                //구글맵 키 받아옴
                val key = com.test.dontforgetproject.BuildConfig.googlemapkey

                // plac api 초기화
                Places.initialize(context,key)
                val placesClient = Places.createClient(mainActivity)

                val field = listOf(
                    Place.Field.NAME,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS_COMPONENTS,
                    Place.Field.TYPES,
                    Place.Field.ADDRESS)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,field)
                    .setHint("주소를 입력해주세요")
                    .build(mainActivity)
                startAutocomplete.launch(intent)
            }


            buttonTodoDetailPublicOwnerEdit.setOnClickListener {

                var content = textInputEditTextTodoDetailPublicOwner.text.toString()
                var date = textViewTodoDetailPublicOwnerDate.text.toString()
                var time = time
                var locationName = placeAddress
                var locationLatitude = latitude
                var locationLongitude = longitude

                if(content.isEmpty()) {
                    var dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
                    val builder = MaterialAlertDialogBuilder(mainActivity)

                    dialogNormalBinding.textViewDialogNormalTitle.text = "경고"
                    dialogNormalBinding.textViewDialogNormalContent.text = "할일을 입력해주세요."

                    builder.setView(dialogNormalBinding.root)
//                    val builder = MaterialAlertDialogBuilder(mainActivity)
//                    builder.setMessage("할일을 입력해주세요.")
                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                        mainActivity.showSoftInput(textInputEditTextTodoDetailPublicOwner)
                    }
                    builder.show()

                    return@setOnClickListener
                }

                if(date.isEmpty()) {
                    var dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
                    val builder = MaterialAlertDialogBuilder(mainActivity)

                    dialogNormalBinding.textViewDialogNormalTitle.text = "경고"
                    dialogNormalBinding.textViewDialogNormalContent.text = "날짜를 선택해주세요."

                    builder.setView(dialogNormalBinding.root)
//                    val builder = MaterialAlertDialogBuilder(mainActivity)
//                    builder.setMessage("날짜를 선택해주세요.")
                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("확인", null)
                    builder.show()

                    return@setOnClickListener

                }

                val todoDataClass = TodoClass(
                    todoIdx,
                    content,
                    todoDetailPersonalViewModel.todoIsChecked.value!!.toLong(),
                    todoDetailPersonalViewModel.todoCategoryIdx.value!!.toLong(),
                    todoDetailPersonalViewModel.todoCategoryName.value.toString(),
                    todoDetailPersonalViewModel.todoFontColor.value!!.toLong(),
                    todoDetailPersonalViewModel.todoBackgroundColor.value!!.toLong(),
                    date,
                    time,
                    locationName,
                    locationLatitude,
                    locationLongitude,
                    todoDetailPersonalViewModel.todoOwnerIdx.value!!.toLong(),
                    todoDetailPersonalViewModel.todoOwnerName.value!!.toString()
                )

                var dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)

                dialogNormalBinding.textViewDialogNormalTitle.text = "경고"
                dialogNormalBinding.textViewDialogNormalContent.text = "수정하시면\n공유하고 있는 모든 인원에게\n변경되어 보여집니다."

                builder.setView(dialogNormalBinding.root)
//                val builder = MaterialAlertDialogBuilder(mainActivity)
//                builder.setTitle("경고")
//                builder.setMessage("수정하시면\n공유하고 있는 모든 인원에게\n변경되어 보여집니다.")
                builder.setNegativeButton("취소",null)
                builder.setPositiveButton("수정") { dialogInterface: DialogInterface, i: Int ->
                    TodoRepository.modifyTodo(todoDataClass) {
                    }


                    val now = Calendar.getInstance()
                    var alarmTime = "${date} $time:00" // 알람이 울리는 시간
                    var alarmDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") .parse(alarmTime)
                    var calculateDate = (alarmDate.time - now.time.time)
                    Log.d("lion", "time : $alarmTime")

//                val random = (1..100000) // 1~100000 범위에서 알람코드 랜덤으로 생성
                    var alarmCode = todoIdx.toInt()
                    Log.d("lion", "code : $alarmCode")
//                deleteAlarm(alarmCode)
                    if(calculateDate < 0) {
                        Log.d("lion", "현재보다 이전 시간으로 알림 설정")
                    } else {
                        setAlarm(alarmCode, content, alarmTime)
                    }

                    Toast.makeText(mainActivity, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    todoDetailPersonalViewModel.getTodoInfo(todoIdx)
                    mainActivity.removeFragment(TODO_DETAIL_PUBLIC_OWNER_FRAGMENT)
                }
                builder.show()
            }

            buttonTodoDetailPublicOwnerDelete.setOnClickListener {
                var dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)

                dialogNormalBinding.textViewDialogNormalTitle.text = "경고"
                dialogNormalBinding.textViewDialogNormalContent.text = "삭제하시면\n공유하고 있는 모든 인원에게\n삭제되어 보여지지 않습니다."

                builder.setView(dialogNormalBinding.root)
//                val builder = MaterialAlertDialogBuilder(mainActivity)
//                builder.setTitle("경고")
//                builder.setMessage("삭제하시면\n공유하고 있는 모든 인원에게\n삭제되어 보여지지 않습니다.")
                builder.setNegativeButton("취소",null)
                builder.setPositiveButton("삭제"){ dialogInterface: DialogInterface, i: Int ->
                    TodoRepository.removeTodo(todoIdx) {

                    }
                    Toast.makeText(mainActivity, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    mainActivity.removeFragment(MainActivity.TODO_DETAIL_PUBLIC_OWNER_FRAGMENT)
                }
                builder.show()
            }
        }
        return fragmentTodoDetailPublicOwnerBinding.root
    }

    private fun setAlarm(alarmCode : Int, content : String, time : String){
        alarmFunctions = AlarmFunctions(mainActivity)
        alarmFunctions.callAlarm(time, alarmCode, content)
    }
}