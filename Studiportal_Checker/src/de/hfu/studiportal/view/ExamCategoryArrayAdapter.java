package de.hfu.studiportal.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.data.Exam;
import de.hfu.studiportal.data.Seperator;

public class ExamCategoryArrayAdapter extends ArrayAdapter<Exam> {

	private final String BONUS;
	private final String MALUS;
	private final String ECTS;
	private final String NO_ECTS;
	private final String STATE_RESIGNATED;
	private final String STATE;
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
		this.NO_ECTS = getStringResource(R.string.text_no_ects);
		this.STATE_RESIGNATED = getStringResource(R.string.text_state_resignated);         
		this.STATE = getStringResource(R.string.text_state);         
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
		List<TextView> textViews = new ArrayList<TextView>();
		textViews.add((TextView) convertView.findViewById(R.id.textViewTitle));
		textViews.add((TextView) convertView.findViewById(R.id.textViewSubtitle1));
		textViews.add((TextView) convertView.findViewById(R.id.textViewSubtitle2));
		ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewState);

		//If e is a seperator hide all views, if not show them all
		if(e instanceof Seperator) {
			convertView.setVisibility(View.GONE);
			convertView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 60));

			return convertView;

		} else {
			convertView.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			imageView.setVisibility(View.VISIBLE);
			for(TextView v : textViews)
				v.setVisibility(View.VISIBLE);

		}

		//Get Kind
		Exam.Kind kind = e.getKindEnum();

		//Set title and exam no
		textViews.get(0).setText(e.getName());

		//Set Thin Typeface
		Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
		textViews.get(0).setTypeface(myTypeface);

		//Fill Views with data
		switch(kind) {
		case KO: 
			if(e.getBonus().equals("-")) {
				//If there are no bnous ects -> hide the useless view, else -> show them
				textViews.get(1).setVisibility(View.GONE);

			} else {
				textViews.get(1).setText(String.format("%s: %s %s", this.BONUS, e.getBonus(), this.ECTS));

			}

			if(e.getMalus().equals("-")) {
				//If there are no malus ects -> hide the useless view, else -> show them
				textViews.get(2).setVisibility(View.GONE);

			} else {
				textViews.get(2).setText(String.format("%s: %s %s", this.MALUS, e.getMalus(), this.ECTS));

			}

			if(e.getMalus().equals("-") && e.getBonus().equals("-")) {
				//If both are not set, the View will be empty (both textview are hidden)! Show the first and say no ects
				textViews.get(1).setVisibility(View.VISIBLE);
				textViews.get(1).setText(this.NO_ECTS);
				textViews.get(2).setVisibility(View.GONE);

			}

			break;

		case PL: 
		case P: 
		case G: 
			if(e.isResignated()) {
				//If e is resignated, shw special info on the topic
				textViews.get(1).setText(String.format("%s: %s", this.STATE, this.STATE_RESIGNATED));
				textViews.get(2).setText(String.format("%s: %s", this.NOTE, e.getNoteName(this.getContext())));

			} else {
				//First field
				if(e.getStateEnum() == Exam.State.AN) {
					//If e is only AN, there is no grade to show. Display state: an
					textViews.get(1).setText(String.format("%s: %s (%s %s)", this.STATE, e.getStateName(this.getContext()), e.getECTS(), this.ECTS));

				} else {
					//e is not an -> be, nb or en. Show grade and ects
					textViews.get(1).setText(String.format("%s: %s (%s %s)", this.GRADE, e.getGrade(), e.getECTS(), this.ECTS));

				}

				//Second Field
				if(e.getKindEnum() == Exam.Kind.G) {
					//If e is generatde, show only the semester
					textViews.get(2).setText(String.format("%s: %s", this.SEMESTER, e.getSemester()));

				} else {
					//Else show attempt and semester
					textViews.get(2).setText(String.format("%s: %s (%s)", this.ATTEMPT, e.getTryCount(), e.getSemester()));

				}
			}

			break;

		case VL: 
			//Show state and sign that e is a vl
			textViews.get(1).setText(String.format("%s: %s (%s %s)", this.STATE, e.getStateName(this.getContext()), e.getECTS(), this.ECTS));
			textViews.get(2).setText(this.PRACTICAL_WORK);

			break;

		case UNDEFINED:
		default:
			//This should not happen. show state an Kind
			textViews.get(1).setText(String.format("%s: %s", this.STATE, e.getStateName(this.getContext())));
			textViews.get(2).setText(e.getKind());

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

		return convertView;
	}
}
