package com.test.dontforgetproject.UI.MainAlertFragment


import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.dontforgetproject.DAO.AlertClass
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.MyApplication
import com.test.dontforgetproject.R
import com.test.dontforgetproject.Repository.AlertRepository
import com.test.dontforgetproject.Repository.TodoRepository
import com.test.dontforgetproject.databinding.DialogNormalBinding
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
                if(userAlertList.size == 0) {
                    fragmentMainAlertBinding.run {
                        textViewMainAlertZero.visibility = View.VISIBLE
                        buttonMainAlert.visibility = View.GONE
                    }
                } else {
                    fragmentMainAlertBinding.run {
                        textViewMainAlertZero.visibility = View.GONE
                        buttonMainAlert.visibility = View.VISIBLE
                    }
                }
            }
        }

        mainAlertViewModel.getAlert(MyApplication.loginedUserInfo.userIdx)

        fragmentMainAlertBinding.run {

            swipeMainAlert.setOnRefreshListener {
                swipeMainAlert.isRefreshing = false
                mainAlertViewModel.getAlert(MyApplication.loginedUserInfo.userIdx)
            }

            toolbarMainAlert.run {
                title = "알림"
            }

            recyclerViewMainAlert.run {
                adapter = RecyclerViewAdapter()

                layoutManager = LinearLayoutManager(mainActivity)
            }

            buttonMainAlert.setOnClickListener {

                var dialogNormalBinding = DialogNormalBinding.inflate(layoutInflater)
                val builder = MaterialAlertDialogBuilder(mainActivity)


                dialogNormalBinding.textViewDialogNormalTitle.text = "경고"
                dialogNormalBinding.textViewDialogNormalContent.text = "알림이 모두 삭제됩니다. \n삭제하시겠습니까?"

                builder.setView(dialogNormalBinding.root)

                builder.setNegativeButton("취소",null)
                builder.setPositiveButton("삭제"){ dialogInterface: DialogInterface, i: Int ->
                    for(position in 0 until userAlertList.size) {
//                        Log.d("lion","position : ${userAlertList.get(position).alertContent}")
                        AlertRepository.removeAlert(userAlertList.get(position).alertIdx) {
                            mainAlertViewModel.getAlert(MyApplication.loginedUserInfo.userIdx)
                            fragmentMainAlertBinding.recyclerViewMainAlert.adapter?.notifyDataSetChanged()
                        }
                    }
                    Toast.makeText(mainActivity, "모든 알림이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                builder.show()
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
                    AlertRepository.removeAlert(userAlertList.get(adapterPosition).alertIdx) {
                        mainAlertViewModel.getAlert(MyApplication.loginedUserInfo.userIdx)
                        fragmentMainAlertBinding.recyclerViewMainAlert.adapter?.notifyDataSetChanged()
                    }
                    Toast.makeText(mainActivity, "알림이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
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