package de.hfu.studiportal.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.data.ExamCategory;
import de.hfu.studiportal.network.NoChangeException;
import de.hfu.studiportal.network.RefreshTask;
import de.hfu.studiportal.network.RefreshTaskStarter;

public class MainActivity extends DialogHostActivity implements Refreshable, AdapterView.OnItemClickListener, View.OnClickListener {

	private ExamCategoryArrayAdapter examCategoryAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
	private boolean isDestroyed = false;
    private ListView examCategoryList;
    private Integer selectedCategory = 0;
    private boolean buttonSearchRaised = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        //If the user is empty, forward to LoginActivity
        if(!this.isLoggedIn()) {
            Intent i = new Intent(this, LoginActivity.class);
            this.startActivity(i);
            this.finish();
            return;
        }

		//Build View
		this.setContentView(R.layout.activity_main);

        //Load user name and password
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String user = prefs.getString(this.getString(R.string.preference_user), "");

        //Set username
        TextView userView = (TextView) this.findViewById(R.id.textViewUser);
        userView.setText(user);

        //Set up Toolbar
        Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(bar);
        this.getSupportActionBar().setTitle(this.getString(R.string.app_name));

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

        //Find exam category list
        this.examCategoryList = (ListView) this.findViewById(R.id.examCategoryList);

        //Add item click listener
        this.examCategoryList.setOnItemClickListener(this);

        //Add click listener to search button (fab)
        this.findViewById(R.id.buttonSearch).setOnClickListener(this);

        //Set Up View
		this.onRefresh();

        //Start Background Service
        RefreshTaskStarter.startRefreshTask(this);
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

        if(buttonSearchRaised) {
            return;
        }

        buttonSearchRaised = true;

        View adBanner = findViewById(R.id.adBanner);
        adBanner.setOnClickListener(this);
        View buttonSearch = findViewById(R.id.buttonSearch);

        adBanner.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) buttonSearch.getLayoutParams();
        lp.bottomMargin += adBanner.getMeasuredHeight();
        buttonSearch.setLayoutParams(lp);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

        if (this.drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

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

		return super.onOptionsItemSelected(item);

	}

	@Override
	public void showErrorDialog(final Exception e) {

		if(e instanceof NoChangeException) {
			//No change
            Snackbar.make(this.findViewById(R.id.coordinatorLayout), getResources().getString(R.string.text_no_change), Snackbar.LENGTH_LONG).show();

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

        //Create ExamCategoryAdapter
        this.examCategoryAdapter = new ExamCategoryArrayAdapter(this, this);

        //Set adapter
        this.examCategoryList.setAdapter(this.examCategoryAdapter);
        
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


    private boolean isLoggedIn() {
        //Load user and password
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String user = prefs.getString(this.getString(R.string.preference_user), "");
        String password = prefs.getString(this.getString(R.string.preference_password), "");

        //If the user is empty, forward to LoginActivity
        return user.length() != 0 &&  password.length() != 0;
    }

    private void showCategory(int categoryIndex) {
        //Check if at least one category is available or the activity was destroyed, cancel if not
        if(this.examCategoryAdapter.getCount() == 0 || this.isDestroyed || !this.isLoggedIn()) {
            return;
        }

        //Create Fragment
        Fragment fragment = new ExamCategoryFragment();
        Bundle args = new Bundle();

        //Fetch Category
        ExamCategory category = this.examCategoryAdapter.getCategory(categoryIndex);

        // Our object is just an integer :-P
        args.putSerializable(ExamCategoryFragment.ARG_CATEGORY, category);
        fragment.setArguments(args);

        //Set Fragment
        this.getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commitAllowingStateLoss();

        //Set title
        this.getSupportActionBar().setTitle(category.getCategoryName());

        //Hide drawer
        this.drawerLayout.closeDrawers();

    }

    @Override
    public void onClick(View v) {
        if(v == this.findViewById(R.id.buttonSearch)) {
            Intent i = new Intent(this, ExamSearchActivity.class);
            this.startActivity(i);

        } else if (v.getId() == R.id.adBanner) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.savedroid.de/?utm_source=mobil&utm_medium=application&utm_campaign=studiportal"));
            this.startActivity(i);

        }
    }
}
