package de.hfu.studiportal.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.data.ExamCategory;

//Instances of this class are fragments representing a single
//object in our collection.
public class ExamCategoryFragment extends Fragment {
	public static final String ARG_CATEGORY = "category";

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// The last two arguments ensure LayoutParams are inflated properly.
		View rootView = inflater.inflate(
				R.layout.fragment_exam_category, container, false);

		//Save Category
		ExamCategory c = (ExamCategory) getArguments().get(ARG_CATEGORY);

        //Find list and init
        RecyclerView list = (RecyclerView) rootView.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        list.setItemAnimator(new DefaultItemAnimator());

		//Set Adapter
		list.setAdapter(new ExamCategoryAdapter(this.getActivity(), c.getAllExams()));
		
		return rootView;
	}
}