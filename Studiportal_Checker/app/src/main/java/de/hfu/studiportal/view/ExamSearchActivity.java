package de.hfu.studiportal.view;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.concurrent.Executors;

import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.data.ExamCategory;
import de.hfu.studiportal.data.StudiportalData;

public class ExamSearchActivity extends DialogHostActivity implements View.OnClickListener, TextWatcher {

	private StudiportalData studiportalData;
	private EditText searchView;
    private ImageButton imageViewBack;
    private ImageButton imageViewClear;
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

        //Find searchView and focus it after a short delay
        this.searchView = (EditText) this.findViewById(R.id.searchView);

        //Focus the search field after a delay of 500ms
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ExamSearchActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ExamSearchActivity.this.searchView.setFocusable(true);
                        ExamSearchActivity.this.searchView.setFocusableInTouchMode(true);
                        ExamSearchActivity.this.searchView.requestFocus();

                    }
                });
            }
        });

        //Add text listsner to searchField
        this.searchView.addTextChangedListener(this);

        //find back and clear button and apply listener
        this.imageViewBack = (ImageButton)  this.findViewById(R.id.imageViewBack);
        this.imageViewClear = (ImageButton) this.findViewById(R.id.imageViewClear);
        this.imageViewBack.setOnClickListener(this);
        this.imageViewClear.setOnClickListener(this);

	}

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Hide Keybord manually to sync the hide animation with the exit transition
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.searchView.getWindowToken(), 0);

        //Prepare items
        this.searchView.setText("");
        this.searchView.setFocusable(false);
        this.imageViewBack.setImageResource(R.drawable.abc_ic_search_api_mtrl_alpha);
        this.imageViewBack.setColorFilter(getResources().getColor(R.color.color_light_text),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        this.imageViewClear.setImageResource(R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha);

    }

	private void performSearch(String query) {
        ExamCategory result = studiportalData.searchExams(query );

        //Create fragment
        ExamCategoryFragment fragment = new ExamCategoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ExamCategoryFragment.ARG_CATEGORY, result);
        fragment.setArguments(args);

        //Put the fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.exam_category_fragment, fragment).commit();

	}

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imageViewBack || this.searchView.getText().length() == 0) {
            this.onBackPressed();

        } else if(v.getId() == R.id.imageViewClear) {
            this.searchView.setText("");
            this.performSearch("");

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.performSearch(this.searchView.getText().toString());

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
