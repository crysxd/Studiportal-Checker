package de.hfu.studiportal.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.data.ExamCategory;

//Instances of this class are fragments representing a single
//object in our collection.
public class ExamCategoryFragment extends ListFragment implements OnClickListener {
	public static final String ARG_CATEGORY = "category";
	public static final String ARG_HIDE_SEARCH_BUTTON = "hide_search";

	private FloatingActionButton mSearchButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// The last two arguments ensure LayoutParams are inflated properly.
		View rootView = inflater.inflate(
				R.layout.fragment_exam_category, container, false);

		//Save Category
		ExamCategory c = (ExamCategory) getArguments().get(ARG_CATEGORY);
		
		//Set Adapter
		this.setListAdapter(new ExamCategoryArrayAdapter(this.getActivity(), c.getAllExams()));
		
		//Attach Floating Button to ListView
		ListView list = (ListView) rootView.findViewById(android.R.id.list);
		mSearchButton = (FloatingActionButton) rootView.findViewById(R.id.button_floating_action);
		mSearchButton.attachToListView(list);
		mSearchButton.setOnClickListener(this);
		
		if(getArguments().getBoolean(ARG_HIDE_SEARCH_BUTTON)) {
			mSearchButton.setVisibility(View.GONE);

		}
		
		return rootView;
	}

	@Override
	public void onClick(View v) {
		Intent i = new Intent(this.getActivity(), ExamSearchActivity.class);
		this.startActivity(i);
				
	}
}