package com.test.dontforgetproject.UI.JoinFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentJoinBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [JoinFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class JoinFragment : Fragment() {
    lateinit var fragmentJoinBinding: FragmentJoinBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentJoinBinding = FragmentJoinBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentJoinBinding.run {
            toolbarJoin.run {
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener{
                    mainActivity.removeFragment(MainActivity.JOIN_FRAGMENT)
                }
                setTitle("회원가입")
            }
            val email = textInputLayoutJoinEmail.editText?.toString()
            val password = textInputLayoutJoinPassword.editText?.toString()
            val passwordCheck = textInputLayoutJoinPasswordCheck.editText?.toString()
            val name = textInputLayoutJoinName.editText?.toString()
            val introduce = textInputLayoutJoinIntroduce.editText?.toString()

            buttonJoin.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.LOGIN_FRAGMENT,false,null)
            }
            buttonProductSellerRegisterAddImage.setOnClickListener {

            }
        }
        return fragmentJoinBinding.root
    }


}