package com.test.dontforgetproject.UI.MainFriendsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewmodel.CreationExtras
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentMainFriendsRequestBinding

class MainFriendsRequestFragment : Fragment() {
    lateinit var binding : FragmentMainFriendsRequestBinding
    lateinit var mainActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainFriendsRequestBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run{

        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

}