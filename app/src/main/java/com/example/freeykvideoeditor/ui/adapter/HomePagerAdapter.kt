package com.example.freeykvideoeditor.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.freeykvideoeditor.ui.homefragment.AnnivarceryFragment
import com.example.freeykvideoeditor.ui.homefragment.BirthDayFragment
import com.example.freeykvideoeditor.ui.homefragment.FriendsFragment
import com.example.freeykvideoeditor.ui.homefragment.MyRecommendationFragment
import com.example.freeykvideoeditor.ui.homefragment.NewArrivalFragment
import com.example.freeykvideoeditor.ui.homefragment.RomanticFragment

class HomePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 6
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> MyRecommendationFragment()
            1 -> NewArrivalFragment()
            2 -> RomanticFragment()
            3 -> FriendsFragment()
            4 -> BirthDayFragment()
            5 -> AnnivarceryFragment()
            else -> {
                throw IllegalAccessError()
            }
        }
    }
}