package de.hfu.studiportal.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.hfu.funfpunktnull.R;

//Instances of this class are fragments representing a single
//object in our collection.
public class ExamCategoryFragment extends Fragment {
 public static final String ARG_OBJECT = "object";

 @Override
 public View onCreateView(LayoutInflater inflater,
         ViewGroup container, Bundle savedInstanceState) {
     // The last two arguments ensure LayoutParams are inflated
     // properly.
     View rootView = inflater.inflate(
             R.layout.fragment_exam_category, container, false);
     Bundle args = getArguments();
     ((TextView) rootView.findViewById(R.id.text1)).setText(
             Integer.toString(args.getInt(ARG_OBJECT)));
     return rootView;
 }
}