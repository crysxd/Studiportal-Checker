package de.hfu.studiportal;

import de.hfu.funfpunktnull.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Fragment to display the settings.xml
 * @author preussjan
 * @since 1.0
 * @version 1.0
 */
public class PreferencesFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

	}
}
