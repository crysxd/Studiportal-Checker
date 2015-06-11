package de.hfu.studiportal.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.data.ExamCategory;
import de.hfu.studiportal.data.StudiportalData;

public class ExamCategoryArrayAdapter extends ArrayAdapter<String> implements OnSharedPreferenceChangeListener {
	
	private StudiportalData data;
	private String studiportalDataKey;
	private Refreshable refresh;

	public ExamCategoryArrayAdapter(Context c, Refreshable refresh) {
        super(c, R.layout.navigation_item_row, R.id.rowText);

		this.refresh = refresh;
		this.studiportalDataKey = c.getResources().getString(R.string.preference_last_studiportal_data);
		PreferenceManager.getDefaultSharedPreferences(c).registerOnSharedPreferenceChangeListener(this);
		this.loadDataAndRefresh(PreferenceManager.getDefaultSharedPreferences(c));

	}

	@Override
	public int getCount() {
		return data.getCategoryCount();
	}

	@Override
	public String getItem(int position) {
		return data.getCategory(position).getCategoryName();
	}

    public ExamCategory getCategory(int position) {
        return data.getCategory(position);
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
		
		this.refresh.onRefresh();
		this.loadDataAndRefresh(sharedPreferences);
	}

}
