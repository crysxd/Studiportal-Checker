package de.hfu.studiportal;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import de.hfu.funfpunktnull.R;

/**
 * Empty Activity to hold the {@link PreferencesFragment}
 * @author preussjan
 * @since 1.0
 * @version 1.0
 */
public class MainActivity extends PreferenceActivity implements DialogHost {

	private DialogHostImplementation dialogHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Create DialogHost
		this.dialogHost = new DialogHostImplementation(this);

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
		this.cancelProgressDialog();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == R.id.action_refresh) {
			new RefreshTask(this).execute();

			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	@Override
	public synchronized void showIndeterminateProgressDialog(final String title, final String text) {
		this.dialogHost.showIndeterminateProgressDialog(title, text);

	}

	@Override
	public void showDialog(final String title, final String text) {
		this.dialogHost.showDialog(title, text);
	}

	@Override
	public void showErrorDialog(final Exception e) {

		if(e instanceof NoChangeException) {
			//No change
			Toast.makeText(MainActivity.this, getResources().getString(R.string.text_no_change), Toast.LENGTH_SHORT).show();

		}

		if(e instanceof LoginException) {
			this.showDialog(
					getResources().getString(R.string.text_error), 
					getResources().getString(R.string.exception_wrong_user_password_long));

		}
	}

	@Override
	public void cancelProgressDialog() {
		this.dialogHost.cancelProgressDialog();

	}
}