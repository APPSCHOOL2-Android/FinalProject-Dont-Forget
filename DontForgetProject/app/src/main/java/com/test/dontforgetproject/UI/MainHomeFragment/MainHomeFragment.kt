package com.test.dontforgetproject.UI.MainHomeFragment

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Util.ThemeUtil
import com.test.dontforgetproject.databinding.FragmentMainHomeBinding
import com.test.dontforgetproject.databinding.RowCategoryTabBinding
import com.test.dontforgetproject.databinding.RowMemoSearchBinding
import com.test.dontforgetproject.databinding.RowTodoBinding

class MainHomeFragment : Fragment() {

    lateinit var binding: FragmentMainHomeBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        binding = FragmentMainHomeBinding.inflate(inflater, container, false)

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
                adapter = TodoRecyclerViewAdapter()
            }

            recyclerViewMainHomeFragmentMemoSearch.run {
                adapter = MemoSearchViewAdapter()
            }
            buttonMainHomeFragment.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.TODO_ADD_FRAGMENT, true, null)
            }
        }


        return binding.root
    }

    // 카테고리 탭
    inner class CategoryTabRecyclerViewAdapter :
        RecyclerView.Adapter<CategoryTabRecyclerViewAdapter.CategoryTabViewHolder>() {

        inner class CategoryTabViewHolder(private val binding: RowCategoryTabBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val textViewCategoryName = binding.textViewRowCategoryTab
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryTabViewHolder =
            CategoryTabViewHolder(
                RowCategoryTabBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun getItemCount(): Int = 10

        override fun onBindViewHolder(holder: CategoryTabViewHolder, position: Int) {
            holder.textViewCategoryName.text = "전체"
        }
    }

    // 할 일
    inner class TodoRecyclerViewAdapter :
        RecyclerView.Adapter<TodoRecyclerViewAdapter.TodoViewHolder>() {

        inner class TodoViewHolder(private val binding: RowTodoBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val checkBoxTodo = binding.checkBoxRowTodo
            val textViewTodo = binding.textViewRowTodo
            val textViewTodoMaker = binding.textViewRowTodoMaker

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

                textViewTodo.setOnClickListener {
                    // 개인, 공유(내가만듬), 공유(내가 안만듬) 분기 필요
                    mainActivity.replaceFragment(
                        MainActivity.TODO_DETAIL_PERSONAL_FRAGMENT,
                        true,
                        null
                    )
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder =
            TodoViewHolder(
                RowTodoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun getItemCount(): Int = 4

        override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
            holder.textViewTodo.text = "오전 8기 약먹기"
            holder.textViewTodoMaker.text = "by 누구"
        }
    }

    // 메모 검색
    inner class MemoSearchViewAdapter :
        RecyclerView.Adapter<MemoSearchViewAdapter.MemoSearchHolder>() {

        inner class MemoSearchHolder(private val binding: RowMemoSearchBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val textViewDate = binding.textViewRowMemoSearchDate
            val textViewCategory = binding.textViewRowMemoSearchCategory
            val checkBoxRowMemoSearch = binding.checkBoxRowMemoSearch
            val textViewRowMemoSearchMaker = binding.textViewRowMemoSearchMaker
            val textViewRowMemoSearch = binding.textViewRowMemoSearch

            init {
                checkBoxRowMemoSearch.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {

                    } else {

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
                RowMemoSearchBinding.inflate(
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
}