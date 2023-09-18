package com.test.dontforgetproject.UI.MainAlertFragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.dontforgetproject.DAO.AlertClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.AlertRepository
import com.test.dontforgetproject.databinding.FragmentMainAlertBinding
import com.test.dontforgetproject.databinding.RowMainAlertBinding

class MainAlertFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var fragmentMainAlertBinding: FragmentMainAlertBinding

    lateinit var mainAlertViewModel: MainAlertViewModel

    var userAlertList = mutableListOf<AlertClass>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentMainAlertBinding = FragmentMainAlertBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        mainAlertViewModel = ViewModelProvider(mainActivity)[MainAlertViewModel::class.java]
        mainAlertViewModel.run {

            alertList.observe(mainActivity) {
                userAlertList = it
                fragmentMainAlertBinding.recyclerViewMainAlert.adapter?.notifyDataSetChanged()
            }
        }
        mainAlertViewModel.getAlert(MyApplication.loginedUserInfo.userIdx)

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

    override fun onResume() {
        super.onResume()
        mainAlertViewModel.getAlert(MyApplication.loginedUserInfo.userIdx)
        fragmentMainAlertBinding.recyclerViewMainAlert.adapter?.notifyDataSetChanged()
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolderClass>(){
        inner class ViewHolderClass(rowBinding: RowMainAlertBinding) : RecyclerView.ViewHolder(rowBinding.root){

            val rowAlert : TextView
            val rowAlertLogo : ImageView

            init{
                rowAlert = rowBinding.textViewRowMainAlertAlert
                rowAlertLogo = rowBinding.imageView

                rowBinding.root.setOnClickListener {
                    // 친구 알림
                    if(userAlertList.get(position).alertType.toInt() == 0) {
                        AlertRepository.removeAlert(userAlertList.get(adapterPosition).alertIdx) {

                        }
                        Toast.makeText(mainActivity, "알림이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        mainAlertViewModel.getAlert(MyApplication.loginedUserInfo.userIdx)
                        fragmentMainAlertBinding.recyclerViewMainAlert.adapter?.notifyDataSetChanged()
                    }
                    // 카테고리 알림
                    else if(userAlertList.get(position).alertType.toInt() == 1) {
                        AlertRepository.removeAlert(userAlertList.get(adapterPosition).alertIdx) {

                        }
                        Toast.makeText(mainActivity, "알림이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        mainAlertViewModel.getAlert(MyApplication.loginedUserInfo.userIdx)
                        fragmentMainAlertBinding.recyclerViewMainAlert.adapter?.notifyDataSetChanged()
                    }

                    // 할일 알림
                    else if(userAlertList.get(position).alertType.toInt() == 2) {
                        AlertRepository.removeAlert(userAlertList.get(adapterPosition).alertIdx) {

                        }
                        Toast.makeText(mainActivity, "알림이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        mainAlertViewModel.getAlert(MyApplication.loginedUserInfo.userIdx)
                        fragmentMainAlertBinding.recyclerViewMainAlert.adapter?.notifyDataSetChanged()
                    }
                }
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
            return userAlertList.size
        }

        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            holder.rowAlertLogo.setImageResource(R.drawable.img_logo)
            holder.rowAlert.text = userAlertList.get(position).alertContent
        }
    }
}