package com.test.dontforgetproject.UI.CategoryAddPersonalFragment

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentCategoryAddPersonalBinding


class CategoryAddPersonalFragment : Fragment() {
    lateinit var categoryAddPersonalBinding: FragmentCategoryAddPersonalBinding
    lateinit var mainActivity: MainActivity

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
                            editTextCategoryAddPersonalName.setTextColor(color)
                        }
                        .showBottomSheet(childFragmentManager)
                }

                buttonCategoryAddPersonalSubmit.setOnClickListener {
                    mainActivity.removeFragment(MainActivity.CATEGORY_ADD_PERSONAL_FRAGMENT)
                }
            }
        }

        return categoryAddPersonalBinding.root
    }
}