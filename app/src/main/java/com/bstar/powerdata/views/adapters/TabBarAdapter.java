package com.bstar.powerdata.views.adapters;

import android.support.annotation.ArrayRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bstar.powerdata.PowerDataApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TabBarAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mFragmentTitleList = new ArrayList<>();

    public TabBarAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    public void addTitle(@ArrayRes int arrayRes) {
        addTitle(PowerDataApplication.getStringArrayResource(arrayRes));
    }
    public void addTitle(String[] stringArray) {
        this.mFragmentTitleList = new ArrayList<String>(Arrays.asList(stringArray));
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
