package com.test.dontforgetproject.UI.MainCategoryFragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentMainCategoryBinding
import com.test.dontforgetproject.databinding.RowMainCategoryBinding
import java.text.DecimalFormat

class MainCategoryFragment : Fragment() {
    lateinit var mainCategoryBinding: FragmentMainCategoryBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainCategoryBinding = FragmentMainCategoryBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        mainCategoryBinding.run {
            toolbarMainCategory.run {
                title = getString(R.string.category)
                inflateMenu(R.menu.menu_main_category)
                setOnMenuItemClickListener {
                    val itemList = arrayOf("개인", "공용")
                    var checkedItem = ""
                    val builder = MaterialAlertDialogBuilder(mainActivity)
                    builder.setTitle("카테고리 종류 선택")
                    // builder.setSingleChoiceItems()
                    builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->

                    }
                    builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->

                    }
                    builder.show()

                    true
                }
            }
            recyclerViewMainCategory.run {
                adapter = MainCategoryRecyclerViewAdpater()
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(MaterialDividerItemDecoration(context, MaterialDividerItemDecoration.VERTICAL))
            }
        }

        return mainCategoryBinding.root
    }

    inner class MainCategoryRecyclerViewAdpater : RecyclerView.Adapter<MainCategoryRecyclerViewAdpater.MainCategoryViewHolder>() {
        inner class MainCategoryViewHolder(rowMainCategoryBinding: RowMainCategoryBinding) :
            RecyclerView.ViewHolder(rowMainCategoryBinding.root) {

            var categoryName: TextView

            init {
                categoryName = rowMainCategoryBinding.textViewRowMainCategoryCategoryName
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainCategoryViewHolder {
            val rowMainCategoryBinding = RowMainCategoryBinding.inflate(layoutInflater)
            val mainCategoryViewHolder = MainCategoryViewHolder(rowMainCategoryBinding)

            rowMainCategoryBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return mainCategoryViewHolder
        }

        override fun getItemCount(): Int {
            return 3
        }

        override fun onBindViewHolder(holder: MainCategoryViewHolder, position: Int) {
            holder.categoryName.text = "카테고리"
        }
    }
}