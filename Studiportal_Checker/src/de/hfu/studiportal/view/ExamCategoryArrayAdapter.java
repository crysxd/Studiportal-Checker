package de.hfu.studiportal.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.data.Exam;

public class ExamCategoryArrayAdapter extends ArrayAdapter<Exam> {

	public ExamCategoryArrayAdapter(Context context, List<Exam> objects) {
		super(context, R.layout.list_item_exam, objects);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Create View
		if (convertView == null) {
			LayoutInflater vi = LayoutInflater.from(getContext());
			convertView = vi.inflate(R.layout.list_item_exam, parent, false);

		}
		
		//Get Exam
		Exam e = this.getItem(position);
		
		//Set information
		((TextView) convertView.findViewById(R.id.textViewTitle)).setText(e.getName());
		((TextView) convertView.findViewById(R.id.textViewSubtitle1)).setText(e.getExamNo());
		((TextView) convertView.findViewById(R.id.textViewSubtitle2)).setText("Note: " + e.getGrade());

		return convertView;
	}

}
