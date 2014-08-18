package de.hfu.studiportal.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.Toast;
import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.data.ExamCategory;
import de.hfu.studiportal.data.StudiportalData;

public class ExamSearchActivity extends FragmentActivity {

	private StudiportalData studiportalData;
	private SearchView searchView;
	private String query;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			studiportalData = StudiportalData.loadFromSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this),
					getResources().getString(R.string.preference_last_studiportal_data));
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, getResources().getString(R.string.text_error), Toast.LENGTH_LONG).show();

		}

		handleIntent(getIntent());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_exam_search, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		this.searchView =
				(SearchView) menu.findItem(R.id.search).getActionView();
		this.searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));
		this.searchView.setIconified(false);

		if(this.query != null)
			this.searchView.setQuery(this.query , false);

		//Autosubmit
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				return false;

			}

			@Override
			public boolean onQueryTextChange(String s) {
				Intent i = new Intent();
				i.putExtra(SearchManager.QUERY, s);
				i.setAction(Intent.ACTION_SEARCH);
				ExamSearchActivity.this.handleIntent(i);
				
				Log.i(getClass().getSimpleName(), "onQueryTextChange()");
				return false;

			}
		});

		return super.onCreateOptionsMenu(menu);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);

	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {	
			//Search
			this.query = intent.getStringExtra(SearchManager.QUERY);
			ExamCategory result = studiportalData.searchExams(this.query );
			Log.i(getClass().getSimpleName(), "handleIntent()");

			//Create fragment
			ExamCategoryFragment fragment = new ExamCategoryFragment();
			Bundle args = new Bundle();
			args.putSerializable(ExamCategoryFragment.ARG_CATEGORY, result);
//			args.putBoolean(ExamCategoryFragment.ARG_HIDE_SEARCH_BUTTON, true);
			fragment.setArguments(args);

			//Put the fragment
			getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();

		}
	}
}
