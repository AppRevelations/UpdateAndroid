package com.apprevelations.updateandroid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:

			return new Tab1();
		case 1:

			return new Tab2();
		case 2:

			return new Tab3();
		}

		return null;
	}

	@Override
	public int getCount() {

		return 3;
	}

}
