package com.test.dontforgetproject.UI.CategoryOptionPublicFragment

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.test.dontforgetproject.DAO.CategoryClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.CategoryRepository
import com.test.dontforgetproject.databinding.DialogCategoryNormalBinding
import com.test.dontforgetproject.databinding.FragmentCategoryOptionPublicBinding
import com.test.dontforgetproject.databinding.RowCategoryOptionPublicBinding
import com.test.dontforgetproject.databinding.RowCategoryOptionPublicOwnerBinding

class CategoryOptionPublicFragment : Fragment() {
    lateinit var categoryOptionPublicBinding: FragmentCategoryOptionPublicBinding
    lateinit var mainActivity: MainActivity

    lateinit var categoryOptionPublicViewModel: CategoryOptionPublicViewModel

    val userInfo = MyApplication.loginedUserInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryOptionPublicBinding = FragmentCategoryOptionPublicBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        categoryOptionPublicViewModel = ViewModelProvider(mainActivity)[CategoryOptionPublicViewModel::class.java]
        categoryOptionPublicViewModel.run {
            categoryName.observe(mainActivity) {
                categoryOptionPublicBinding.editTextCategoryOptionPublicName.setText(it)
            }

            categoryColor.observe(mainActivity) {
                categoryOptionPublicBinding.run {
                    textInputCategoryOptionPublicName.run {
                        boxStrokeColor = it
                        hintTextColor = ColorStateList.valueOf(it)
                    }

                    editTextCategoryOptionPublicName.run {
                        setTextColor(it)
                    }

                    textViewCategoryOptionPublicColorPicker.backgroundTintList = ColorStateList.valueOf(it)
                }
            }

            categoryOwner.observe(mainActivity) {
                categoryOptionPublicBinding.textViewCategoryOptionPublicOwnerName.text = it
            }

            joinUserNameList.observe(mainActivity) {
                categoryOptionPublicBinding.recyclerViewCategoryOptionPublic.adapter?.notifyDataSetChanged()
            }
        }

        categoryOptionPublicBinding.run {
            val categoryIdx = arguments?.getLong("categoryIdx")!!
            val categoryOwnerIdx = arguments?.getLong("categoryOwnerIdx")!!
            val categoryOwnerName = arguments?.getString("categoryOwnerName")!!

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
                val dialogCategoryNormalBinding = DialogCategoryNormalBinding.inflate(layoutInflater)

                dialogCategoryNormalBinding.textViewDialogCategoryTitle.text = "경고"
                dialogCategoryNormalBinding.textViewDialogCategoryContent.text = "카테고리에서 나가면 카테고리의 할일도 같이 사라집니다."

                builder.setView(dialogCategoryNormalBinding.root)
                builder.setPositiveButton("나가기") { dialogInterface: DialogInterface, i: Int ->
                    val categoryName = editTextCategoryOptionPublicName.text.toString()
                    val categoryColor = editTextCategoryOptionPublicName.currentTextColor
                    var categoryFontColor = Color.BLACK
                    if (categoryColor == -12352444 || categoryColor == -16744538) {
                        categoryFontColor = Color.WHITE
                    }

                    val categoryJoinUserIdxList = ArrayList<Long>()
                    categoryJoinUserIdxList.add(categoryOwnerIdx)
                    categoryJoinUserIdxList.addAll(categoryOptionPublicViewModel.joinUserIdxList?.value!!)
                    // 자신의 userIdx가 담긴 인덱스를 찾아 해당 위치의 값을 삭제
                    val indexOfUserIdx = categoryJoinUserIdxList.indexOf(userInfo.userIdx)
                    categoryJoinUserIdxList.removeAt(indexOfUserIdx)
                    Log.i("indexOfUser", indexOfUserIdx.toString())

                    val categoryJoinUserNameList = ArrayList<String>()
                    categoryJoinUserNameList.add(categoryOwnerName)
                    categoryJoinUserNameList.addAll(categoryOptionPublicViewModel.joinUserNameList?.value!!)
                    // 자신의 userName을 삭제
                    categoryJoinUserNameList.removeAt(indexOfUserIdx)

                    val categoryClass = CategoryClass(
                        categoryIdx,
                        categoryName,
                        categoryColor.toLong(),
                        categoryFontColor.toLong(),
                        categoryJoinUserIdxList,
                        categoryJoinUserNameList,
                        1,
                        categoryOwnerIdx,
                        categoryOwnerName
                    )


                    CategoryRepository.modifyCategory(categoryClass) {
                        mainActivity.removeFragment(MainActivity.CATEGORY_OPTION_PUBLIC_FRAGMENT)
                    }
                }
                builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->

                }
                builder.show()
            }

            categoryOptionPublicViewModel.getCategoryInfo(categoryIdx)
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