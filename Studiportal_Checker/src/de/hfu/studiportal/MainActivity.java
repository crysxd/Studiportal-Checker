package de.hfu.studiportal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

		hideProgressDialog(null);
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
	public void hideProgressDialog(Exception e) {
		//Hide dialog
		if(this.progressDialog != null)
			this.progressDialog.hide();
		
		//Cancel if no exception
		if(e == null)
			return;
		
		if(e instanceof NoChangeException) {
			//No change
			Toast.makeText(this, getResources().getString(R.string.text_no_change), Toast.LENGTH_SHORT).show();
			
		}
		
		if(e instanceof LoginException) {
			//Login is wrong
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setMessage(R.string.exception_wrong_user_password_long)
	               .setPositiveButton(R.string.text_close, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // FIRE ZE MISSILES!
	                   }
	               });
	
	        // Create the AlertDialog object and return it
	        builder.create().show();

		}

	}
}