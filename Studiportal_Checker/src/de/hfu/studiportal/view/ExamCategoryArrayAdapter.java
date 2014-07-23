package de.hfu.studiportal.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.data.Exam;
import de.hfu.studiportal.data.Seperator;

public class ExamCategoryArrayAdapter extends ArrayAdapter<Exam> {

	private final String BONUS;
	private final String MALUS;
	private final String ECTS;
	private final String ACCOUNT;
	private final String STATE_RESIGNATED;
	private final String STATE;
	private final String GENRATED;
	private final String SEMESTER;
	private final String ATTEMPT;
	private final String PRACTICAL_WORK;
	private final String NOTE;
	private final String GRADE;


	public ExamCategoryArrayAdapter(Context context, List<Exam> objects) {
		super(context, R.layout.list_item_exam, objects);

		this.BONUS = getStringResource(R.string.text_bonus);            
		this.MALUS = getStringResource(R.string.text_malus);         
		this.ECTS = getStringResource(R.string.text_ects);         
		this.ACCOUNT = getStringResource(R.string.text_account);         
		this.STATE_RESIGNATED = getStringResource(R.string.text_state_resignated);         
		this.STATE = getStringResource(R.string.text_state);         
		this.GENRATED = getStringResource(R.string.text_generated);         
		this.SEMESTER = getStringResource(R.string.text_semester);         
		this.ATTEMPT = getStringResource(R.string.text_attempt);         
		this.PRACTICAL_WORK = getStringResource(R.string.text_practical_work);         
		this.NOTE = getStringResource(R.string.text_note);
		this.GRADE = getStringResource(R.string.text_grade);

	}

	private String getStringResource(int id) {
		return this.getContext().getResources().getString(id);

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

		//Find Views
		List<TextView> textViews = new ArrayList<>();
		textViews.add((TextView) convertView.findViewById(R.id.textViewTitle));
		textViews.add((TextView) convertView.findViewById(R.id.textViewSubtitle1));
		textViews.add((TextView) convertView.findViewById(R.id.textViewSubtitle2));
		textViews.add((TextView) convertView.findViewById(R.id.textViewSubtitle3));
		textViews.add((TextView) convertView.findViewById(R.id.textViewSubtitle4));
		ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewState);

		//If e is a seperator hide all views, if not show them all
		if(e instanceof Seperator) {
			imageView.setVisibility(View.GONE);
			for(TextView v : textViews)
				v.setVisibility(View.GONE);

			return convertView;

		} else {
			imageView.setVisibility(View.VISIBLE);
			for(TextView v : textViews)
				v.setVisibility(View.VISIBLE);

		}

		//Get Kind
		Exam.Kind kind = e.getKindEnum();

		//Set title and exam no
		textViews.get(0).setText(e.getName());
		textViews.get(1).setText(e.getExamNo());

		int usedViews = 2;

		//Fill Views with data
		switch(kind) {
		case KO: 
			textViews.get(1).setText(String.format("%s (%s)", e.getExamNo(), this.ACCOUNT));
			textViews.get(3).setText(String.format("%s: %s %s", this.BONUS, e.getBonus(), this.ECTS));
			textViews.get(4).setText(String.format("%s: %s %s", this.MALUS, e.getMalus(), this.ECTS));
			
			//Hide the highlighted field....there is nothing to highlight
			textViews.get(2).setVisibility(View.GONE);
			usedViews = 5;
			break;

		case PL: 
		case P: 
		case G: 
			if(e.isResignated()) {
				textViews.get(2).setText(String.format("%s: %s", this.STATE, this.STATE_RESIGNATED));
				textViews.get(3).setText(String.format("%s: %s", this.NOTE, e.getNoteName(this.getContext())));
				usedViews = 5;

			} else {
				if(e.getStateEnum() == Exam.State.AN) {
					textViews.get(2).setText(String.format("%s: %s", this.STATE, e.getStateName(this.getContext())));
					textViews.get(3).setText(String.format("%s: %s", this.ECTS, e.getECTS()));
					usedViews = 5;	

				} else {
					textViews.get(2).setText(String.format("%s: %s", this.GRADE, e.getGrade()));
					textViews.get(3).setText(String.format("%s: %s", this.STATE, e.getStateName(this.getContext())));
					usedViews = 5;

				}
			}

			if(e.getKindEnum() == Exam.Kind.G) {
				textViews.get(1).setText(String.format("%s (%s)", e.getExamNo() , this.GENRATED));
				textViews.get(4).setText(String.format("%s: %s", this.SEMESTER, e.getSemester()));

			} else {
				textViews.get(4).setText(String.format("%s: %s (%s)", this.ATTEMPT, e.getTryCount(), e.getSemester()));

			}

			break;

		case VL: 
			textViews.get(1).setText(String.format("%s (%s)", e.getExamNo(), this.PRACTICAL_WORK));
			textViews.get(2).setText(String.format("%s: %s", this.STATE, e.getStateName(this.getContext())));
			textViews.get(3).setText(String.format("%s: %s", this.ECTS, e.getECTS()));
			textViews.get(4).setText(String.format("%s: %s (%s)", this.ATTEMPT, e.getTryCount(), e.getSemester()));
			usedViews = 5;


			break;

		case UNDEFINED:
			break;

		}

		//Set icon
		switch (e.getStateEnum()) {
		case AN: imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_an)); break;
		case BE: imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_be)); break;
		case NB: imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_nb)); break;
		case EN: imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_en)); break;
		case UNDEFINED: imageView.setVisibility(View.GONE); break;
		}

		//If eis resignated, override icon with flag
		if(e.isResignated()) {
			imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_re));

		} 

		//hide unused textviews
		for(int i=usedViews; i<textViews.size(); i++) {
			textViews.get(i).setVisibility(View.GONE);

		}

		return convertView;
	}
}
