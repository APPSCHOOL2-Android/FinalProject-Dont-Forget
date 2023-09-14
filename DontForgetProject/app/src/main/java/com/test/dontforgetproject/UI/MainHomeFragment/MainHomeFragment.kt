package com.test.dontforgetproject.UI.MainHomeFragment

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.test.dontforgetproject.DAO.TodoClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Util.ThemeUtil
import com.test.dontforgetproject.databinding.FragmentMainHomeBinding
import com.test.dontforgetproject.databinding.RowHomeCategoryBinding
import com.test.dontforgetproject.databinding.RowHomeCategoryTabBinding
import com.test.dontforgetproject.databinding.RowHomeMemoSearchBinding
import com.test.dontforgetproject.databinding.RowHomeTodoBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainHomeFragment : Fragment() {

    lateinit var binding: FragmentMainHomeBinding
    lateinit var mainActivity: MainActivity
    lateinit var mainHomeViewModel: MainHomeViewModel

    var selectedCategoryPosition = 0
    lateinit var selectedDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        binding = FragmentMainHomeBinding.inflate(inflater, container, false)

        mainHomeViewModel = ViewModelProvider(this)[MainHomeViewModel::class.java]
        mainHomeViewModel.run {
            categories.observe(mainActivity) {
                binding.recyclerViewMainHomeFragmentCategory.adapter?.notifyDataSetChanged()
                binding.recyclerViewMainHomeFragmentTodo.adapter?.notifyDataSetChanged()
            }

            categories2.observe(mainActivity) {
                binding.recyclerViewMainHomeFragmentCategory.adapter?.notifyDataSetChanged()
                binding.recyclerViewMainHomeFragmentTodo.adapter?.notifyDataSetChanged()
            }

            todoList.observe(mainActivity) {
                binding.recyclerViewMainHomeFragmentCategory.adapter?.notifyDataSetChanged()
                binding.recyclerViewMainHomeFragmentTodo.adapter?.notifyDataSetChanged()
            }
        }

        selectedDate = getCurrentDate()
        mainHomeViewModel.getTodoByDate(
            selectedDate,
            mainHomeViewModel.getCategoryAll(MyApplication.loginedUserInfo.userIdx)
        )
        Log.d("asdasdasd", selectedDate)

        binding.run {
            textInputEditTextMainHomeFragment.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        textInputLayoutMainHomeFragment.run {
                            endIconMode = TextInputLayout.END_ICON_CUSTOM
                            setEndIconDrawable(R.drawable.ic_close_24px)
                            setEndIconOnClickListener {
                                val inputMethodManager =
                                    mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                inputMethodManager.hideSoftInputFromWindow(
                                    textInputEditTextMainHomeFragment.windowToken,
                                    0
                                )
                                textInputEditTextMainHomeFragment.text = null
                                textInputEditTextMainHomeFragment.clearFocus()
                            }
                        }
                        scrollViewMainHomeFragment.visibility = View.GONE
                        constraintLayoutMainHomeFragment.visibility = View.VISIBLE
                    } else {
                        textInputLayoutMainHomeFragment.endIconMode = TextInputLayout.END_ICON_NONE
                        scrollViewMainHomeFragment.visibility = View.VISIBLE
                        constraintLayoutMainHomeFragment.visibility = View.GONE
                    }
                }

            recyclerViewMainHomeFragmentCategory.run {
                adapter = CategoryTabRecyclerViewAdapter()
            }

            recyclerViewMainHomeFragmentTodo.run {
                adapter = CategoryRecyclerViewAdapter()
            }

            recyclerViewMainHomeFragmentMemoSearch.run {
                adapter = MemoSearchViewAdapter()
            }

            buttonMainHomeFragment.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.TODO_ADD_FRAGMENT, true, null)
            }

            calendarViewMainHomeFragment.setOnDateChangeListener { view, year, month, dayOfMonth ->
                selectedCategoryPosition = 0
                val formattedMonth = String.format("%02d", month + 1)
                selectedDate = "${year}-${formattedMonth}-${dayOfMonth}"
                Log.d("asdasdasd", selectedDate)

                // 고른 날에 맞는 todo가져오기
                mainHomeViewModel.getTodoByDate(
                    selectedDate,
                    mainHomeViewModel.getCategoryAll(MyApplication.loginedUserInfo.userIdx)
                )
            }

            mainHomeViewModel.getCategoryAll(MyApplication.loginedUserInfo.userIdx)
        }


        return binding.root
    }

    // 카테고리 탭
    inner class CategoryTabRecyclerViewAdapter :
        RecyclerView.Adapter<CategoryTabRecyclerViewAdapter.CategoryTabViewHolder>() {

        inner class CategoryTabViewHolder(private val binding: RowHomeCategoryTabBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val textViewCategoryName = binding.textViewRowCategoryTab
            val cardViewRowCategoryTab = binding.cardViewRowCategoryTab

            init {
                binding.root.setOnClickListener {
                    Log.d("asdasdasd", "탭 인덱스 : ${adapterPosition}")
                    if (adapterPosition != 0) {
                        // 전체가 아닌 카테고리
                        Log.d(
                            "asdasdasd",
                            "카테고리 인덱스 : ${mainHomeViewModel.categories.value?.get(adapterPosition - 1)?.categoryIdx}"
                        )
                        mainHomeViewModel.getCategoryByCategoryIdx(
                            mainHomeViewModel.categories.value?.get(
                                adapterPosition - 1
                            )?.categoryIdx!!
                        )

                    } else {
                        // 전체 카테고리
                        mainHomeViewModel.getCategoryAll(MyApplication.loginedUserInfo.userIdx)
                    }
                    val position = adapterPosition

                    // 이전에 선택한 항목의 배경색을 원래대로 돌려놓음
                    val previousSelectedPosition = selectedCategoryPosition
                    selectedCategoryPosition = position

                    // 클릭한 항목의 배경색을 변경하고 위치를 추적
                    notifyItemChanged(position)
                    notifyItemChanged(previousSelectedPosition)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryTabViewHolder =
            CategoryTabViewHolder(
                RowHomeCategoryTabBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun getItemCount(): Int {
            // 첫 번째 항목에 고정된 값을 추가하려면 +1을 해줍니다.
            return mainHomeViewModel.categories.value?.size!! + 1
        }

        override fun onBindViewHolder(holder: CategoryTabViewHolder, position: Int) {
            // 첫 번째 항목에 고정된 값을 설정
            if (position == 0) {
                holder.textViewCategoryName.text = "전체"
                val backgroundColor = if (position == selectedCategoryPosition) {
                    ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary)
                } else {
                    ContextCompat.getColor(holder.itemView.context, R.color.transparent)
                }
                holder.cardViewRowCategoryTab.setCardBackgroundColor(backgroundColor)
            } else {
                // 데이터가 존재하는 경우에만 데이터를 설정
                mainHomeViewModel.categories.value?.let { categories ->
                    val dataIndex = position - 1 // 첫 번째 항목을 제외한 위치
                    holder.textViewCategoryName.text = categories[dataIndex].categoryName

                    val backgroundColor = if (position == selectedCategoryPosition) {
                        categories[dataIndex].categoryColor.toInt()
                    } else {
                        ContextCompat.getColor(holder.itemView.context, R.color.transparent)
                    }
                    holder.cardViewRowCategoryTab.setCardBackgroundColor(backgroundColor)
                }
            }
        }
    }

    // 카테고리
    inner class CategoryRecyclerViewAdapter :
        RecyclerView.Adapter<CategoryRecyclerViewAdapter.CategoryViewHolder>() {
        inner class CategoryViewHolder(private val binding: RowHomeCategoryBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val textViewCategory = binding.textViewRowHomeCategoryCategoryName
            val recyclerViewRowHomeCategory = binding.recyclerViewRowHomeCategory
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder =
            CategoryViewHolder(
                RowHomeCategoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun getItemCount(): Int = mainHomeViewModel.categories2.value?.size!!

        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            holder.textViewCategory.run {
                val category = mainHomeViewModel.categories2.value?.get(position)
                text = category?.categoryName
                val color = category?.categoryColor?.toInt() ?: 0
                setTextColor(color)
            }
            holder.recyclerViewRowHomeCategory.run {
                val categoryIdx =
                    mainHomeViewModel.categories2.value?.get(position)?.categoryIdx ?: -1
                val todoListForCategory = mainHomeViewModel.getTodoListForCategory(categoryIdx)
                val isCategoryPublic =
                    mainHomeViewModel.categories2.value?.get(position)?.categoryIsPublic
                val ownerIdx = mainHomeViewModel.categories2.value?.get(position)?.categoryOwnerIdx
                adapter =
                    TodoRecyclerViewAdapter(todoListForCategory, isCategoryPublic!!, ownerIdx!!)
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    // 할 일
    inner class TodoRecyclerViewAdapter(
        private val todoList: List<TodoClass>,
        private val isCategoryPublic: Long,
        private val ownerIdx: Long
    ) :
        RecyclerView.Adapter<TodoRecyclerViewAdapter.TodoViewHolder>() {

        inner class TodoViewHolder(private val binding: RowHomeTodoBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val checkBoxTodo = binding.checkBoxRowTodo
            val textViewTodo = binding.textViewRowTodo
            val textViewTodoMaker = binding.textViewRowTodoMaker
            val textViewRowTodoLocation = binding.textViewRowTodoLocation

            init {
                checkBoxTodo.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        textViewTodo.paintFlags =
                            textViewTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        textViewTodo.setTextColor(resources.getColor(R.color.accentGray))
                    } else {
                        textViewTodo.paintFlags =
                            textViewTodo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                        if (MyApplication.selectedTheme == ThemeUtil.DARK_MODE) {
                            textViewTodo.setTextColor(resources.getColor(android.R.color.white))
                        } else {
                            textViewTodo.setTextColor(resources.getColor(android.R.color.black))
                        }
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder =
            TodoViewHolder(
                RowHomeTodoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun getItemCount(): Int = todoList.size

        override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
            Log.d(
                "asdasdasd",
                "인덱스 : ${mainHomeViewModel.todoList.value?.get(position)?.todoCategoryIdx}"
            )
            val todo = todoList[position]
            holder.textViewTodo.run {
                text = todo.todoContent
                setOnClickListener {
                    val bundle = Bundle()
                    bundle.putLong("todoIdx", todo.todoIdx)
                    if (isCategoryPublic == 0L) {
                        mainActivity.replaceFragment(
                            MainActivity.TODO_DETAIL_PERSONAL_FRAGMENT,
                            true,
                            bundle
                        )
                    } else {
                        if (MyApplication.loginedUserInfo.userIdx == ownerIdx) {
                            mainActivity.replaceFragment(
                                MainActivity.TODO_DETAIL_PUBLIC_OWNER_FRAGMENT,
                                true,
                                bundle
                            )
                        } else {
                            mainActivity.replaceFragment(
                                MainActivity.TODO_DETAIL_PUBLIC_FRAGMENT,
                                true,
                                bundle
                            )
                        }
                    }
                }
            }
            holder.textViewTodoMaker.text = "by ${todo.todoOwnerName}"
            holder.textViewRowTodoLocation.text = todo.todoLocationName
        }
    }

    // 메모 검색
    inner class MemoSearchViewAdapter :
        RecyclerView.Adapter<MemoSearchViewAdapter.MemoSearchHolder>() {

        inner class MemoSearchHolder(private val binding: RowHomeMemoSearchBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val textViewDate = binding.textViewRowMemoSearchDate
            val textViewCategory = binding.textViewRowMemoSearchCategory
            val checkBoxRowMemoSearch = binding.checkBoxRowMemoSearch
            val textViewRowMemoSearchMaker = binding.textViewRowMemoSearchMaker
            val textViewRowMemoSearch = binding.textViewRowMemoSearch

            init {
                checkBoxRowMemoSearch.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        textViewRowMemoSearch.paintFlags =
                            textViewRowMemoSearch.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        textViewRowMemoSearch.setTextColor(resources.getColor(R.color.accentGray))
                    } else {
                        textViewRowMemoSearch.paintFlags =
                            textViewRowMemoSearch.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                        if (MyApplication.selectedTheme == ThemeUtil.DARK_MODE) {
                            textViewRowMemoSearch.setTextColor(resources.getColor(android.R.color.white))
                        } else {
                            textViewRowMemoSearch.setTextColor(resources.getColor(android.R.color.black))
                        }
                    }
                }

                textViewRowMemoSearch.setOnClickListener {
                    // 개인, 공유(내가만듬), 공유(내가 안만듬) 분기 필요
                    mainActivity.replaceFragment(
                        MainActivity.TODO_DETAIL_PERSONAL_FRAGMENT,
                        true,
                        null
                    )
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoSearchHolder =
            MemoSearchHolder(
                RowHomeMemoSearchBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun getItemCount(): Int = 3

        override fun onBindViewHolder(holder: MemoSearchHolder, position: Int) {
            holder.textViewDate.text = "23.09.05"
            holder.textViewCategory.text = "6팀 최종프로젝트"
            holder.textViewRowMemoSearchMaker.text = "by 누구"
            holder.textViewRowMemoSearch.text = "3시 강사님과 미팅"
        }
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
}