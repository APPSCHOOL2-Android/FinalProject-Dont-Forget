package com.test.dontforgetproject.UI.MainHomeFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.databinding.FragmentMainHomeBinding
import com.test.dontforgetproject.databinding.RowCategoryTabBinding
import com.test.dontforgetproject.databinding.RowTodoBinding

class MainHomeFragment : Fragment() {

    lateinit var binding:FragmentMainHomeBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        binding = FragmentMainHomeBinding.inflate(inflater, container, false)

        binding.run {
            recyclerViewMainHomeFragmentCategory.run {
                adapter = CategoryTabRecyclerViewAdapter()
            }

            recyclerViewMainHomeFragmentTodo.run {
                adapter = TodoRecyclerViewAdapter()
            }
        }


        return binding.root
    }

    // 카테고리 탭
    inner class CategoryTabRecyclerViewAdapter :
        RecyclerView.Adapter<CategoryTabRecyclerViewAdapter.CategoryTabViewHolder>() {

        inner class CategoryTabViewHolder(private val binding: RowCategoryTabBinding) :
            RecyclerView.ViewHolder(binding.root) {
                val textViewCategoryName = binding.textViewItemCategoryTab
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
            val checkBoxTodo = binding.checkBoxItemTodo
            val textViewTodoMaker = binding.textViewItemTodoMaker
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
            holder.checkBoxTodo.text = "오전 8기 약먹기"
            holder.textViewTodoMaker.text = "by 누구"
        }
    }
}