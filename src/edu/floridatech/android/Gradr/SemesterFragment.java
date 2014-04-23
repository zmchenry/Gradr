package edu.floridatech.android.Gradr;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.floridatech.android.Gradr.db.DbHelper;
import edu.floridatech.android.Gradr.model.Classes;
import edu.floridatech.android.Gradr.model.CustomListAdapter;
import edu.floridatech.android.Gradr.model.Semester;

public class SemesterFragment extends Fragment {

	private TextView overallGPAText;
	private TextView overallGPANumber;
	private Button addSemesterButton;
	private EditText addSemesterEdit;
	private ListView semesterList;
	private CustomListAdapter<Semester> listAdapter;
	private ArrayList<Semester> semesterData;
	private DbHelper db;
	private Typeface blackFont;
	private Typeface lightFont;
	private OnSemesterSelectedListener listener;

	public interface OnSemesterSelectedListener {
		public void onSemesterSelected(long id, String semesterName);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnSemesterSelectedListener) {
			listener = (OnSemesterSelectedListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement MainFragment.OnSemesterSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.semester_screen, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		db = new DbHelper(getActivity()); // Open database
		semesterData = new ArrayList<Semester>(); // Instantiate list
		List<Semester> dbSemesters = db.getSemesterList(); // Retrieve data from
		for (int i = 0; i < dbSemesters.size(); i++) {
			semesterData.add(dbSemesters.get(i));
		}

		blackFont = Typeface.createFromAsset(getActivity().getAssets(),
				"Gotham-Black.ttf");
		lightFont = Typeface.createFromAsset(getActivity().getAssets(),
				"Gotham-Light.ttf");

		/* Start setup of TextViews */
		overallGPAText = (TextView) getActivity().findViewById(
				R.id.semester_overall_gpa_text);
		overallGPANumber = (TextView) getActivity().findViewById(
				R.id.semester_overall_gpa_number);
		addSemesterButton = (Button) getActivity().findViewById(
				R.id.add_semester_button);
		addSemesterEdit = (EditText) getActivity().findViewById(
				R.id.add_semester_edit);
		// Get convert the percentage to a GPA and a text color
		calculateDisplayGrade();
		addSemesterButton.setTypeface(lightFont);
		addSemesterEdit.setTypeface(lightFont);
		/* End setup of TextViews */

		listAdapter = new CustomListAdapter<Semester>(getActivity(),
				semesterData);
		semesterList = (ListView) getActivity().findViewById(
				R.id.semester_names);
		semesterList.setAdapter(listAdapter);
		// Listener for add button
		addSemesterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (addSemesterEdit.getText().toString().equals("")) {
					makeToast("Semester name is blank!");
				} else {
					Semester semester = new Semester(addSemesterEdit.getText()
							.toString());
					semester.setGrade(100);
					long id = db.createSemester(semester); // Add it to the
															// database
					semester.setId(id); // Add it to the list

					semesterData.add(semester);
					listAdapter.notifyDataSetChanged(); // Update list view
					addSemesterEdit.setText("");
					calculateDisplayGrade();
				}

			}
		});

		semesterList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				listener.onSemesterSelected(semesterData.get(position).getId(),
						semesterData.get(position).toString());
			}
		});

		semesterList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				editListViewItem(view, position); // On long click edit semester
													// name
				return true;
			}
		});
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

	private void editListViewItem(View view, final int position) {
		// Get the view of the toolbar for the whole list item view
		final View toolbar = view.findViewById(R.id.toolbar);
		final EditText text = (EditText) view.findViewById(R.id.edit_text);
		// Get the text from the list for editing
		text.setText(semesterData.get(position).getName());
		listAdapter.turnOptionOn(toolbar);
		Button accept = (Button) view.findViewById(R.id.replace);
		Button delete = (Button) view.findViewById(R.id.remove);

		accept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (text.getText().toString() != null) {
					Semester semester = semesterData.get(position);
					String oldSemesterName = semester.getName();
					semester.setName(text.getText().toString()); // Commit
																	// changes
																	// to object
					db.updateSemester(semester, oldSemesterName); // Update
																	// database
					semesterData.set(position, semester);
					listAdapter.notifyDataSetChanged(); // Update listview
					listAdapter.turnOptionOff(toolbar); // Make toolbar
														// invisible
				} else {
					makeToast("Semester name empty");
				}
			}

		});

		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				db.deleteSemester(semesterData.get(position).getId()); // Remove
																		// from
																		// database
				semesterData.remove(position); // Remove from list
				listAdapter.notifyDataSetChanged(); // Update listview
				listAdapter.turnOptionOff(toolbar);
			}
		});
	}

	public void calculateDisplayGrade() {
		double percentage = 0;

		// Set all semesters percentages so they can be used for gpa
		for (int i = 0; i < semesterData.size(); i++) {
			percentage = 0;
			List<Classes> classes = db.getClassList(semesterData.get(i)
					.getName());
			for (int j = 0; j < classes.size(); j++) {
				percentage += classes.get(j).getGrade();
			}
			if (classes.size() > 0) {
				semesterData.get(i).setGrade((percentage / classes.size()));
			} else {
				semesterData.get(i).setGrade(100);
			}
			db.updateSemester(semesterData.get(i), semesterData.get(i)
					.getName());
		}
		percentage = 0;
		// Calculate final gpa.
		for (int i = 0; i < semesterData.size(); i++) {
			percentage += semesterData.get(i).getGrade();
		}
		if (semesterData.size() > 0) {
			percentage = (percentage / semesterData.size());

		} else {
			percentage = 100;
		}
		String gpaText = percentToGPA(percentage);
		String textColor = calculateTextColor(percentage);
		int parsed = Color.parseColor(textColor.trim());
		// Apply color to both text views and change their font
		setupTextView(overallGPAText, parsed);
		setupTextView(overallGPANumber, parsed);
		overallGPANumber.setText(gpaText);
	}
	
	public void makeToast(String toastText) {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View toastView = inflater.inflate(R.layout.toast_layout, null);
		TextView text = (TextView) toastView.findViewById(R.id.toast_text);
		text.setText(toastText);
		Toast toast = new Toast(getActivity());
		toast.setView(toastView);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceBundle) {
		super.onViewCreated(view, savedInstanceBundle);
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
