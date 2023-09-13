package com.test.dontforgetproject.UI.MainCategoryFragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.DialogCategoryAddBinding
import com.test.dontforgetproject.databinding.FragmentMainCategoryBinding
import com.test.dontforgetproject.databinding.RowMainCategoryBinding
import java.text.DecimalFormat

class MainCategoryFragment : Fragment() {
    lateinit var fragmentMainCategoryBinding: FragmentMainCategoryBinding
    lateinit var mainActivity: MainActivity

    lateinit var mainCategoryViewModel: MainCategoryViewModel

    var userIdx = MyApplication.loginedUserInfo.userIdx

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMainCategoryBinding = FragmentMainCategoryBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        mainCategoryViewModel = ViewModelProvider(mainActivity)[MainCategoryViewModel::class.java]
        mainCategoryViewModel.run {
            categoryList.observe(mainActivity) {
                fragmentMainCategoryBinding.recyclerViewMainCategory.adapter?.notifyDataSetChanged()
            }
        }

        fragmentMainCategoryBinding.run {
            toolbarMainCategory.run {
                title = getString(R.string.category)
                inflateMenu(R.menu.menu_main_category)
                setOnMenuItemClickListener {
                    val builder = MaterialAlertDialogBuilder(mainActivity)
                    val dialogCategoryAddBinding = DialogCategoryAddBinding.inflate(layoutInflater)

                    builder.setView(dialogCategoryAddBinding.root)

                    builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                        val checkedItem = dialogCategoryAddBinding.radioGroupCategoryType.checkedRadioButtonId
                        when (checkedItem) {
                            R.id.radioButtonPersonal -> {
                                mainActivity.replaceFragment(MainActivity.CATEGORY_ADD_PERSONAL_FRAGMENT, true, null)
                            }
                            R.id.radioButtonPublic -> {
                                mainActivity.replaceFragment(MainActivity.CATEGORY_ADD_PUBLIC_FRAGMENT, true, null)
                            }
                        }
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
            }

            mainCategoryViewModel.getMyCategory(userIdx)
        }

        return fragmentMainCategoryBinding.root
    }

    inner class MainCategoryRecyclerViewAdpater : RecyclerView.Adapter<MainCategoryRecyclerViewAdpater.MainCategoryViewHolder>() {
        inner class MainCategoryViewHolder(rowMainCategoryBinding: RowMainCategoryBinding) :
            RecyclerView.ViewHolder(rowMainCategoryBinding.root) {

            var categoryName: TextView

            init {
                categoryName = rowMainCategoryBinding.textViewRowMainCategoryCategoryName

                rowMainCategoryBinding.root.setOnClickListener {
                    val categoryIsPublic = mainCategoryViewModel.categoryList.value?.get(adapterPosition)?.categoryIsPublic!!
                    val categoryOwnerIdx = mainCategoryViewModel.categoryList.value?.get(adapterPosition)?.categoryOwnerIdx!!
                    val categoryIdx = mainCategoryViewModel.categoryList.value?.get(adapterPosition)?.categoryIdx!!

                    val bundle = Bundle()
                    bundle.putLong("categoryIdx", categoryIdx)

                    // 개인 카테고리일 경우
                    if (categoryIsPublic == 0L) {
                        mainActivity.replaceFragment(MainActivity.CATEGORY_OPTION_PERSONAL_FRAGMENT, true, bundle)
                    }
                    // 공용 카테고리일 경우
                    else {
                        // 내가 만든 카테고리일 경우
                        if (userIdx == categoryOwnerIdx) {
                            mainActivity.replaceFragment(MainActivity.CATEGORY_OPTION_PUBLIC_OWNER_FRAGMENT, true, bundle)
                        }
                        // 타인이 만든 카테고리일 경우
                        else {
                            mainActivity.replaceFragment(MainActivity.CATEGORY_OPTION_PUBLIC_FRAGMENT, true, bundle)
                        }
                    }
                }
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
            return mainCategoryViewModel.categoryList.value?.size!!
        }

        override fun onBindViewHolder(holder: MainCategoryViewHolder, position: Int) {
            holder.categoryName.text = mainCategoryViewModel.categoryList.value?.get(position)?.categoryName!!
            holder.categoryName.setTextColor(mainCategoryViewModel.categoryList.value?.get(position)?.categoryColor?.toInt()!!)
        }
    }
}