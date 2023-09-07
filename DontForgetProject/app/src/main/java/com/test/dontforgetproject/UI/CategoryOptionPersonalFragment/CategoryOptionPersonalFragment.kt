package com.test.dontforgetproject.UI.CategoryOptionPersonalFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentCategoryOptionPersonalBinding
import com.test.dontforgetproject.databinding.RowMainCategoryBinding

class CategoryOptionPersonalFragment : Fragment() {
    lateinit var categoryOptionPersonalBinding: FragmentCategoryOptionPersonalBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryOptionPersonalBinding = FragmentCategoryOptionPersonalBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        categoryOptionPersonalBinding.run {
            toolbarCategoryOptionPersonal.run {
                title = "카테고리 관리"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
            }
        }

        return categoryOptionPersonalBinding.root
    }
}