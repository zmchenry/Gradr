package edu.floridatech.android.Gradr;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;
import edu.floridatech.android.Gradr.db.DbHelper;
import edu.floridatech.android.Gradr.model.Assignment;
import edu.floridatech.android.Gradr.model.Category;
import edu.floridatech.android.Gradr.model.Classes;
import edu.floridatech.android.Gradr.model.ExpandableListAdapter;

public class AssignmentFragment extends Fragment {
	private ArrayList<Category> listDataTitles;
	private ExpandableListAdapter listAdapter;
	private DbHelper db;
	private long classID;
	private String className;
	private String classSemester;
	private double classGrade;
	private TextView displayGrade;
	private TextView displayText;
	private Typeface mLight;
	private ExpandableListView classList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Grab information from bundle
		if (savedInstanceState != null) {
			classID = getArguments().getLong("classID");
			className = getArguments().getString("className");
			classSemester = getArguments().getString("classSemester");
			classGrade = getArguments().getDouble("classGrade");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.assignment_screen, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Create font that is used quite a bit
		mLight = Typeface.createFromAsset(getActivity().getAssets(),
				"Gotham-Light.ttf");
		// Open database
		db = new DbHelper(getActivity());

		// Get all category items and get all assignments for each category
		listDataTitles = db.getCategoryList(className);
		for (int i = 0; i < listDataTitles.size(); i++) {
			listDataTitles.get(i).setItems(
					db.getAssignments(className, listDataTitles.get(i)
							.getCategory()));
		}
		/* Start setup of TextViews */
		displayText = (TextView) getActivity().findViewById(
				R.id.assignments_overall_gpa_text);
		displayGrade = (TextView) getActivity().findViewById(
				R.id.assignments_overall_gpa_number);
		Button addCategory = (Button) getActivity().findViewById(
				R.id.add_category_button);
		final EditText addCategoryEdit = (EditText) getActivity().findViewById(
				R.id.add_category_edit);
		final EditText addCategoryWeight = (EditText) getActivity()
				.findViewById(R.id.add_category_weight);

		// Calculate percentage based on data in database
		double percentage = calculateDisplayGrade();

		// Set color of displays based on percentage
		setGradeColor(displayText, percentage, false);
		setGradeColor(displayGrade, percentage, true);
		
		// Apply text to each text box
		addCategoryEdit.setTypeface(mLight);
		addCategory.setTypeface(mLight);
		addCategoryWeight.setTypeface(mLight);
		/* End setup of TextViews */

		// Create list view and assign adapter
		classList = (ExpandableListView) getActivity().findViewById(
				R.id.category_names);
		listAdapter = new ExpandableListAdapter(getActivity(), listDataTitles,
				this);
		classList.setAdapter(listAdapter);
	
		addCategory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Sanity checks
				if (addCategoryEdit.getText().toString().trim().equals("")) {
					makeToast("Category name is blank!");
				} else if (addCategoryWeight.getText().toString().trim()
						.equals("")) {
					makeToast("Category weight is blank!");
				} else if (weightCheck(Double.parseDouble(addCategoryWeight
						.getText().toString())) > 100) {
					makeToast("Category weight cannot be more than 100!");
				} else {
					// We are sane so start grabbing data
					Category category = new Category(className, addCategoryEdit
							.getText().toString(), addCategoryWeight.getText()
							.toString());
					Assignment assignment = new Assignment(className, category
							.getCategory(), "Button");
					category.addItem(assignment);

					long id = db.createCategory(category); 	 // Add category to the database
					db.createAssignment(assignment);
					category.setId(id); 				  	 // Add category to the list
					listDataTitles.add(category);
					listAdapter.notifyDataSetChanged(); 	 // Update list view
					addCategoryEdit.setText("");
					addCategoryWeight.setText("");
					double curGrade = calculateDisplayGrade();
					setGradeColor(displayText, curGrade, false); // Set colors again
					setGradeColor(displayGrade, curGrade, true);
				}
			}

		});

		displayGrade.addTextChangedListener(new TextWatcher() {
			// Not used but have to implement
			@Override
			public void afterTextChanged(Editable arg0) {

			}
			// Not used but have to implement
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			// Update class grade display if the grade is changed here
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String newGrade = displayGrade.getText().toString();
				Classes classObj = new Classes(classID, classSemester,
						className, Double.parseDouble(newGrade.substring(0,
								newGrade.length() - 1)));
				db.updateClass(classObj, classObj.getSemesterName());
			}
		});

		// Handle long clicks
		classList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// If long click on category label
				if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
					editParentItem(view, position);
					
					double percentage = calculateDisplayGrade();
					String gpaText = percentToGPA(percentage);
					String textColor = calculateTextColor(percentage);
					int parsed = Color.parseColor(textColor.trim());
					// Apply color to both text views and change their font
					setupTextView(displayText, parsed);
					setupTextView(displayGrade, parsed);
					displayGrade.setText(gpaText);
				} else {
				}
				return true;
			}
		});

		// No long click needed on child only click
		classList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					final int groupPosition, final int childPosition, long id) {
				editChildItem(v, groupPosition, childPosition);
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
		double gpa = (percentage / 25);
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

	
	public int findIndexOfCategory(String categoryName) {
		for (int i = 0; i < listDataTitles.size(); i++) {
			if (listDataTitles.get(i).toString().equals(categoryName)) {
				return i;
			}
		}
		return -1;
	}

	// Used when adding an assignment to a category list
	public void onAddClicked(int groupPosition, int childPosition,
			String assignmentName, String grade) {
		Assignment assignment = new Assignment(className, listDataTitles.get(
				groupPosition).toString(), assignmentName, grade);
		// Add assignment to category list
		listDataTitles.get(groupPosition).addItem(assignment);
		
		// Set displays and calculate grade
		double curGrade = calculateDisplayGrade();
		setGradeColor(displayText, curGrade, false);
		setGradeColor(displayGrade, curGrade, true);
		db.createAssignment(assignment);
		listAdapter.notifyDataSetChanged();
	}

	private void editChildItem(View view, final int parentPosition,
			final int childPosition) {
		// Get the view of the toolbar for the whole list item view
		final View toolbar = view.findViewById(R.id.toolbar);
		final EditText editText = (EditText) view.findViewById(R.id.edit_text);
		final EditText editGrade = (EditText) view
				.findViewById(R.id.edit_grade_text);
		// Get the text from the list for editing
		editText.setText(listDataTitles.get(parentPosition)
				.getChild(childPosition).getAssignmentName());
		editGrade.setText(listDataTitles.get(parentPosition)
				.getChild(childPosition).getGradeRecieved());
		listAdapter.turnOptionOn(toolbar);
		Button accept = (Button) view.findViewById(R.id.replace);
		Button delete = (Button) view.findViewById(R.id.remove);

		accept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (editText.getText().toString() != null) {
					Assignment assignment = listDataTitles.get(parentPosition)
							.getChild(childPosition);
					assignment.setAssignmentName(editText.getText().toString());
					// Sanity checks for if blank name is entered
					if (editGrade.getText().toString().trim().equals("")) {
						assignment.setGradeRecieved("--");
					// Sanity check for grade over 100
					} else if (Double.parseDouble(editGrade.getText()
							.toString()) > 100) {
						makeToast("Grade over 100");
					} else {
						assignment.setGradeRecieved(editGrade.getText()
								.toString());
					}
					db.updateAssignment(assignment); // Update database
					listDataTitles.get(parentPosition).setItem(childPosition,
							assignment);
					double curGrade = calculateDisplayGrade();
					setGradeColor(displayText, curGrade, false);
					setGradeColor(displayGrade, curGrade, true);
					listAdapter.notifyDataSetChanged(); // Update listview
					listAdapter.turnOptionOff(toolbar); // Make toolbar
					// invisible
				} else {
					makeToast("Assignment name empty");
				}
			}

		});

		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				db.deleteAssignment(listDataTitles.get(parentPosition)
						.getItems().get(childPosition)); // Remove
				// from
				// database
				listDataTitles.get(parentPosition).delete(childPosition); // Remove from list
				double curGrade = calculateDisplayGrade();
				setGradeColor(displayText, curGrade, false);
				setGradeColor(displayGrade, curGrade, true);
				listAdapter.notifyDataSetChanged(); // Update listview
				listAdapter.turnOptionOff(toolbar);
			}
		});
	}

	private void editParentItem(View view, final int parentPosition) {
		// Get the view of the toolbar for the whole list item view
		final View toolbar = view.findViewById(R.id.toolbar);
		final EditText editText = (EditText) view.findViewById(R.id.edit_text);
		final EditText weightText = (EditText) view
				.findViewById(R.id.edit_text_weight);
		editText.setTypeface(mLight);
		editText.setTypeface(mLight);
		// Get the text from the list for editing
		editText.setText(listDataTitles.get(parentPosition).getCategory());
		weightText.setText(listDataTitles.get(parentPosition).getWeight());
		listAdapter.turnOptionOn(toolbar);
		Button accept = (Button) view.findViewById(R.id.replace);
		Button delete = (Button) view.findViewById(R.id.remove);

		accept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (editText.getText().toString() != null) {
					Category category = listDataTitles.get(parentPosition);
					String oldCategory = category.getCategory();
					String oldWeight = category.getWeight();
					// Sanity check for blank category name
					if (!weightText.getText().toString().trim().equals("")) {
						category.setWeight(weightText.getText().toString());
					}
					// Make sure weights do not go above 100
					if (weightCheck(0) > 100) {
						makeToast("Category weight cannot be more than 100!");
						category.setWeight(oldWeight);
						return;
					}

					category.setCategory(editText.getText().toString());
					db.updateCategory(category, oldCategory); // Update database
					listDataTitles.set(parentPosition, category);
					listAdapter.notifyDataSetChanged(); // Update listview
					listAdapter.turnOptionOff(toolbar); // Make toolbar
														// invisible
					double curGrade = calculateDisplayGrade();
					setGradeColor(displayText, curGrade, false);
					setGradeColor(displayGrade, curGrade, true);
				} else {
					makeToast("Category name empty");
				}
			}

		});

		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				db.deleteCategory(listDataTitles.get(parentPosition)); // Remove	from database
				listDataTitles.remove(parentPosition); 				   // Remove from list
				listAdapter.notifyDataSetChanged(); 				   // Update listview
				listAdapter.turnOptionOff(toolbar);
				double curGrade = calculateDisplayGrade();
				setGradeColor(displayText, curGrade, false);
				setGradeColor(displayGrade, curGrade, true);
			}
		});
	}

	public void setGradeColor(TextView number, double percentage,
			boolean isNumber) {
		String textColor = calculateTextColor(percentage);
		int parsed = Color.parseColor(textColor.trim());
		// Apply color to both text views and change their font
		setupTextView(number, parsed);
		if (isNumber) {
			DecimalFormat format = new DecimalFormat("0.00");
			String formatted = format.format(percentage);
			number.setText(formatted + "%");
		}
	}

	public double calculateDisplayGrade() {
		if (listDataTitles.size() < 1) {
			return 100;
		} else {
			double percentage = 0;
			double totalWeight = 0;
			for (int i = 0; i < listDataTitles.size(); i++) {
				percentage += (Double.parseDouble(listDataTitles.get(i)
						.getWeight()) * listDataTitles.get(i).getGrade());
				totalWeight += (Double.parseDouble(listDataTitles.get(i)
						.getWeight()));
			}
			return percentage / totalWeight;
		}
	}
	// Fragments cannot have constructors
	public static Fragment newInstance(long id, String semesterName,
			String name, double grade) {
		AssignmentFragment assignmentFragment = new AssignmentFragment();
		Bundle args = new Bundle();
		args.putLong("classID", id);
		args.putString("classSemester", semesterName);
		args.putString("className", name);
		args.putDouble("classGrade", grade);
		assignmentFragment.setArguments(args);
		assignmentFragment.className = name;
		assignmentFragment.classID = id;
		assignmentFragment.classSemester = semesterName;
		assignmentFragment.classGrade = grade;
		return assignmentFragment;
	}

	public double weightCheck(double extra) {
		double total = 0;
		for (Category c : listDataTitles) {
			total += Double.parseDouble(c.getWeight());
		}
		total += extra;
		return total;
	}

	// Make a custom toast
	public void makeToast(String toastText) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
