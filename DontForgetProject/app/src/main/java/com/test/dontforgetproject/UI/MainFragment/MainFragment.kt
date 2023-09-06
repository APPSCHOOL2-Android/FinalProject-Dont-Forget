package com.test.dontforgetproject.UI.MainFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.dontforgetproject.databinding.FragmentMainBinding


class MainFragment : Fragment() {
    lateinit var fragmentMainBinding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMainBinding = FragmentMainBinding.inflate(inflater)
        return fragmentMainBinding.root
    }


}