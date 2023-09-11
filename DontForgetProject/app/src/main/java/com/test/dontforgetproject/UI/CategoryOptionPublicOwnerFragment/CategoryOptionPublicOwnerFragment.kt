package com.test.dontforgetproject.UI.CategoryOptionPublicOwnerFragment

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentCategoryOptionPersonalBinding
import com.test.dontforgetproject.databinding.FragmentCategoryOptionPublicOwnerBinding
import com.test.dontforgetproject.databinding.RowCategoryOptionPublicOwnerBinding
import com.test.dontforgetproject.databinding.RowMainCategoryBinding


class CategoryOptionPublicOwnerFragment : Fragment() {
    lateinit var categoryOptionPublicOwnerBinding: FragmentCategoryOptionPublicOwnerBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryOptionPublicOwnerBinding =
            FragmentCategoryOptionPublicOwnerBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        categoryOptionPublicOwnerBinding.run {
            toolbarCategoryOptionPublicOwner.run {
                title = "카테고리 관리"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.CATEGORY_OPTION_PUBLIC_OWNER_FRAGMENT)
                }
            }

            recyclerViewCategoryOptionPublicOwner.run {
                adapter = CategoryOptionPublicRecyclerViewAdpater()
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }

            textViewCategoryOptionPublicOwnerColorPicker.setOnClickListener {
//                MaterialColorPickerDialog
//                    .Builder(mainActivity)        					// Pass Activity Instance
//                    .setTitle("색상")           		// Default "Choose Color"
//                    .setColorShape(ColorShape.CIRCLE)   	// Default ColorShape.CIRCLE
//                    .setColorSwatch(ColorSwatch._300)   	// Default ColorSwatch._500
//                    .setDefaultColor(R.color.category1) 		// Pass Default Color
//                    .setColorRes(resources.getIntArray(R.array.colors))
//                    .setColorListener { color, colorHex ->
//                        textViewCategoryOptionPublicOwnerColorPicker.backgroundTintList = ColorStateList.valueOf(color)
//                        textInputCategoryOptionPublicOwnerName.boxStrokeColor = color
//                        editTextCategoryOptionPublicOwnerName.setTextColor(color)
//                    }
//                    .showBottomSheet(childFragmentManager)
                setCategoryColor(
                    textViewCategoryOptionPublicOwnerColorPicker,
                    textInputCategoryOptionPublicOwnerName,
                    editTextCategoryOptionPublicOwnerName
                )
            }

            buttonCategoryOptionPublicOwnerModify.setOnClickListener {
                mainActivity.removeFragment(MainActivity.CATEGORY_OPTION_PUBLIC_OWNER_FRAGMENT)
            }

            buttonCategoryOptionPublicOwnerDelete.setOnClickListener {
                val builder = MaterialAlertDialogBuilder(mainActivity)
                builder.setTitle("카테고리 삭제")
                builder.setMessage("카테고리를 삭제하시겠습니까?\n참여인원의 캘린더에서도 삭제됩니다.")
                builder.setPositiveButton("삭제") { dialogInterface: DialogInterface, i: Int ->
                    mainActivity.removeFragment(MainActivity.CATEGORY_OPTION_PUBLIC_OWNER_FRAGMENT)
                }
                builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->

                }
                builder.show()
            }
        }

        return categoryOptionPublicOwnerBinding.root
    }

    fun setCategoryColor(colorPicker: TextView, textInput: TextInputLayout, editText: TextInputEditText) {
        MaterialColorPickerDialog
            .Builder(mainActivity)                            // Pass Activity Instance
            .setTitle("색상")                // Default "Choose Color"
            .setColorShape(ColorShape.CIRCLE)    // Default ColorShape.CIRCLE
            .setColorSwatch(ColorSwatch._300)    // Default ColorSwatch._500
            .setDefaultColor(R.color.category1)        // Pass Default Color
            .setColorRes(resources.getIntArray(R.array.colors))
            .setColorListener { color, colorHex ->
                colorPicker.backgroundTintList = ColorStateList.valueOf(color)
                textInput.boxStrokeColor = color
                editText.setTextColor(color)
            }
            .showBottomSheet(childFragmentManager)
    }

    inner class CategoryOptionPublicRecyclerViewAdpater :
        RecyclerView.Adapter<CategoryOptionPublicRecyclerViewAdpater.CategoryOptionPublicViewHolder>() {
        inner class CategoryOptionPublicViewHolder(rowCategoryOptionPublicOwnerBinding: RowCategoryOptionPublicOwnerBinding) :
            RecyclerView.ViewHolder(rowCategoryOptionPublicOwnerBinding.root) {

            var userName: TextView

            init {
                userName =
                    rowCategoryOptionPublicOwnerBinding.textViewRowCategoryOptionPublicOwnerName
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CategoryOptionPublicViewHolder {
            val rowCategoryOptionPublicOwnerBinding =
                RowCategoryOptionPublicOwnerBinding.inflate(layoutInflater)
            val categoryOptionPublicViewHolder =
                CategoryOptionPublicViewHolder(rowCategoryOptionPublicOwnerBinding)

            rowCategoryOptionPublicOwnerBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return categoryOptionPublicViewHolder
        }

        override fun getItemCount(): Int {
            return 7
        }

        override fun onBindViewHolder(holder: CategoryOptionPublicViewHolder, position: Int) {
            holder.userName.text = "이름"
        }
    }
}