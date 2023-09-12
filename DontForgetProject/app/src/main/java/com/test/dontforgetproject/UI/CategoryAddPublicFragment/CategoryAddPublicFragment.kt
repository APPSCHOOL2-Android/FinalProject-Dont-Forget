package com.test.dontforgetproject.UI.CategoryAddPublicFragment

import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.github.dhaval2404.colorpicker.util.ColorUtil.parseColor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.dontforgetproject.DAO.CategoryClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.databinding.DialogCategoryAddPeopleBinding
import com.test.dontforgetproject.databinding.DialogCategoryNormalBinding
import com.test.dontforgetproject.databinding.FragmentCategoryAddPublicBinding
import com.test.dontforgetproject.databinding.RowDialogCategoryAddPeopleBinding
import com.test.dontforgetproject.databinding.RowMainCategoryBinding

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
                title = "공용 카테고리 추가"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.CATEGORY_ADD_PUBLIC_FRAGMENT)
                }
            }

            textInputCategoryAddPublicName.run {
                val color = ContextCompat.getColor(context, R.color.category1)
                boxStrokeColor = color
                hintTextColor = ColorStateList.valueOf(color)
            }

            editTextCategoryAddPublicName.run {
                setTextColor(ContextCompat.getColor(context, R.color.category1))
            }

            textViewCategoryAddPublicColorPicker.setOnClickListener {
                MaterialColorPickerDialog
                    .Builder(mainActivity)        					// Pass Activity Instance
                    .setTitle("색상")           		// Default "Choose Color"
                    .setColorShape(ColorShape.CIRCLE)   	// Default ColorShape.CIRCLE
                    .setColorSwatch(ColorSwatch._300)   	// Default ColorSwatch._500
                    .setDefaultColor(R.color.category1) 		// Pass Default Color
                    .setColorRes(resources.getIntArray(R.array.colors))
                    .setColorListener { color, colorHex ->
                        textViewCategoryAddPublicColorPicker.backgroundTintList = ColorStateList.valueOf(color)
                        textInputCategoryAddPublicName.boxStrokeColor = color
                        editTextCategoryAddPublicName.setTextColor(color)
                    }
                    .showBottomSheet(childFragmentManager)
            }

            buttonCategoryAddPublicAdd.setOnClickListener {
                val dialogCategoryAddPeopleBinding = DialogCategoryAddPeopleBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)

                dialogCategoryAddPeopleBinding.run {
                    val friendsList = arrayListOf<String>("이주형","임성욱", "피유진", "신승헌", "이채연", "정채윤", "홍길동", "김민수")

                    val cAdapter = CategoryAddRecyclerViewAdpater(friendsList, mainActivity)

                    editTextDialogCategoryAddPeople.addTextChangedListener(object: TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            cAdapter.filter.filter(s)
                        }

                        override fun afterTextChanged(s: Editable?) {
                        }

                    })

                    recyclerViewDialogCategoryAddPeople.run {
                        adapter = cAdapter
                        layoutManager = LinearLayoutManager(context)
                    }
                }

                builder.setView(dialogCategoryAddPeopleBinding.root)
                builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                }
                builder.show()
            }

            buttonCategoryAddPublicSubmit.setOnClickListener {
                val categoryName = editTextCategoryAddPublicName.text.toString()
                val categoryColor = editTextCategoryAddPublicName.currentTextColor
                var categoryFontColor = Color.BLACK
                if (categoryColor == -12352444 || categoryColor == -16744538) {
                    categoryFontColor = Color.WHITE
                }
                val categoryJoinUserIdxList = ArrayList<Long>()
                categoryJoinUserIdxList.add(1)
                val categoryJoinUserNameList = ArrayList<String>()
                categoryJoinUserNameList.add("testA")

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
                        1,
                        1,
                        "testA"
                    )

                    // 카테고리 객체 저장
                    CategoryRepository.addCategoryInfo(categoryClass) {
                        // 카테고리 idx 저장
                        CategoryRepository.setCategoryIdx(categoryIdx) {
                            mainActivity.removeFragment(MainActivity.CATEGORY_ADD_PUBLIC_FRAGMENT)
                        }
                    }
                }
            }
        }

        return categoryAddPublicBinding.root
    }

    inner class CategoryAddRecyclerViewAdpater(var friendsList: ArrayList<String>, var context: Context) : RecyclerView.Adapter<CategoryAddRecyclerViewAdpater.CategoryAddViewHolder>(), Filterable {
        var filteredList: ArrayList<String> = friendsList

        inner class CategoryAddViewHolder(rowDialogCategoryAddPeopleBinding: RowDialogCategoryAddPeopleBinding) :
            RecyclerView.ViewHolder(rowDialogCategoryAddPeopleBinding.root) {
                var friendName: TextView

                init {
                    friendName = rowDialogCategoryAddPeopleBinding.textViewRowDialogCategoryAddPeopleName
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAddViewHolder {
            val rowDialogCategoryAddPeopleBinding = RowDialogCategoryAddPeopleBinding.inflate(layoutInflater)
            val categoryAddViewHolder = CategoryAddViewHolder(rowDialogCategoryAddPeopleBinding)

            rowDialogCategoryAddPeopleBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return categoryAddViewHolder
        }

        override fun getItemCount(): Int {
            return filteredList.size
        }

        override fun onBindViewHolder(holder: CategoryAddViewHolder, position: Int) {
            holder.friendName.text = filteredList.get(position)
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    val charString = constraint.toString()
                    filteredList = if (charString.isEmpty()) {
                        ArrayList(friendsList)
                    } else {
                        val filteredList = ArrayList<String>()
                        if (friendsList != null) {
                            for (name in friendsList) {
                                if(name.lowercase().contains(charString.lowercase())) {
                                    filteredList.add(name);
                                }
                            }
                        }
                        filteredList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = filteredList
                    return filterResults
                }

                override fun publishResults(constraint: CharSequence, results: FilterResults) {
                    filteredList  = results.values as ArrayList<String>
                    notifyDataSetChanged()
                }
            }
        }
    }
}