package com.test.dontforgetproject.UI.CategoryAddPublicFragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.github.dhaval2404.colorpicker.util.ColorUtil.parseColor
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentCategoryAddPublicBinding

class CategoryAddPublicFragment : Fragment() {
    lateinit var categoryAddPublicBinding: FragmentCategoryAddPublicBinding
    lateinit var mainActivity: MainActivity

    companion object {
        private const val COLOR_SELECTED = "selectedColor"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryAddPublicBinding = FragmentCategoryAddPublicBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        categoryAddPublicBinding.run {
            toolbarCategoryAddPublic.run {
                title = "카테고리 추가"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
            }
            textViewCategoryAddPublicColorPicker.setOnClickListener {
                // Kotlin Code
                MaterialColorPickerDialog
                    .Builder(mainActivity)        					// Pass Activity Instance
                    .setTitle("색상선택")           		// Default "Choose Color"
                    .setColorShape(ColorShape.CIRCLE)   	// Default ColorShape.CIRCLE
                    .setColorSwatch(ColorSwatch._300)   	// Default ColorSwatch._500
                    .setDefaultColor(R.color.category1) 		// Pass Default Color
                    .setColorRes(resources.getIntArray(R.array.colors))
                    .setColorListener { color, colorHex ->
                        Log.i("ssss", color.toString())
                        textViewCategoryAddPublicColorPicker.backgroundTintList = ColorStateList.valueOf(color)
                    }
                    .showBottomSheet(childFragmentManager)

            }
        }

        return categoryAddPublicBinding.root
    }

}