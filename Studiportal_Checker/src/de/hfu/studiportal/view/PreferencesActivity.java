package de.hfu.studiportal.view;

import android.os.Bundle;

/**
 * Empty Activity to hold the {@link PreferencesFragment}
 * @author preussjan
 * @since 1.0
 * @version 1.0
 */
public class PreferencesActivity extends DialogHostActivity {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		getFragmentManager().beginTransaction().replace(android.R.id.content, 
				new PreferencesFragment()).commit();


	}
}