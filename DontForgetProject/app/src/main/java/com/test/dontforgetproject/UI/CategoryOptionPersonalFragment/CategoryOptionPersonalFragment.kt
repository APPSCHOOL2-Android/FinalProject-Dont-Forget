package com.test.dontforgetproject.UI.CategoryOptionPersonalFragment

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
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
import com.test.dontforgetproject.databinding.FragmentCategoryOptionPersonalBinding
import com.test.dontforgetproject.databinding.RowMainCategoryBinding

class CategoryOptionPersonalFragment : Fragment() {
    lateinit var fragmentCategoryOptionPersonalBinding: FragmentCategoryOptionPersonalBinding
    lateinit var mainActivity: MainActivity

    lateinit var categoryOptionPersonalViewModel: CategoryOptionPersonalViewModel

    val userInfo = MyApplication.loginedUserInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCategoryOptionPersonalBinding = FragmentCategoryOptionPersonalBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        categoryOptionPersonalViewModel = ViewModelProvider(mainActivity)[CategoryOptionPersonalViewModel::class.java]
        categoryOptionPersonalViewModel.run {
            categoryName.observe(mainActivity) {
                fragmentCategoryOptionPersonalBinding.editTextCategoryOptionPersonalName.setText(it)
            }

            categoryColor.observe(mainActivity) {
                fragmentCategoryOptionPersonalBinding.run {
                    textInputCategoryOptionPersonalName.run {
                        boxStrokeColor = it
                        hintTextColor = ColorStateList.valueOf(it)
                    }

                    editTextCategoryOptionPersonalName.run {
                        setTextColor(it)
                    }

                    textViewCategoryOptionPersonalColorPicker.backgroundTintList = ColorStateList.valueOf(it)
                }
            }
        }

        fragmentCategoryOptionPersonalBinding.run {
            val categoryIdx = arguments?.getLong("categoryIdx")!!

            toolbarCategoryOptionPersonal.run {
                title = "카테고리 관리"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.CATEGORY_OPTION_PERSONAL_FRAGMENT)
                }
            }

            textViewCategoryOptionPersonalColorPicker.setOnClickListener {
                MaterialColorPickerDialog
                    .Builder(mainActivity)        					// Pass Activity Instance
                    .setTitle("색상")           		// Default "Choose Color"
                    .setColorShape(ColorShape.CIRCLE)   	// Default ColorShape.CIRCLE
                    .setColorSwatch(ColorSwatch._300)   	// Default ColorSwatch._500
                    .setDefaultColor(R.color.category1) 		// Pass Default Color
                    .setColorRes(resources.getIntArray(R.array.colors))
                    .setColorListener { color, colorHex ->
                        textViewCategoryOptionPersonalColorPicker.backgroundTintList = ColorStateList.valueOf(color)
                        textInputCategoryOptionPersonalName.boxStrokeColor = color
                        editTextCategoryOptionPersonalName.setTextColor(color)
                    }
                    .showBottomSheet(childFragmentManager)
            }

            // 수정하기
            buttonCategoryOptionPersonalModify.setOnClickListener {
                val categoryName = editTextCategoryOptionPersonalName.text.toString()
                val categoryColor = editTextCategoryOptionPersonalName.currentTextColor
                var categoryFontColor = Color.BLACK
                if (categoryColor == -12352444 || categoryColor == -16744538) {
                    categoryFontColor = Color.WHITE
                }
                val categoryJoinUserIdxList = ArrayList<Long>()
                categoryJoinUserIdxList.add(userInfo.userIdx)
                val categoryJoinUserNameList = ArrayList<String>()
                categoryJoinUserNameList.add(userInfo.userName)

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

                CategoryRepository.modifyCategory(categoryClass) {
                    Toast.makeText(mainActivity, "카테고리 수정 완료", Toast.LENGTH_SHORT)
                        .show()
                    mainActivity.removeFragment(MainActivity.CATEGORY_OPTION_PERSONAL_FRAGMENT)
                }
            }

            // 삭제하기
            buttonCategoryOptionPersonalDelete.setOnClickListener {
                val dialogCategoryNormalBinding = DialogCategoryNormalBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)

                dialogCategoryNormalBinding.textViewDialogCategoryTitle.text = "경고"
                dialogCategoryNormalBinding.textViewDialogCategoryContent.text = "카테고리를 삭제하면 카테고리의 할일도 같이 삭제됩니다."

                builder.setView(dialogCategoryNormalBinding.root)
                builder.setPositiveButton("삭제") { dialogInterface: DialogInterface, i: Int ->
                    // 카테고리 삭제
                    CategoryRepository.removeCategory(categoryIdx) {
                        Toast.makeText(mainActivity, "카테고리 삭제 완료", Toast.LENGTH_SHORT)
                            .show()
                        mainActivity.removeFragment(MainActivity.CATEGORY_OPTION_PERSONAL_FRAGMENT)
                    }
                    // 카테고리에 속한 할일도 삭제
                    CategoryRepository.removeTodoByCategoryIdx(categoryIdx) {

                    }
                }
                builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->

                }
                builder.show()
            }

            // 카테고리 정보 가져오기
            categoryOptionPersonalViewModel.getCategoryInfo(categoryIdx)
        }

        return fragmentCategoryOptionPersonalBinding.root
    }
}