package de.hfu.studiportal.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.data.ExamCategory;
import de.hfu.studiportal.data.StudiportalData;

public class ExamSearchActivity extends DialogHostActivity {

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

        //Build View
        setContentView(R.layout.activity_exam_search);

        //Set up Toolbar
        Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(bar);

		//Enable home as up
		this.getSupportActionBar().setHomeButtonEnabled(true);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		handleIntent(getIntent());

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
			
		}
		
		return super.onOptionsItemSelected(item);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.activity_exam_search, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		this.searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        this.searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
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
			getSupportFragmentManager().beginTransaction().replace(R.id.exam_category_fragment, fragment).commit();

		}
	}
}
