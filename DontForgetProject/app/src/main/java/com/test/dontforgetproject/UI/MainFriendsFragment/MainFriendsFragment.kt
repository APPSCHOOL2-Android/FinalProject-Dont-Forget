package com.test.dontforgetproject.UI.MainFriendsFragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.test.dontforgetproject.MainActivity
import com.test.dontforgetproject.R
import com.test.dontforgetproject.databinding.DialogMainFriendsBinding
import com.test.dontforgetproject.databinding.FragmentMainFriendsBinding

class MainFriendsFragment : Fragment() {

    lateinit var binding : FragmentMainFriendsBinding
    lateinit var mainActivity: MainActivity

    // 탭레이아웃
    var tabName = arrayOf("친구목록", "친구 요청함")
    val fragmentList = mutableListOf<Fragment>()

    // 뷰페이저
    lateinit var mainFriendsListFragment: MainFriendsListFragment
    lateinit var mainFriendsRequestFragment : MainFriendsRequestFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainFriendsBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        mainFriendsListFragment = MainFriendsListFragment()
        mainFriendsRequestFragment = MainFriendsRequestFragment()

        binding.run{
            // 툴바
            toolbarMainFriends.run{
                title = "친구목록"
                inflateMenu(R.menu.menu_main_friends)

                setOnMenuItemClickListener {
                    when(it.itemId){
                        // 친구추가
                        R.id.item_menuMainFriend_add->{
                            var dialogMainFriendsBinding = DialogMainFriendsBinding.inflate(layoutInflater)
                            val builder = AlertDialog.Builder(mainActivity)

                            builder.setView(dialogMainFriendsBinding.root)
                            dialogMainFriendsBinding.editTextDialogMainFriends.requestFocus()

                            builder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
                                true
                                Toast.makeText(mainActivity, "친구 추가가 완료 되었습니다!", Toast.LENGTH_SHORT).show()
                            }

                            builder.setNegativeButton("취소",null)
                            builder.show()
                        }
                    }
                    true
                }
            }

            // 탭레이아웃
            fragmentList.add(mainFriendsListFragment)
            fragmentList.add(mainFriendsRequestFragment)

            viewPagerMainFriends.adapter = TabAdapterClass(mainActivity)
            val tabLayoutMediator =
                TabLayoutMediator(tabsMainFriends, viewPagerMainFriends) { tab: TabLayout.Tab, i: Int ->
                    tab.text = tabName[i]
                }
            tabLayoutMediator.attach()

        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.run{
            viewPagerMainFriends.requestLayout()
        }
    }

    // 탭레이아웃 어댑터
    inner class TabAdapterClass(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

    }

}