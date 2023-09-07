package com.test.dontforgetproject.UI.TodoAddSearchFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentTodoAddSearchBinding

lateinit var mainActivity: MainActivity
lateinit var todoAddSearchBinding: FragmentTodoAddSearchBinding

class TodoAddSearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        todoAddSearchBinding = FragmentTodoAddSearchBinding.inflate(layoutInflater)

        todoAddSearchBinding.run {

        }



        return todoAddSearchBinding.root
    }

}