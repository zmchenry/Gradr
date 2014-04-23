package edu.floridatech.android.Gradr;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import edu.floridatech.android.Gradr.db.DbHelper;
import edu.floridatech.android.Gradr.model.Semester;

public class MainFragment extends Fragment {
	private DbHelper db;
	private OnItemSelectedListener listener;
	private Typeface blackFont;

	public interface OnItemSelectedListener {
		public void onTaskSelected(String buttonName);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnItemSelectedListener) {
			listener = (OnItemSelectedListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement MainFragment.OnItemSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		blackFont = Typeface.createFromAsset(getActivity().getAssets(),
				"Gotham-Black.ttf");
		View view = inflater.inflate(R.layout.main_screen, container, false);
		db = new DbHelper(getActivity());
		/* Start setup of TextViews */
		TextView text = (TextView) view.findViewById(R.id.overall_gpa_text);
		TextView number = (TextView) view.findViewById(R.id.overall_gpa_number);
		TextView title = (TextView) view.findViewById(R.id.title_text);
		TextView subtitle = (TextView) view.findViewById(R.id.title_subtext);
		Button gradeBook = (Button) view.findViewById(R.id.background_button);
		Button assignmentPlanner = (Button) view
				.findViewById(R.id.assignment_planner_button);
		Typeface boldFont = Typeface.createFromAsset(getActivity().getAssets(),
				"Gotham-Bold.ttf");
		title.setTypeface(boldFont);
		Typeface bookFont = Typeface.createFromAsset(getActivity().getAssets(),
				"Gotham-Book.ttf");
		subtitle.setTypeface(bookFont);

		gradeBook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onTaskSelected("Gradebook");
			}
		});

		assignmentPlanner.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onTaskSelected("Planner");
			}
		});

		// Needs to be replaced with real data
		double percentage = calculatePercentage();
		// Get convert the percentage to a GPA and a text color
		String gpaText = percentToGPA(percentage);
		String textColor = calculateTextColor(percentage);
		int parsed = Color.parseColor(textColor.trim());
		// Apply color to both text views and change their font
		setupTextView(text, parsed);
		setupTextView(number, parsed);
		number.setText(gpaText);
		/* End setup of TextViews */

		return view;
	}

	// Do math to convert the percentage to a hexadecimal number
	public String calculateTextColor(double percentage) {
		double r = percentage < 50 ? 255 : Math
				.floor(255 - (percentage * 2 - 100) * 255 / 100);
		double g = percentage > 50 ? 255 : Math
				.floor((percentage * 2) * 255 / 100);
		String rStr = convertToString(r);
		String gStr = convertToString(g);
		return String.format("#%s%s00", rStr, gStr);
	}

	// Convert this number to a string
	public String convertToString(double num) {
		int n = (int) num;
		String hex = Integer.toHexString(n);
		if (n < 16) {
			hex = "0" + hex;
		}
		return String.format(Locale.US, "%s", hex);
	}

	public String percentToGPA(double percentage) {
		double gpa = (percentage / 20) - 1;
		if (gpa < 0) {
			gpa = 0.00;
		}
		DecimalFormat format = new DecimalFormat("0.00");
		return format.format(gpa);
	}

	public void setupTextView(TextView textView, int parsed) {
		changeFont(textView);
		textView.setTextColor(parsed);
	}

	public void changeFont(TextView textView) {
		textView.setTypeface(blackFont);
	}

	public double calculatePercentage() {
		List<Semester> semesters = db.getSemesterList();
		double percentage = 0;
		for (int i = 0; i < semesters.size(); i++) {
			percentage += semesters.get(i).getGrade();
		}

		if (semesters.size() > 0) {
			return percentage / semesters.size();
		} else {
			return 100;
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceBundle) {
		super.onViewCreated(view, savedInstanceBundle);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		db = new DbHelper(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		db.close();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStop() {
		super.onStop();
		db.close();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		db.close();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}
}
