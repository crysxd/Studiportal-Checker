package de.hfu.studiportal.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.data.ExamCategory;
import de.hfu.studiportal.network.NoChangeException;
import de.hfu.studiportal.network.RefreshTask;
import de.hfu.studiportal.network.RefreshTaskStarter;

public class MainActivity extends DialogHostActivity implements Refreshable, AdapterView.OnItemClickListener {

	private ExamCategoryArrayAdapter examCategoryAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
	private boolean isDestroyed = false;
    private ListView examCategoryList;
    private Integer selectedCategory = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Start Background Service
		RefreshTaskStarter.startRefreshTask(this);

		//Build View
		this.setContentView(R.layout.activity_main);

        //Set up Toolbar
        Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(bar);
        this.getSupportActionBar().setTitle(this.getString(R.string.app_name));

        //Display user name
        String user = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getString(R.string.preference_user), "");
        TextView userView = (TextView) this.findViewById(R.id.textViewUser);
        userView.setText(user);

        //Set Up Navigation Drawer
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Set up drawer toggle which will be put in the left side of the ActionBar
        this.drawerToggle = new ActionBarDrawerToggle(
                this,              /* host Activity */
                drawerLayout,      /* DrawerLayout object */
                R.string.app_name, /* "open drawer" description */
                R.string.app_name  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View view) {
                super.onDrawerClosed(view);
            }
        };

        // Set the drawer toggle as the DrawerListener
        this.drawerLayout.setDrawerListener(this.drawerToggle);

        //Setup the ActionBar for the DrawerToggle
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        //Create ExamCategoryAdapter
        this.examCategoryAdapter = new ExamCategoryArrayAdapter(this, this);

        //Find exam category list, set adapter
        this.examCategoryList = (ListView) this.findViewById(R.id.examCategoryList);
        this.examCategoryList.setAdapter(this.examCategoryAdapter);

        //Add item click listener
        this.examCategoryList.setOnItemClickListener(this);

        //Set Up View
		this.onRefresh();
		
	}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        this.drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.drawerToggle.onConfigurationChanged(newConfig);
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

		if(item.getItemId() == R.id.action_preferences) {
			Intent i = new Intent(this, PreferencesActivity.class);
			this.startActivity(i);

			return true;
		}

		if(item.getItemId() == R.id.action_open_online) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse("https://studi-portal.hs-furtwangen.de/"));
			this.startActivity(i);

			return true;
		}

        if(item.getItemId() == R.id.action_search) {
            Intent i = new Intent(this, ExamSearchActivity.class);
            this.startActivity(i);

            return true;
        }

		return super.onOptionsItemSelected(item);

	}

	@Override
	public void showErrorDialog(final Exception e) {

		if(e instanceof NoChangeException) {
			//No change
            Snackbar.make(this.drawerLayout, getResources().getString(R.string.text_no_change), Snackbar.LENGTH_LONG).show();

		}else {
			super.showErrorDialog(e);

		}
	}
	
	@Override
	protected void onDestroy() {
		synchronized(this) {
			this.isDestroyed = true;
		}
		
		this.dismiss();
		
		super.onDestroy();
		
	}

	@Override
	public synchronized void onRefresh() {
		if(this.isDestroyed)
			return;

        //Update fragment
        this.showCategory(this.selectedCategory);

	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Save the selected Category
        this.selectedCategory = position;

        //Update fragment
        this.showCategory(this.selectedCategory);

    }

    private void showCategory(int categoryIndex) {
        //Create Fragment
        Fragment fragment = new ExamCategoryFragment();
        Bundle args = new Bundle();

        //Fetch Category
        ExamCategory category = examCategoryAdapter.getCategory(categoryIndex);

        // Our object is just an integer :-P
        args.putSerializable(ExamCategoryFragment.ARG_CATEGORY, category);
        fragment.setArguments(args);

        //Set Fragment
        this.getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();

        //Set title
        this.getSupportActionBar().setTitle(category.getCategoryName());

        //Hide drawer
        this.drawerLayout.closeDrawers();

        showErrorDialog(new NoChangeException());

    }
}
