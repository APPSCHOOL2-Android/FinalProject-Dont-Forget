package com.test.dontforgetproject.UI.CategoryOptionPublicOwnerFragment

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
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.test.dontforgetproject.DAO.CategoryClass
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.UI.CategoryOptionPublicFragment.CategoryOptionPublicViewModel
import com.test.dontforgetproject.databinding.DialogCategoryAddPeopleBinding
import com.test.dontforgetproject.databinding.DialogCategoryNormalBinding
import com.test.dontforgetproject.databinding.FragmentCategoryOptionPersonalBinding
import com.test.dontforgetproject.databinding.FragmentCategoryOptionPublicOwnerBinding
import com.test.dontforgetproject.databinding.RowCategoryOptionPublicOwnerBinding
import com.test.dontforgetproject.databinding.RowDialogCategoryAddPeopleBinding
import com.test.dontforgetproject.databinding.RowMainCategoryBinding


class CategoryOptionPublicOwnerFragment : Fragment() {
    lateinit var categoryOptionPublicOwnerBinding: FragmentCategoryOptionPublicOwnerBinding
    lateinit var mainActivity: MainActivity

    lateinit var categoryOptionPublicViewModel: CategoryOptionPublicViewModel

    val userInfo = MyApplication.loginedUserInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryOptionPublicOwnerBinding =
            FragmentCategoryOptionPublicOwnerBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        categoryOptionPublicViewModel = ViewModelProvider(mainActivity)[CategoryOptionPublicViewModel::class.java]
        categoryOptionPublicViewModel.run {
            categoryName.observe(mainActivity) {
                categoryOptionPublicOwnerBinding.editTextCategoryOptionPublicOwnerName.setText(it)
            }

            categoryColor.observe(mainActivity) {
                categoryOptionPublicOwnerBinding.run {
                    textInputCategoryOptionPublicOwnerName.run {
                        boxStrokeColor = it
                        hintTextColor = ColorStateList.valueOf(it)
                    }

                    editTextCategoryOptionPublicOwnerName.run {
                        setTextColor(it)
                    }

                    textViewCategoryOptionPublicOwnerColorPicker.backgroundTintList = ColorStateList.valueOf(it)
                }
            }

            categoryOwner.observe(mainActivity) {
                categoryOptionPublicOwnerBinding.textViewCategoryOptionPublicOwnerOwnerName.text = it
            }

            joinUserNameList.observe(mainActivity) {
                categoryOptionPublicOwnerBinding.recyclerViewCategoryOptionPublicOwner.adapter?.notifyDataSetChanged()
            }
        }

        categoryOptionPublicOwnerBinding.run {
            val categoryIdx = arguments?.getLong("categoryIdx")!!

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

            // 인원추가 버튼
            buttonCategoryOptionPublicOwnerAdd.setOnClickListener {
                val dialogCategoryAddPeopleBinding = DialogCategoryAddPeopleBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)

                val friendsList = arrayListOf<Friend>()
                friendsList.addAll(userInfo.userFriendList)
                friendsList.removeAt(0)

                // 참여 인원으로 이미 추가된 친구는 리스트에서 제외
                val friendsNotInList = ArrayList<Friend>()
                for (friend in friendsList) {
                    var notIn = true

                    for (joinUserIdx in categoryOptionPublicViewModel.joinUserIdxList.value!!) {
                        if (friend.friendIdx == joinUserIdx) {
                            notIn = false
                            continue
                        }
                    }

                    if (notIn == true) {
                        friendsNotInList.add(friend)
                    }
                }

                val aAdapter = AddPeopleRecyclerViewAdpater(friendsNotInList, mainActivity)

                dialogCategoryAddPeopleBinding.run {
                    // 텍스트 입력하면 검색되도록 설정
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
                            aAdapter.filter.filter(s)
                        }

                        override fun afterTextChanged(s: Editable?) {
                        }

                    })

                    recyclerViewDialogCategoryAddPeople.run {
                        adapter = aAdapter
                        layoutManager = LinearLayoutManager(context)
                        addItemDecoration(MaterialDividerItemDecoration(context, MaterialDividerItemDecoration.VERTICAL))
                    }
                }

                builder.setView(dialogCategoryAddPeopleBinding.root)
                builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                    val addPeopleIdxList = mutableListOf<Long>()
                    val addPeopleNameList = mutableListOf<String>()
                    for (selected in aAdapter.selectedItems) {
                        addPeopleIdxList.add(aAdapter.filteredList[selected].friendIdx)
                        addPeopleNameList.add(aAdapter.filteredList[selected].friendName)
                    }
                    categoryOptionPublicViewModel.addPeople(addPeopleIdxList, addPeopleNameList)
                }
                builder.show()
            }

            // 수정하기
            buttonCategoryOptionPublicOwnerModify.setOnClickListener {
                val categoryName = editTextCategoryOptionPublicOwnerName.text.toString()
                val categoryColor = editTextCategoryOptionPublicOwnerName.currentTextColor
                var categoryFontColor = Color.BLACK
                if (categoryColor == -12352444 || categoryColor == -16744538) {
                    categoryFontColor = Color.WHITE
                }

                val categoryJoinUserIdxList = ArrayList<Long>()
                categoryJoinUserIdxList.add(userInfo.userIdx)
                categoryJoinUserIdxList.addAll(categoryOptionPublicViewModel.joinUserIdxList?.value!!)

                val categoryJoinUserNameList = ArrayList<String>()
                categoryJoinUserNameList.add(userInfo.userName)
                categoryJoinUserNameList.addAll(categoryOptionPublicViewModel.joinUserNameList?.value!!)

                val categoryClass = CategoryClass(
                    categoryIdx,
                    categoryName,
                    categoryColor.toLong(),
                    categoryFontColor.toLong(),
                    categoryJoinUserIdxList,
                    categoryJoinUserNameList,
                    1,
                    userInfo.userIdx,
                    userInfo.userName
                )

                CategoryRepository.modifyCategory(categoryClass) {
                    Toast.makeText(mainActivity, "카테고리 수정 완료", Toast.LENGTH_SHORT)
                        .show()
                    mainActivity.removeFragment(MainActivity.CATEGORY_OPTION_PUBLIC_OWNER_FRAGMENT)
                }
            }

            // 삭제하기
            buttonCategoryOptionPublicOwnerDelete.setOnClickListener {
                val builder = MaterialAlertDialogBuilder(mainActivity)
                val dialogCategoryNormalBinding = DialogCategoryNormalBinding.inflate(layoutInflater)

                dialogCategoryNormalBinding.textViewDialogCategoryTitle.text = "경고"
                dialogCategoryNormalBinding.textViewDialogCategoryContent.text = "카테고리를 삭제하면 참여인원의 캘린더에서도 카테고리와 할일이 모두 삭제됩니다."

                builder.setView(dialogCategoryNormalBinding.root)
                builder.setPositiveButton("삭제") { dialogInterface: DialogInterface, i: Int ->
                    // 카테고리 삭제
                    CategoryRepository.removeCategory(categoryIdx) {
                        Toast.makeText(mainActivity, "카테고리 삭제 완료", Toast.LENGTH_SHORT)
                            .show()
                        mainActivity.removeFragment(MainActivity.CATEGORY_OPTION_PUBLIC_OWNER_FRAGMENT)
                    }
                    // 카테고리에 속한 할일도 삭제
                    CategoryRepository.removeTodoByCategoryIdx(categoryIdx) {

                    }
                }
                builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->

                }
                builder.show()
            }

            categoryOptionPublicViewModel.getCategoryInfo(categoryIdx)
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

    // 인원추가 다이얼로그 리사이클러뷰 어댑터
    inner class AddPeopleRecyclerViewAdpater(var friendsList: ArrayList<Friend>, var context: Context) : RecyclerView.Adapter<AddPeopleRecyclerViewAdpater.CategoryAddViewHolder>(),
        Filterable {
        var filteredList: ArrayList<Friend> = friendsList
        val selectedItems = HashSet<Int>()

        inner class CategoryAddViewHolder(rowDialogCategoryAddPeopleBinding: RowDialogCategoryAddPeopleBinding) :
            RecyclerView.ViewHolder(rowDialogCategoryAddPeopleBinding.root) {
            var friendName: TextView

            init {
                friendName = rowDialogCategoryAddPeopleBinding.textViewRowDialogCategoryAddPeopleName

                rowDialogCategoryAddPeopleBinding.root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        toggleSelection(position)
                    }
                    Log.i("selected", selectedItems.toString())
                }
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
            holder.friendName.text = filteredList[position].friendName
            holder.itemView.isSelected = selectedItems.contains(position)

            // 선택된 항목인 경우 시각적 스타일 변경
            if (selectedItems.contains(position)) {
                holder.itemView.setBackgroundColor(Color.parseColor("#74B1B1B1"))
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor("#00FFFFFF"))
            }
        }

        // 항목 선택/해제 토글
        private fun toggleSelection(position: Int) {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            notifyItemChanged(position)
        }

        // 검색을 위한 필터
        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    // 선택된 항목 초기화
                    selectedItems.clear()

                    val charString = constraint.toString()
                    filteredList = if (charString.isEmpty()) {
                        ArrayList(friendsList)
                    } else {
                        val filteredList = ArrayList<Friend>()
                        if (friendsList != null) {
                            for (friend in friendsList) {
                                if(friend.friendName.lowercase().contains(charString.lowercase())) {
                                    filteredList.add(friend);
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
                    filteredList  = results.values as ArrayList<Friend>
                    notifyDataSetChanged()
                }
            }
        }
    }

    inner class CategoryOptionPublicRecyclerViewAdpater :
        RecyclerView.Adapter<CategoryOptionPublicRecyclerViewAdpater.CategoryOptionPublicViewHolder>() {
        inner class CategoryOptionPublicViewHolder(rowCategoryOptionPublicOwnerBinding: RowCategoryOptionPublicOwnerBinding) :
            RecyclerView.ViewHolder(rowCategoryOptionPublicOwnerBinding.root) {

            var userName: TextView

            init {
                userName =
                    rowCategoryOptionPublicOwnerBinding.textViewRowCategoryOptionPublicOwnerName

                // X 버튼 눌렀을 때
                rowCategoryOptionPublicOwnerBinding.buttonRowCategoryOptionPublicOwnerDelete.setOnClickListener {
                    categoryOptionPublicViewModel.removePeople(adapterPosition)
                    notifyDataSetChanged()
                }
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
            return categoryOptionPublicViewModel.joinUserNameList.value?.size!!
        }

        override fun onBindViewHolder(holder: CategoryOptionPublicViewHolder, position: Int) {
            holder.userName.text = categoryOptionPublicViewModel.joinUserNameList.value?.get(position)!!
        }
    }

    override fun onResume() {
        super.onResume()
        categoryOptionPublicViewModel.reset()
    }
}