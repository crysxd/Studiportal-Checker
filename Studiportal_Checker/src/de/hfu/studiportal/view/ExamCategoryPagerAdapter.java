package de.hfu.studiportal.view;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.data.StudiportalData;

public class ExamCategoryPagerAdapter extends FragmentStatePagerAdapter {
	
	private StudiportalData data;

	public ExamCategoryPagerAdapter(FragmentManager fm, Context c) {
		super(fm);
		
		try {
			String key = c.getResources().getString(R.string.preference_last_studiportal_data);
			data = StudiportalData.loadFromSharedPreferences(PreferenceManager.getDefaultSharedPreferences(c), key);
			
		} catch(Exception e) {
			e.printStackTrace();
			data = new StudiportalData();
			
		}
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = new ExamCategoryFragment();
		Bundle args = new Bundle();
		// Our object is just an integer :-P
		args.putInt(ExamCategoryFragment.ARG_OBJECT, i + 1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		return data.getCategoryCount();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return data.getCategory(position).getCategoryName();
	}

}
