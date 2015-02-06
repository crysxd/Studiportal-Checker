package de.hfu.studiportal.view;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.data.Exam;

public class ExamActivity extends DialogHostActivity {
	
	public static final String ARG_EXAM = "exam";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Set content View
		this.setContentView(R.layout.activity_exam);

        //Set up Toolbar
        Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(bar);

		//Get arg
		Exam e = (Exam) getIntent().getExtras().get(ExamActivity.ARG_EXAM);
		
		//Set Title
        this.getSupportActionBar().setTitle(e.getName());

        //Set other information
		//If there is a grade
		if(e.getGrade() != null && e.getGrade().length() > 0) {
			this.setText(e.getGrade(), R.id.textGrade);
			this.setText(e.getStateName(this), R.id.textState);
			
		//If there is no grade
		} else {
			this.setText(e.getStateName(this), R.id.textGrade);
			this.setText("", R.id.textState);
			
		}
		
		//Set icon
		int d = 0;
		switch (e.getStateEnum()) {
		case AN: d = R.drawable.ic_an; break;
		case BE: d = R.drawable.ic_be; break;
		case NB: d = R.drawable.ic_nb; break;
		case EN: d = R.drawable.ic_en; break;
		default:
			
		}
		
		TextView tv = (TextView) findViewById(R.id.textState);
		tv.setCompoundDrawablesWithIntrinsicBounds(d, 0, 0, 0);
	}
	
	private void setText(String text, int id) {
		TextView tv = (TextView) this.findViewById(id);
		tv.setText(text);
		
	}

}
