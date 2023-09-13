package com.test.dontforgetproject.UI.CategoryAddPersonalFragment

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.dontforgetproject.DAO.CategoryClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.databinding.DialogCategoryNormalBinding
import com.test.dontforgetproject.databinding.FragmentCategoryAddPersonalBinding


class CategoryAddPersonalFragment : Fragment() {
    lateinit var categoryAddPersonalBinding: FragmentCategoryAddPersonalBinding
    lateinit var mainActivity: MainActivity

    val userInfo = MyApplication.loginedUserInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryAddPersonalBinding = FragmentCategoryAddPersonalBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        categoryAddPersonalBinding.run {
            toolbarCategoryAddPersonal.run {
                title = "개인 카테고리 추가"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.CATEGORY_ADD_PERSONAL_FRAGMENT)
                }
            }

            textInputCategoryAddPersonalName.run {
                val color = ContextCompat.getColor(context, R.color.category1)
                boxStrokeColor = color
                hintTextColor = ColorStateList.valueOf(color)
            }

            editTextCategoryAddPersonalName.run {
                setTextColor(ContextCompat.getColor(context, R.color.category1))
            }

            textViewCategoryAddPersonalColorPicker.setOnClickListener {
                MaterialColorPickerDialog
                    .Builder(mainActivity)        					// Pass Activity Instance
                    .setTitle("색상")           		// Default "Choose Color"
                    .setColorShape(ColorShape.CIRCLE)   	// Default ColorShape.CIRCLE
                    .setColorSwatch(ColorSwatch._300)   	// Default ColorSwatch._500
                    .setDefaultColor(R.color.category1) 		// Pass Default Color
                    .setColorRes(resources.getIntArray(R.array.colors))
                    .setColorListener { color, colorHex ->
                        textViewCategoryAddPersonalColorPicker.backgroundTintList = ColorStateList.valueOf(color)
                        textInputCategoryAddPersonalName.boxStrokeColor = color
                        textInputCategoryAddPersonalName.hintTextColor = ColorStateList.valueOf(color)
                        editTextCategoryAddPersonalName.setTextColor(color)
                    }
                    .showBottomSheet(childFragmentManager)
            }

            // 만들기 버튼
            buttonCategoryAddPersonalSubmit.setOnClickListener {
                val categoryName = editTextCategoryAddPersonalName.text.toString()
                val categoryColor = editTextCategoryAddPersonalName.currentTextColor
                var categoryFontColor = Color.BLACK
                if (categoryColor == -12352444 || categoryColor == -16744538) {
                    categoryFontColor = Color.WHITE
                }
                val categoryJoinUserIdxList = ArrayList<Long>()
                categoryJoinUserIdxList.add(userInfo.userIdx)
                val categoryJoinUserNameList = ArrayList<String>()
                categoryJoinUserNameList.add(userInfo.userName)

                if (categoryName.isEmpty()) {
                    val dialogCategoryNormalBinding = DialogCategoryNormalBinding.inflate(layoutInflater)
                    val builder = MaterialAlertDialogBuilder(mainActivity)

                    dialogCategoryNormalBinding.textViewDialogCategoryTitle.text = "카테고리 이름 오류"
                    dialogCategoryNormalBinding.textViewDialogCategoryContent.text = "카테고리 이름을 입력하세요."

                    builder.setView(dialogCategoryNormalBinding.root)
                    builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                    }
                    builder.show()

                    return@setOnClickListener
                }

                // 카테고리 idx 가져오기
                CategoryRepository.getCategoryIdx {
                    var categoryIdx = it.result.value as Long
                    categoryIdx++

                    val categoryClass = CategoryClass(
                        categoryIdx,
                        categoryName,
                        categoryColor.toLong(),
                        categoryFontColor.toLong(),
                        categoryJoinUserIdxList,
                        categoryJoinUserNameList,
                        0,
                        userInfo.userIdx,
                        userInfo.userName
                    )

                    // 카테고리 객체 저장
                    CategoryRepository.addCategoryInfo(categoryClass) {
                        // 카테고리 idx 저장
                        CategoryRepository.setCategoryIdx(categoryIdx) {
                            mainActivity.removeFragment(MainActivity.CATEGORY_ADD_PERSONAL_FRAGMENT)
                        }
                    }
                }
            }
        }

        return categoryAddPersonalBinding.root
    }
}