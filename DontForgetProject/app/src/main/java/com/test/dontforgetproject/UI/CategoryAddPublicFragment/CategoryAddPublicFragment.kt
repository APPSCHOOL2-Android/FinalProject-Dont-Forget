package com.test.dontforgetproject.UI.CategoryAddPublicFragment

import android.content.ClipData
import android.content.ClipData.Item
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.github.dhaval2404.colorpicker.util.ColorUtil.parseColor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.test.dontforgetproject.DAO.AlertClass
import com.test.dontforgetproject.DAO.CategoryClass
import com.test.dontforgetproject.DAO.Friend
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.AlertRepository
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.UI.CategoryOptionPublicOwnerFragment.CategoryOptionPublicOwnerFragment
import com.test.dontforgetproject.databinding.DialogCategoryAddPeopleBinding
import com.test.dontforgetproject.databinding.DialogCategoryNormalBinding
import com.test.dontforgetproject.databinding.FragmentCategoryAddPublicBinding
import com.test.dontforgetproject.databinding.RowCategoryOptionPublicOwnerBinding
import com.test.dontforgetproject.databinding.RowDialogCategoryAddPeopleBinding
import com.test.dontforgetproject.databinding.RowMainCategoryBinding

class CategoryAddPublicFragment : Fragment() {
    lateinit var categoryAddPublicBinding: FragmentCategoryAddPublicBinding
    lateinit var mainActivity: MainActivity

    lateinit var categoryAddPublicViewModel: CategoryAddPublicViewModel

    val userInfo = MyApplication.loginedUserInfo

    companion object {
        private const val COLOR_SELECTED = "selectedColor"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryAddPublicBinding = FragmentCategoryAddPublicBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        categoryAddPublicViewModel =
            ViewModelProvider(mainActivity)[CategoryAddPublicViewModel::class.java]
        categoryAddPublicViewModel.run {
            peopleList.observe(mainActivity) {
                categoryAddPublicBinding.recyclerViewCategoryAddPublic.adapter?.notifyDataSetChanged()
            }
        }

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

            // 색상 선택
            textViewCategoryAddPublicColorPicker.setOnClickListener {
                MaterialColorPickerDialog
                    .Builder(mainActivity)                            // Pass Activity Instance
                    .setTitle("색상")                // Default "Choose Color"
                    .setColorShape(ColorShape.CIRCLE)    // Default ColorShape.CIRCLE
                    .setColorSwatch(ColorSwatch._300)    // Default ColorSwatch._500
                    .setDefaultColor(R.color.category1)        // Pass Default Color
                    .setColorRes(resources.getIntArray(R.array.colors))
                    .setColorListener { color, colorHex ->
                        textViewCategoryAddPublicColorPicker.backgroundTintList =
                            ColorStateList.valueOf(color)
                        textInputCategoryAddPublicName.boxStrokeColor = color
                        textInputCategoryAddPublicName.hintTextColor = ColorStateList.valueOf(color)
                        editTextCategoryAddPublicName.setTextColor(color)
                    }
                    .showBottomSheet(childFragmentManager)
            }

            // 인원 추가
            buttonCategoryAddPublicAdd.setOnClickListener {
                val dialogCategoryAddPeopleBinding =
                    DialogCategoryAddPeopleBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)

                val friendsList = arrayListOf<Friend>()
                friendsList.addAll(userInfo.userFriendList)
                friendsList.removeAt(0)

                // 참여 인원으로 이미 추가된 친구는 리스트에서 제외
                val friendsNotInList = ArrayList<Friend>()
                for (friend in friendsList) {
                    var notIn = true

                    for (p in categoryAddPublicViewModel.peopleList.value!!) {
                        if (friend.friendIdx == p.friendIdx) {
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
                    editTextDialogCategoryAddPeople.addTextChangedListener(object : TextWatcher {
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
                        addItemDecoration(
                            MaterialDividerItemDecoration(
                                context,
                                MaterialDividerItemDecoration.VERTICAL
                            )
                        )
                    }
                }

                builder.setView(dialogCategoryAddPeopleBinding.root)
                builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                    val addPeopleList = mutableListOf<Friend>()
                    for (selected in aAdapter.selectedItems) {
                        addPeopleList.add(aAdapter.filteredList[selected])
                    }
                    categoryAddPublicViewModel.addPeople(addPeopleList)
                }
                builder.show()
            }

            recyclerViewCategoryAddPublic.run {
                adapter = CategoryAddPublicRecyclerViewAdpater()
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }

            // 만들기
            buttonCategoryAddPublicSubmit.setOnClickListener {
                val categoryName = editTextCategoryAddPublicName.text.toString()

                val categoryColor = editTextCategoryAddPublicName.currentTextColor
                var categoryFontColor = Color.BLACK
                if (categoryColor == -12352444 || categoryColor == -16744538) {
                    categoryFontColor = Color.WHITE
                }

                val categoryJoinUserIdxList = ArrayList<Long>()
                categoryJoinUserIdxList.add(userInfo.userIdx)
                val categoryJoinUserNameList = ArrayList<String>()
                categoryJoinUserNameList.add(userInfo.userName)

                // 리스트에 참여인원 추가
                for (joinUser in categoryAddPublicViewModel.peopleList.value!!) {
                    categoryJoinUserIdxList.add(joinUser.friendIdx)
                    categoryJoinUserNameList.add(joinUser.friendName)
                }

                if (categoryName.isEmpty()) {
                    val dialogCategoryNormalBinding =
                        DialogCategoryNormalBinding.inflate(layoutInflater)
                    val builder = MaterialAlertDialogBuilder(mainActivity)

                    dialogCategoryNormalBinding.textViewDialogCategoryTitle.text = "카테고리 이름 오류"
                    dialogCategoryNormalBinding.textViewDialogCategoryContent.text =
                        "카테고리 이름을 입력하세요."

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
                        userInfo.userIdx,
                        userInfo.userName
                    )

                    // 카테고리 객체 저장
                    CategoryRepository.addCategoryInfo(categoryClass) {
                        // 카테고리 idx 저장
                        CategoryRepository.setCategoryIdx(categoryIdx) {
                            // 알림 idx 가져오기
                            AlertRepository.getAlertIdx {
                                var alertIdx = it.result.value as Long
                                val alertContent =
                                    "${userInfo.userName}님이 ${categoryName} 카테고리에 나를 추가했습니다."

                                for (idx in 1 until categoryJoinUserIdxList.size) {
                                    alertIdx++

                                    val alertClass = AlertClass(
                                        alertIdx,
                                        alertContent,
                                        categoryJoinUserIdxList[idx],
                                        1
                                    )

                                    // 알림 객체 저장
                                    AlertRepository.addAlertInfo(alertClass) {
                                        // 알림 idx 저장
                                        AlertRepository.setAlertIdx(alertIdx) {

                                        }
                                    }
                                }
                            }
                            mainActivity.removeFragment(MainActivity.CATEGORY_ADD_PUBLIC_FRAGMENT)
                        }
                    }
                }
            }
        }

        return categoryAddPublicBinding.root
    }

    // 인원추가 다이얼로그 리사이클러뷰 어댑터
    inner class AddPeopleRecyclerViewAdpater(
        var friendsList: ArrayList<Friend>,
        var context: Context
    ) : RecyclerView.Adapter<AddPeopleRecyclerViewAdpater.CategoryAddViewHolder>(), Filterable {
        var filteredList: ArrayList<Friend> = friendsList
        val selectedItems = HashSet<Int>()

        inner class CategoryAddViewHolder(rowDialogCategoryAddPeopleBinding: RowDialogCategoryAddPeopleBinding) :
            RecyclerView.ViewHolder(rowDialogCategoryAddPeopleBinding.root) {
            var friendName: TextView

            init {
                friendName =
                    rowDialogCategoryAddPeopleBinding.textViewRowDialogCategoryAddPeopleName

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
            val rowDialogCategoryAddPeopleBinding =
                RowDialogCategoryAddPeopleBinding.inflate(layoutInflater)
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
                                if (friend.friendName.lowercase()
                                        .contains(charString.lowercase())
                                ) {
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
                    filteredList = results.values as ArrayList<Friend>
                    notifyDataSetChanged()
                }
            }
        }
    }

    // 참여인원 리사이클러뷰 어댑터
    inner class CategoryAddPublicRecyclerViewAdpater :
        RecyclerView.Adapter<CategoryAddPublicRecyclerViewAdpater.CategoryOptionPublicViewHolder>() {
        inner class CategoryOptionPublicViewHolder(rowCategoryOptionPublicOwnerBinding: RowCategoryOptionPublicOwnerBinding) :
            RecyclerView.ViewHolder(rowCategoryOptionPublicOwnerBinding.root) {

            var userName: TextView

            init {
                userName =
                    rowCategoryOptionPublicOwnerBinding.textViewRowCategoryOptionPublicOwnerName

                // X 버튼 눌렀을 때
                rowCategoryOptionPublicOwnerBinding.buttonRowCategoryOptionPublicOwnerDelete.setOnClickListener {
                    categoryAddPublicViewModel.removePeople(adapterPosition)
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
            return categoryAddPublicViewModel.peopleList.value?.size!!
        }

        override fun onBindViewHolder(holder: CategoryOptionPublicViewHolder, position: Int) {
            holder.userName.text =
                categoryAddPublicViewModel.peopleList.value?.get(position)?.friendName
        }
    }

    override fun onResume() {
        super.onResume()
        categoryAddPublicViewModel.reset()
    }
}