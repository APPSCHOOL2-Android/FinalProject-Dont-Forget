package com.test.dontforgetproject.UI.CategoryAddPersonalFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentCategoryAddPersonalBinding


class CategoryAddPersonalFragment : Fragment() {
    lateinit var categoryAddPersonalBinding: FragmentCategoryAddPersonalBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryAddPersonalBinding = FragmentCategoryAddPersonalBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        categoryAddPersonalBinding.run {
            toolbarCategoryAddPersonal.run {
                title = "카테고리 추가"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
            }
        }

        return categoryAddPersonalBinding.root
    }
}