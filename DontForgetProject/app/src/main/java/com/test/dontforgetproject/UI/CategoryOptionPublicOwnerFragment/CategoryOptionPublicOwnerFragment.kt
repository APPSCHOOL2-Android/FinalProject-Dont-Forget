package com.test.dontforgetproject.UI.CategoryOptionPublicOwnerFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
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
        categoryOptionPublicOwnerBinding = FragmentCategoryOptionPublicOwnerBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        categoryOptionPublicOwnerBinding.run {
            toolbarCategoryOptionPublicOwner.run {
                title = "카테고리 관리"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
            }

            recyclerViewCategoryOptionPublicOwner.run {
                adapter = CategoryOptionPublicRecyclerViewAdpater()
                layoutManager = LinearLayoutManager(context)
            }
        }

        return categoryOptionPublicOwnerBinding.root
    }

    inner class CategoryOptionPublicRecyclerViewAdpater : RecyclerView.Adapter<CategoryOptionPublicRecyclerViewAdpater.CategoryOptionPublicViewHolder>() {
        inner class CategoryOptionPublicViewHolder(rowCategoryOptionPublicOwnerBinding: RowCategoryOptionPublicOwnerBinding) :
            RecyclerView.ViewHolder(rowCategoryOptionPublicOwnerBinding.root) {

            var userName: TextView

            init {
                userName = rowCategoryOptionPublicOwnerBinding.textViewRowCategoryOptionPublicOwnerName
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryOptionPublicViewHolder {
            val rowCategoryOptionPublicOwnerBinding = RowCategoryOptionPublicOwnerBinding.inflate(layoutInflater)
            val categoryOptionPublicViewHolder = CategoryOptionPublicViewHolder(rowCategoryOptionPublicOwnerBinding)

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