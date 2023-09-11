package com.test.dontforgetproject.UI.CategoryOptionPublicFragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentCategoryOptionPublicBinding
import com.test.dontforgetproject.databinding.RowCategoryOptionPublicBinding
import com.test.dontforgetproject.databinding.RowCategoryOptionPublicOwnerBinding

class CategoryOptionPublicFragment : Fragment() {
    lateinit var categoryOptionPublicBinding: FragmentCategoryOptionPublicBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryOptionPublicBinding = FragmentCategoryOptionPublicBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        categoryOptionPublicBinding.run {
            toolbarCategoryOptionPublic.run {
                title = "카테고리 관리"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.CATEGORY_OPTION_PUBLIC_FRAGMENT)
                }
            }
            recyclerViewCategoryOptionPublic.run {
                adapter = CategoryOptionPublicRecyclerViewAdpater()
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(MaterialDividerItemDecoration(context, MaterialDividerItemDecoration.VERTICAL))
            }

            buttonCategoryOptionPublicExit.setOnClickListener {
                val builder = MaterialAlertDialogBuilder(mainActivity)
                builder.setTitle("카테고리 나가기")
                builder.setMessage("카테고리에서 나가시겠습니까?\n메모도 같이 삭제됩니다.")
                builder.setPositiveButton("삭제") { dialogInterface: DialogInterface, i: Int ->
                    mainActivity.removeFragment(MainActivity.CATEGORY_OPTION_PUBLIC_FRAGMENT)
                }
                builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->

                }
                builder.show()
            }
        }

        return categoryOptionPublicBinding.root
    }

    inner class CategoryOptionPublicRecyclerViewAdpater : RecyclerView.Adapter<CategoryOptionPublicRecyclerViewAdpater.CategoryOptionPublicViewHolder>() {
        inner class CategoryOptionPublicViewHolder(rowCategoryOptionPublicBinding: RowCategoryOptionPublicBinding) :
            RecyclerView.ViewHolder(rowCategoryOptionPublicBinding.root) {

            var userName: TextView

            init {
                userName = rowCategoryOptionPublicBinding.textViewRowCategoryOptionPublicName
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryOptionPublicViewHolder {
            val rowCategoryOptionPublicBinding = RowCategoryOptionPublicBinding.inflate(layoutInflater)
            val categoryOptionPublicViewHolder = CategoryOptionPublicViewHolder(rowCategoryOptionPublicBinding)

            rowCategoryOptionPublicBinding.root.layoutParams = ViewGroup.LayoutParams(
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