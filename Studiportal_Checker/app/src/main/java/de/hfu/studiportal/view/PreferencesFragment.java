package de.hfu.studiportal.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.network.RefreshTaskStarter;

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
		PreferenceManager.setDefaultValues(this.getActivity(), R.xml.preferences, false);
		PreferenceManager.getDefaultSharedPreferences(this.getActivity()).registerOnSharedPreferenceChangeListener(this);

		Preference logout = this.findPreference(getResources().getString(R.string.preference_logout));
		logout.setOnPreferenceClickListener (new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				// Use the Builder class for convenient dialog construction
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(R.string.text_logout_dialog)
				.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				})
				.setPositiveButton(R.string.preferences_logout_title, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//Stop update task
						RefreshTaskStarter.cancelRefreshTask(getActivity());
						
						//Delete login-info
						Editor sp = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
						sp.putString(getResources().getString(R.string.preference_last_studiportal_data), "");
						sp.putString(getResources().getString(R.string.preference_password), "");
						sp.apply();		

						//Restart refresh task, will cause loginActivity to show up
						RefreshTaskStarter.startRefreshTask(getActivity());
                        PreferencesFragment.this.getActivity().finish();

					}
				});

				// Create the AlertDialog object and return it
				builder.create().show();
				return true;

			}
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = super.onCreateView(inflater, container, savedInstanceState);
	    view.setBackgroundColor(getResources().getColor(R.color.color_activity_background));

	    return view;
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
		p = this.findPreference(getResources().getString(R.string.preference_logout));
		p.setSummary(username);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		updateSummaries();

		if(key.equals(getResources().getString(R.string.preference_refresh_rate))) {
			RefreshTaskStarter.startRefreshTask(this.getActivity());

		}

	}
}
