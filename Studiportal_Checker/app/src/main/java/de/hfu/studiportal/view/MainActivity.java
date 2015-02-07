package de.hfu.studiportal.view;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.network.NoChangeException;
import de.hfu.studiportal.network.RefreshTask;
import de.hfu.studiportal.network.RefreshTaskStarter;

public class MainActivity extends DialogHostActivity implements Refreshable, PopupMenu.OnMenuItemClickListener, View.OnClickListener {

    private ExamCategoryPagerAdapter pagerAdapter;
	private ViewPager viewPager;
    private TextView searchHint;
    private CardView searchField;
    private ImageButton overflowIcon;
	private boolean isDestroyed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Start Background Service
		RefreshTaskStarter.startRefreshTask(this);

		//Build View
		setContentView(R.layout.activity_main);

        //Set up Toolbar
        Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(bar);
        this.getSupportActionBar().setTitle(this.getString(R.string.app_name));

        //Setup search action
        this.searchHint = (TextView) findViewById(R.id.textViewTitle);
        this.searchField = (CardView) findViewById(R.id.cardView);
        this.searchField.setOnClickListener(this);

        //Setup Popupmenug
        this.overflowIcon = (ImageButton) this.findViewById(R.id.imageViewOverflow);
        this.overflowIcon.setOnClickListener(this);

		//Set Up ViewPager
		viewPager = (ViewPager) findViewById(R.id.pager);
		this.onRefresh();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.cancelProgressDialog();

        //If the search hint is invisible, fade it back in
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(this.searchField, new Fade());
            this.searchHint.setVisibility(View.VISIBLE);

        }
	}

	@Override
    public boolean onMenuItemClick(MenuItem item) {
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
			Toast.makeText(MainActivity.this, getResources().getString(R.string.text_no_change), Toast.LENGTH_SHORT).show();

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
		
		int selectedPage = 0;
		
		if(this.viewPager != null)
			selectedPage = this.viewPager.getCurrentItem();
			
		pagerAdapter = new ExamCategoryPagerAdapter(getSupportFragmentManager(), this, this);
		this.viewPager.setAdapter(this.pagerAdapter);
		
		viewPager.setCurrentItem(selectedPage);
		
	}

    @Override
    public void onClick(View view) {
        //If the Search field is clicked
        if(view == this.searchField) {
            //Create a intent for the search activity
            Intent i = new Intent(this, ExamSearchActivity.class);

            //If we are on 5.0 or higher, prepare a shared elements transition
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //Create transition
                ActivityOptions transition = ActivityOptions.makeSceneTransitionAnimation(
                        MainActivity.this,
                        new Pair<View, String>(MainActivity.this.searchField,
                                MainActivity.this.getString(R.string.transition_name_search_field))
                );

                //Start activity with transition
                this.startActivity(i, transition.toBundle());

                //Hide the searchHint, otherwise this will glitch the animation back
                //Is shown again in onResume()
                this.searchHint.setVisibility(View.INVISIBLE);

            } else {
                //We are not on Lolipop, shared element transitions are not available -> start normally
                this.startActivity(i);

            }
        }

        //If the overflow icon is pressed we must show the menu
        if(view == this.overflowIcon) {
            PopupMenu menu = null;

            //Create the Popupmenu, either with a gravity (on 4.4+) or widthout
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                menu = new PopupMenu(MainActivity.this, findViewById(R.id.popupAnchor), Gravity.END);

            } else {
                menu = new PopupMenu(MainActivity.this, findViewById(R.id.popupAnchor));

            }

            //Inflate menu, set listener and show
            menu.getMenuInflater().inflate(R.menu.activity_main, menu.getMenu());
            menu.setOnMenuItemClickListener(MainActivity.this);
            menu.show();

        }
    }
}
