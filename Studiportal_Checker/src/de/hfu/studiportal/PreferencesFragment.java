package de.hfu.studiportal;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import de.hfu.funfpunktnull.R;

/**
 * Fragment to display the settings.xml
 * @author preussjan
 * @since 1.0
 * @version 1.0
 */
public class PreferencesFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		updateSummaries();
		PreferenceManager.getDefaultSharedPreferences(this.getActivity()).registerOnSharedPreferenceChangeListener(this);
		
		Preference login = this.findPreference(getResources().getString(R.string.preference_login));
		login.setOnPreferenceClickListener (new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(getActivity(), LoginActivity.class);
				getActivity().startActivity(i);
				return true;
				
			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		  PreferenceManager.getDefaultSharedPreferences(this.getActivity()).unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		updateSummaries();

	}

	private void updateSummaries() {
		//Get Timestamp of last check
		String key = getResources().getString(R.string.preference_last_check);
		long lastCheck = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getLong(key, 0);

		//inti date string
		String dateString = "";

		//If it was refreshed 
		if(lastCheck > 0) {
			Date d = new Date(lastCheck);
			dateString = getResources().getString(R.string.text_last_updated);
			dateString += new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format(d);
			Log.i(this.getClass().getSimpleName(), dateString);

		}

		//Set the summary or an empty string if never refreshed
		Preference p = this.findPreference(getResources().getString(R.string.preference_refresh_rate));
		p.setSummary(dateString);

		//Display username
		key = getResources().getString(R.string.preference_user);
		String username = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString(key, "");
		p = this.findPreference(getResources().getString(R.string.preference_login));
		p.setSummary(username);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		updateSummaries();

	}
}
