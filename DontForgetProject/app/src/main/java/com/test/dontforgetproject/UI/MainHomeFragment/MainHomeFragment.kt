package com.test.dontforgetproject.UI.MainHomeFragment

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.test.dontforgetproject.DAO.TodoClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.TodoRepository
import com.test.dontforgetproject.Util.LoadingDialog
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
    lateinit var loadingDialog: LoadingDialog
    lateinit var categoryIdxList: List<Long>

    var selectedCategoryPosition = 0
    lateinit var memoList: List<TodoClass>
    lateinit var selectedDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        binding = FragmentMainHomeBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireContext())

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

            todoList2.observe(mainActivity) {
                memoList = mainHomeViewModel.getTodo()
                binding.recyclerViewMainHomeFragmentMemoSearch.adapter?.notifyDataSetChanged()
            }
        }

        categoryIdxList =
            mainHomeViewModel.getCategoryAll(MyApplication.loginedUserInfo.userIdx, loadingDialog)
        setTodoData()
        mainHomeViewModel.getTodo(
            categoryIdxList
        )

        binding.run {
            textInputEditTextMainHomeFragment.run {
                doOnTextChanged { text, start, before, count ->
                    val newText = text.toString()
                    memoList = mainHomeViewModel.todoList2.value?.filter {
                        it.todoContent.contains(newText)
                    }!!

                    binding.recyclerViewMainHomeFragmentMemoSearch.adapter?.notifyDataSetChanged()
                }

                onFocusChangeListener =
                    View.OnFocusChangeListener { _, hasFocus ->
                        if (hasFocus) {
                            mainHomeViewModel.getTodo(
                                categoryIdxList
                            )
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
                            mainHomeViewModel.getTodoByDate(
                                selectedDate,
                                mainHomeViewModel.getCategoryAll(MyApplication.loginedUserInfo.userIdx, loadingDialog)
                            )
                            textInputLayoutMainHomeFragment.endIconMode =
                                TextInputLayout.END_ICON_NONE
                            scrollViewMainHomeFragment.visibility = View.VISIBLE
                            constraintLayoutMainHomeFragment.visibility = View.GONE
                        }
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

            setCalendar()
        }


        return binding.root
    }

    private fun setCalendar() {
        binding.run {
            calendarViewMainHomeFragment.setOnDateChangeListener { view, year, month, dayOfMonth ->
                selectedCategoryPosition = 0
                val formattedMonth = String.format("%02d", month + 1)
                selectedDate = "${year}-${formattedMonth}-${dayOfMonth}"

                // 고른 날에 맞는 todo가져오기
                mainHomeViewModel.getTodoByDate(
                    selectedDate,
                    mainHomeViewModel.getCategoryAll(MyApplication.loginedUserInfo.userIdx, loadingDialog)
                )
            }
        }
    }

    private fun setTodoData() {
        selectedDate = getCurrentDate()
        mainHomeViewModel.getTodoByDate(
            selectedDate,
            mainHomeViewModel.getCategoryAll(MyApplication.loginedUserInfo.userIdx, loadingDialog)
        )
    }

    private fun setTodoDataOtherDay() {
        mainHomeViewModel.getTodoByDate(
            selectedDate,
            mainHomeViewModel.getCategoryAll(MyApplication.loginedUserInfo.userIdx, loadingDialog)
        )
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
                    if (adapterPosition != 0) {
                        // 전체가 아닌 카테고리
                        mainHomeViewModel.getCategoryByCategoryIdx(
                            mainHomeViewModel.categories.value?.get(
                                adapterPosition - 1
                            )?.categoryIdx!!
                        )

                    } else {
                        // 전체 카테고리
                        mainHomeViewModel.getCategoryAll(
                            MyApplication.loginedUserInfo.userIdx,
                            loadingDialog
                        )
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

                // 포지션이 0이면서 선택된 상태인 경우 글자색을 흰색으로, 아닌 경우 검은색으로 설정
                val textColor = if (position == selectedCategoryPosition) {
                    Color.WHITE
                } else {
                    if (MyApplication.selectedTheme == ThemeUtil.DARK_MODE) {
                        Color.WHITE
                    } else {
                        Color.BLACK
                    }
                }
                holder.textViewCategoryName.setTextColor(textColor)

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

                    // 포지션이 0이 아니면서 선택된 상태인 경우 글자색을 흰색으로, 아닌 경우 검은색으로 설정
                    val textColor = if (position == selectedCategoryPosition) {
                        categories[dataIndex].categoryFontColor.toInt()
                    } else {
                        if (MyApplication.selectedTheme == ThemeUtil.DARK_MODE) {
                            Color.WHITE
                        } else {
                            Color.BLACK
                        }
                    }
                    holder.textViewCategoryName.setTextColor(textColor)

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
                adapter =
                    TodoRecyclerViewAdapter(todoListForCategory, isCategoryPublic!!)
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    // 할 일
    inner class TodoRecyclerViewAdapter(
        private val todoList: List<TodoClass>,
        private val isCategoryPublic: Long
    ) :
        RecyclerView.Adapter<TodoRecyclerViewAdapter.TodoViewHolder>() {

        inner class TodoViewHolder(private val binding: RowHomeTodoBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val checkBoxTodo = binding.checkBoxRowTodo
            val textViewTodo = binding.textViewRowTodo
            val textViewTodoMaker = binding.textViewRowTodoMaker
            val textViewRowTodoLocation = binding.textViewRowTodoLocation
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
                        if (MyApplication.loginedUserInfo.userIdx == todo.todoOwnerIdx) {
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
            holder.textViewRowTodoLocation.text = if (todo.todoLocationName == "위치 없음") {
                todo.todoLocationName
            } else {
                val parts = todo.todoLocationName.split("@")
                if (parts.size > 1) {
                    parts[1]
                } else {
                    todo.todoLocationName
                }
            }

            if (todo.todoIsChecked == 0L) {
                holder.checkBoxTodo.isChecked = false
                holder.textViewTodo.paintFlags =
                    holder.textViewTodo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                if (MyApplication.selectedTheme == ThemeUtil.DARK_MODE) {
                    holder.textViewTodo.setTextColor(resources.getColor(android.R.color.white))
                } else {
                    holder.textViewTodo.setTextColor(resources.getColor(android.R.color.black))
                }
            } else {
                holder.checkBoxTodo.isChecked = true
                holder.textViewTodo.paintFlags =
                    holder.textViewTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.textViewTodo.setTextColor(resources.getColor(R.color.accentGray))
            }

            holder.checkBoxTodo.setOnCheckedChangeListener { buttonView, isChecked ->
                val newTodoIsChecked: Long = if (isChecked) 1 else 0

                val todoDataClass = TodoClass(
                    todoIdx = todo.todoIdx,
                    todoContent = todo.todoContent,
                    todoIsChecked = newTodoIsChecked,
                    todoCategoryIdx = todo.todoCategoryIdx,
                    todoCategoryName = todo.todoCategoryName,
                    todoFontColor = todo.todoFontColor,
                    todoBackgroundColor = todo.todoBackgroundColor,
                    todoDate = todo.todoDate,
                    todoAlertTime = todo.todoAlertTime,
                    todoLocationName = todo.todoLocationName,
                    todoLocationLatitude = todo.todoLocationLatitude,
                    todoLocationLongitude = todo.todoLocationLongitude,
                    todoOwnerIdx = todo.todoOwnerIdx,
                    todoOwnerName = todo.todoOwnerName
                )

                TodoRepository.modifyTodo(todoDataClass) { task ->
                    memoList = mainHomeViewModel.getTodo()
                }

                if (isChecked) {
                    holder.textViewTodo.paintFlags =
                        holder.textViewTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    holder.textViewTodo.setTextColor(resources.getColor(R.color.accentGray))
                } else {
                    holder.textViewTodo.paintFlags =
                        holder.textViewTodo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    if (MyApplication.selectedTheme == ThemeUtil.DARK_MODE) {
                        holder.textViewTodo.setTextColor(resources.getColor(android.R.color.white))
                    } else {
                        holder.textViewTodo.setTextColor(resources.getColor(android.R.color.black))
                    }
                }
            }
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
            val textViewLocation = binding.textViewRowMemoSearchLocation
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoSearchHolder =
            MemoSearchHolder(
                RowHomeMemoSearchBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun getItemCount(): Int = memoList.size

        override fun onBindViewHolder(holder: MemoSearchHolder, position: Int) {
            val todo = memoList[position]
            val categories = mainHomeViewModel.getCategories()
            var isCategoryPublic: Long = 0
            for (i in categories) {
                if (todo.todoCategoryIdx == i.categoryIdx) {
                    isCategoryPublic = i.categoryIsPublic
                    holder.textViewCategory.setTextColor(i.categoryColor.toInt())
                }
            }

            holder.textViewDate.text = todo.todoDate
            holder.textViewCategory.text = todo.todoCategoryName
            holder.textViewRowMemoSearchMaker.text = "by ${todo.todoOwnerName}"
            holder.textViewLocation.text = if (todo.todoLocationName == "위치 없음") {
                todo.todoLocationName
            } else {
                val parts = todo.todoLocationName.split("@")
                if (parts.size > 1) {
                    parts[1]
                } else {
                    todo.todoLocationName
                }
            }

            holder.textViewRowMemoSearch.run {
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
                        if (MyApplication.loginedUserInfo.userIdx == todo.todoOwnerIdx) {
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

            holder.checkBoxRowMemoSearch.setOnCheckedChangeListener { buttonView, isChecked ->
                val newTodoIsChecked: Long = if (isChecked) 1 else 0

                val todoDataClass = TodoClass(
                    todoIdx = todo.todoIdx,
                    todoContent = todo.todoContent,
                    todoIsChecked = newTodoIsChecked,
                    todoCategoryIdx = todo.todoCategoryIdx,
                    todoCategoryName = todo.todoCategoryName,
                    todoFontColor = todo.todoFontColor,
                    todoBackgroundColor = todo.todoBackgroundColor,
                    todoDate = todo.todoDate,
                    todoAlertTime = todo.todoAlertTime,
                    todoLocationName = todo.todoLocationName,
                    todoLocationLatitude = todo.todoLocationLatitude,
                    todoLocationLongitude = todo.todoLocationLongitude,
                    todoOwnerIdx = todo.todoOwnerIdx,
                    todoOwnerName = todo.todoOwnerName
                )

                TodoRepository.modifyTodo(todoDataClass) { task ->
                }

                if (isChecked) {
                    holder.textViewRowMemoSearch.paintFlags =
                        holder.textViewRowMemoSearch.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    holder.textViewRowMemoSearch.setTextColor(resources.getColor(R.color.accentGray))
                } else {
                    holder.textViewRowMemoSearch.paintFlags =
                        holder.textViewRowMemoSearch.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    if (MyApplication.selectedTheme == ThemeUtil.DARK_MODE) {
                        holder.textViewRowMemoSearch.setTextColor(resources.getColor(android.R.color.white))
                    } else {
                        holder.textViewRowMemoSearch.setTextColor(resources.getColor(android.R.color.black))
                    }
                }
            }

            if (todo.todoIsChecked == 0L) {
                holder.checkBoxRowMemoSearch.isChecked = false
                holder.textViewRowMemoSearch.paintFlags =
                    holder.textViewRowMemoSearch.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                if (MyApplication.selectedTheme == ThemeUtil.DARK_MODE) {
                    holder.textViewRowMemoSearch.setTextColor(resources.getColor(android.R.color.white))
                } else {
                    holder.textViewRowMemoSearch.setTextColor(resources.getColor(android.R.color.black))
                }
            } else {
                holder.checkBoxRowMemoSearch.isChecked = true
                holder.textViewRowMemoSearch.paintFlags =
                    holder.textViewRowMemoSearch.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.textViewRowMemoSearch.setTextColor(resources.getColor(R.color.accentGray))
            }
        }
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    override fun onResume() {
        super.onResume()

        FirebaseDatabase.getInstance().reference
            .child("categoryInfo")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    // 데이터를 다시 로드하고 어댑터에 설정
                    categoryIdxList =
                        mainHomeViewModel.getCategoryAll(MyApplication.loginedUserInfo.userIdx, loadingDialog)
                    val currentDate = getCurrentDate()
                    if (currentDate == selectedDate) {
                        setTodoData()
                    } else{
                        setTodoDataOtherDay()
                    }
                    mainHomeViewModel.getTodo(categoryIdxList)
                    setCalendar()
                }
            })

        FirebaseDatabase.getInstance().reference
            .child("todoInfo")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    // 데이터를 다시 로드하고 어댑터에 설정
                    categoryIdxList =
                        mainHomeViewModel.getCategoryAll(MyApplication.loginedUserInfo.userIdx, loadingDialog)
                    val currentDate = getCurrentDate()
                    if (currentDate == selectedDate) {
                        setTodoData()
                    } else{
                        setTodoDataOtherDay()
                    }
                    mainHomeViewModel.getTodo(categoryIdxList)
                    setCalendar()
                }
            })

        // 리사이클러뷰의 어댑터 초기화
        binding.recyclerViewMainHomeFragmentCategory.adapter = CategoryTabRecyclerViewAdapter()
        binding.recyclerViewMainHomeFragmentTodo.adapter = CategoryRecyclerViewAdapter()
        binding.recyclerViewMainHomeFragmentMemoSearch.adapter = MemoSearchViewAdapter()
    }
}