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
import android.text.Editable;
import android.text.TextWatcher;
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

public class ClassFragment extends Fragment {
	private String semesterName;

	private TextView overallGPAText;
	private TextView overallGPANumber;
	private Button addClassButton;
	private EditText addClassEdit;
	private ListView classListView;
	private long mSemesterId;
	private ArrayList<Classes> mClassList;
	private CustomListAdapter<Classes> listAdapter;
	private DbHelper db;

	private OnClassSelectedListener listener;

	public interface OnClassSelectedListener {
		public void onClassSelected(long id, Classes classObj);
	}

	// Sanity check for implementing listener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnClassSelectedListener) {
			listener = (OnClassSelectedListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement MainFragment.OnClassSelectedListener");
		}
	}
	
	// Fragments cannot have constructor
	public static Fragment newInstance(long id, String semesterName) {
		ClassFragment classFragment = new ClassFragment();
		Bundle args = new Bundle();
		args.putLong("semesterId", id);
		args.putString("semesterName", semesterName);
		classFragment.mSemesterId = id;
		classFragment.semesterName = semesterName;
		classFragment.setArguments(args);
		return classFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.class_screen, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			semesterName = getArguments().getString("semesterName");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			semesterName = args.getString("semesterName");
		}

		db = new DbHelper(getActivity());
		mClassList = new ArrayList<Classes>();
		List<Classes> dbClassList = db.getClassList(semesterName);
		for (int i = 0; i < dbClassList.size(); i++) {
			mClassList.add(dbClassList.get(i));
		}

		/* Start setup of TextViews */
		overallGPAText = (TextView) getActivity().findViewById(
				R.id.class_overall_gpa_text);
		overallGPANumber = (TextView) getActivity().findViewById(
				R.id.class_overall_gpa_number);
		addClassButton = (Button) getActivity().findViewById(
				R.id.add_class_button);
		addClassEdit = (EditText) getActivity().findViewById(
				R.id.add_class_edit);

		calculateDisplayGrade(true);

		Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
				"Gotham-Light.ttf");
		addClassEdit.setTypeface(font);
		addClassButton.setTypeface(font);
		/* End setup of TextViews */

		classListView = (ListView) getActivity().findViewById(
				R.id.class_names);
		listAdapter = new CustomListAdapter<Classes>(getActivity(), mClassList);
		classListView.setAdapter(listAdapter);
		classListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				listener.onClassSelected(mSemesterId, mClassList.get(position));
			}
		});

		addClassButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (addClassEdit.getText().toString().equals("")) {
					makeToast("Class name is blank!");
				} else {
					Classes classObj = new Classes(addClassEdit.getText()
							.toString(), semesterName);
					classObj.setGrade(100);
					long id = db.createClass(classObj);
					classObj.setId(id); // Add it to the list

					mClassList.add(classObj);
					listAdapter.notifyDataSetChanged(); // Update list view
					addClassEdit.setText("");
				}
				calculateDisplayGrade(true);
			}
		});

		classListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				editListViewItem(view, position);
				return true;
			}
		});
		
		overallGPAText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				double newGrade = calculateDisplayGradePercentage();
				Semester semester = new Semester(mSemesterId, semesterName, newGrade);
				db.updateSemester(semester, semesterName);
			}

		});

	}

	private void editListViewItem(View view, final int position) {
		// Get the view of the toolbar for the whole list item view
		final View toolbar = view.findViewById(R.id.toolbar);
		final EditText editText = (EditText) view.findViewById(R.id.edit_text);
		// Get the text from the list for editing
		editText.setText(mClassList.get(position).getName());
		listAdapter.turnOptionOn(toolbar);
		Button accept = (Button) view.findViewById(R.id.replace);
		Button delete = (Button) view.findViewById(R.id.remove);

		accept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (editText.getText().toString() != null) {
					Classes classes = mClassList.get(position);
					String oldClassName = classes.getName();
					classes.setName(editText.getText().toString()); // Commit
																	// changes
																	// to object
					db.updateClass(classes, oldClassName); // Update database
					mClassList.set(position, classes);
					listAdapter.notifyDataSetChanged(); // Update listview
					listAdapter.turnOptionOff(toolbar); // Make toolbar
														// invisible
				} else {
					makeToast("Semester name is empty");
				}
			}

		});

		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				db.deleteSemester(mClassList.get(position).getId()); // Remove
																		// from
																		// database
				mClassList.remove(position); // Remove from list
				listAdapter.notifyDataSetChanged(); // Update listview
				listAdapter.turnOptionOff(toolbar);
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
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
				"Gotham-Black.ttf");
		textView.setTypeface(font);
	}

	public void calculateDisplayGrade(boolean GPA) {
		double percentage = 0;
		for (int i = 0; i < mClassList.size(); i++) {
			percentage += mClassList.get(i).getGrade();
		}
		if (mClassList.size() > 0) {
			percentage = (percentage / mClassList.size());

		} else {
			percentage = 100;
		}
		String gpaText;
		if (GPA) {
			gpaText = percentToGPA(percentage);
		} else {
			gpaText = Double.toString(percentage);
		}
		String textColor = calculateTextColor(percentage);
		int parsed = Color.parseColor(textColor.trim());
		// Apply color to both text views and change their font
		setupTextView(overallGPAText, parsed);
		setupTextView(overallGPANumber, parsed);
		overallGPANumber.setText(gpaText);
	}
	
	public double calculateDisplayGradePercentage() {
		double percentage = 0;
		for (int i = 0; i < mClassList.size(); i++) {
			percentage += mClassList.get(i).getGrade();
		}
		if (mClassList.size() > 0) {
			percentage = (percentage / mClassList.size());

		} else {
			percentage = 100;
		}
		return percentage;
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
