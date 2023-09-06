package com.test.dontforgetproject.UI.MainAlertFragment

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.FragmentMainAlertBinding
import com.test.dontforgetproject.databinding.RowMainAlertBinding

class MainAlertFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var fragmentMainAlertBinding: FragmentMainAlertBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentMainAlertBinding = FragmentMainAlertBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentMainAlertBinding.run {
            toolbarMainAlert.run {
                title = "알림"
            }
            recyclerViewMainAlert.run {
                adapter = RecyclerViewAdapter()

                layoutManager = LinearLayoutManager(mainActivity)
            }
        }

        return fragmentMainAlertBinding.root
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolderClass>(){
        inner class ViewHolderClass(rowBinding: RowMainAlertBinding) : RecyclerView.ViewHolder(rowBinding.root){

            val rowAlert : TextView
            val rowAlertLogo : ImageView

            init{
                rowAlert = rowBinding.textViewRowMainAlertAlert
                rowAlertLogo = rowBinding.imageView
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            val rowAlertListBinding = RowMainAlertBinding.inflate(layoutInflater)
            val viewHolder = ViewHolderClass(rowAlertListBinding)

            rowAlertListBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return viewHolder
        }

        override fun getItemCount(): Int {
            return 100
        }

        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            holder.rowAlertLogo.setImageResource(R.drawable.img_logo)
            holder.rowAlert.text = "알림 내용입니다."
        }
    }
}