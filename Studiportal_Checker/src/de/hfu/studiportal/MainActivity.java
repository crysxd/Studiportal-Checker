package de.hfu.studiportal;

import de.hfu.funfpunktnull.R;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Empty Activity to hold the {@link PreferencesFragment}
 * @author preussjan
 * @since 1.0
 * @version 1.0
 */
public class MainActivity extends PreferenceActivity {

	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		getFragmentManager().beginTransaction().replace(android.R.id.content, 
				new PreferencesFragment()).commit();
		PreferenceManager.setDefaultValues(MainActivity.this, R.xml.preferences, false);

		//Start Background Service
		RefreshServiceStarter.startRefreshTask(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.activity_main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();

		hideProgressDialog();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == R.id.action_refresh) {
			new RefreshTask(this).execute();
			this.progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.text_start_refresh));
			this.progressDialog.show();

			return true;
		}

		return super.onOptionsItemSelected(item);

	}
	public void hideProgressDialog() {
		if(this.progressDialog != null)
			this.progressDialog.hide();

		//		Date last = new Date(PreferenceManager.getDefaultSharedPreferences(this).getLong(getResources().getString(R.string.preference_last_check), 0));
		//		String summary = getResources().getString(R.string.text_last_updated) + new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss 'Uhr'").format(last);
		//		getPreferenceScreen().findPreference(getResources().getString(R.string.preference_refresh_settings)).setSummary(summary);

	}
}