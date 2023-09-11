package com.test.dontforgetproject.UI.CategoryOptionPersonalFragment

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentCategoryOptionPersonalBinding
import com.test.dontforgetproject.databinding.RowMainCategoryBinding

class CategoryOptionPersonalFragment : Fragment() {
    lateinit var categoryOptionPersonalBinding: FragmentCategoryOptionPersonalBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryOptionPersonalBinding = FragmentCategoryOptionPersonalBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        categoryOptionPersonalBinding.run {
            toolbarCategoryOptionPersonal.run {
                title = "카테고리 관리"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.CATEGORY_OPTION_PERSONAL_FRAGMENT)
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

                buttonCategoryOptionPersonalModify.setOnClickListener {
                    mainActivity.removeFragment(MainActivity.CATEGORY_OPTION_PERSONAL_FRAGMENT)
                }

                buttonCategoryOptionPersonalDelete.setOnClickListener {
                    val builder = MaterialAlertDialogBuilder(mainActivity)
                    builder.setTitle("카테고리 삭제")
                    builder.setMessage("카테고리를 삭제하시겠습니까?\n메모도 같이 삭제됩니다.")
                    builder.setPositiveButton("삭제") { dialogInterface: DialogInterface, i: Int ->
                        mainActivity.removeFragment(MainActivity.CATEGORY_OPTION_PERSONAL_FRAGMENT)
                    }
                    builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->

                    }
                    builder.show()
                }
            }
        }

        return categoryOptionPersonalBinding.root
    }
}