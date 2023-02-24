package com.example.skysys.utils

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.skysys.FragmentMissao
import com.example.skysys.R
import com.example.skysys.newInstace

class tabAdapter(ctx: Context, fm: FragmentManager): FragmentPagerAdapter(fm) {
    private val tabTitles: Array<String> = ctx.resources.getStringArray(R.array.sections)

        override fun getItem(position: Int): Fragment {
        val fm = FragmentMissao()

        return newInstace(tabTitles[0],tabTitles[1] ,tabTitles[2])
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return super.getPageTitle(position)
    }
}