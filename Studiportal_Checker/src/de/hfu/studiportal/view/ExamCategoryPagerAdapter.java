package de.hfu.studiportal.view;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.data.StudiportalData;

public class ExamCategoryPagerAdapter extends FragmentStatePagerAdapter implements OnSharedPreferenceChangeListener {
	
	private StudiportalData data;
	private String studiportalDataKey;

	public ExamCategoryPagerAdapter(FragmentManager fm, Context c) {
		super(fm);
		
		this.studiportalDataKey = c.getResources().getString(R.string.preference_last_studiportal_data);
		PreferenceManager.getDefaultSharedPreferences(c).registerOnSharedPreferenceChangeListener(this);
		this.loadDataAndRefresh(PreferenceManager.getDefaultSharedPreferences(c));

	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = new ExamCategoryFragment();
		Bundle args = new Bundle();
		// Our object is just an integer :-P
		args.putSerializable(ExamCategoryFragment.ARG_CATEGORY, data.getCategory(i));
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		return data.getCategoryCount();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return data.getCategory(position).getCategoryName().toUpperCase(Locale.getDefault());
	}
	
	private void loadDataAndRefresh(SharedPreferences prefs) {
		try {
			data = StudiportalData.loadFromSharedPreferences(prefs, studiportalDataKey);
			this.notifyDataSetChanged();
			
		} catch(Exception e) {
			e.printStackTrace();
			
			if(data == null)
				data = new StudiportalData();
			
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		if(!key.equals(studiportalDataKey))
			return;
		
		this.loadDataAndRefresh(sharedPreferences);
	}

}
