package de.hfu.studiportal.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
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

		// Set ad
		ImageView ad = (ImageView) this.findViewById(R.id.imageViewAd);
        String campain = "null";
		if(e.getStateEnum() == Exam.State.BE || e.getStateEnum() == Exam.State.NB) {
			if(e.getGrade().startsWith("1") || e.getGrade().startsWith("2")) {
				ad.setImageResource(R.drawable.add_01);
                campain = "ad_01";

			} else if(e.getGrade().startsWith("5")) {
				ad.setImageResource(R.drawable.add_02);
                campain = "ad_02";

			} else {
				ad.setImageResource(R.drawable.add_02);
                campain = "ad_03";

			}
		} else {
			ad.setImageDrawable(null);

		}

        final String campainFinal = campain;
        ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=de.savedroid&hl=de&referrer=utm_source%3DStudiportal%26utm_medium%3DBanner%26utm_campaign%3D" + campainFinal));
                startActivity(i);

            }
        });

		TextView tv = (TextView) findViewById(R.id.textState);
		tv.setCompoundDrawablesWithIntrinsicBounds(d, 0, 0, 0);
	}

	private void setText(String text, int id) {
		TextView tv = (TextView) this.findViewById(id);
		tv.setText(text);

	}

}
