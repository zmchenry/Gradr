package edu.floridatech.android.Gradr.model;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.floridatech.android.Gradr.AssignmentFragment;
import edu.floridatech.android.Gradr.R;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
	private Activity mContext;
	private List<Category> mListDataTitle;
	private Typeface mFont;
	private AssignmentFragment mFrag;

	public ExpandableListAdapter(Activity activity,
			List<Category> listDataTitle, AssignmentFragment frag) {
		mContext = activity;
		mListDataTitle = listDataTitle;
		mFont = Typeface.createFromAsset(mContext.getAssets(),
				"Gotham-Light.ttf");
		mFrag = frag;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mListDataTitle.get(groupPosition).getChild(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean lastChild, View convertView, ViewGroup parent) {
		final String childText = mListDataTitle.get(groupPosition)
				.getChild(childPosition).getAssignmentName();
		final String childGrade = mListDataTitle.get(groupPosition)
				.getChild(childPosition).getGradeRecieved();
		if (childText.equals("Button")) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater
					.inflate(R.layout.expandable_add_button, null);
			Button addButton = (Button) convertView
					.findViewById(R.id.add_assignment_button);
			final EditText editText = (EditText) convertView
					.findViewById(R.id.add_assignment_edit);
			final EditText gradeText = (EditText) convertView
					.findViewById(R.id.add_assignment_grade);
			editText.setTypeface(mFont);
			gradeText.setTypeface(mFont);
			addButton.setTypeface(mFont);
			addButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String e = editText.getText().toString();
					String g = gradeText.getText().toString();
					if (e.trim().equals("")) {
						makeToast("Assignment name empty");
					} else if (g.trim().equals("")) {
						makeToast("Grade left empty");
					} else if (Double.parseDouble(g) > 100) {
						makeToast("Grade over 100%");
					} else {
						mFrag.onAddClicked(groupPosition, childPosition, e, g);
						editText.setText("");
						gradeText.setText("");
					}

				}
			});
			convertView.setPadding(15, 0, 0, 0);
			return convertView;
		} else {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_child_item, null);

		}
		convertView.setPadding(15, 0, 0, 0);
		TextView assignmentName = (TextView) convertView
				.findViewById(R.id.custom_title);
		TextView grade = (TextView) convertView
				.findViewById(R.id.custom_grade_title);

		assignmentName.setTypeface(mFont);
		assignmentName.setText(childText);
		grade.setTypeface(mFont);
		grade.setText(childGrade);
		if (!grade.getText().equals("--")) {
			String textColor = calculateTextColor(Double.parseDouble(grade
					.getText().toString()));
			int parsed = Color.parseColor(textColor.trim());
			grade.setTextColor(parsed);
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mListDataTitle.get(groupPosition).getChildCount();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mListDataTitle.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mListDataTitle.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String headerTitle = mListDataTitle.get(groupPosition).getCategory();
		String weight = mListDataTitle.get(groupPosition).getWeight();
		double grade = mListDataTitle.get(groupPosition).getGrade();
		;
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group, null);
		}
		TextView listTitle = (TextView) convertView
				.findViewById(R.id.list_item_title);
		TextView listWeight = (TextView) convertView
				.findViewById(R.id.list_item_title_weight);
		TextView listGrade = (TextView) convertView
				.findViewById(R.id.list_item_title_grade);
		mFrag.setGradeColor(listGrade, grade, true);
		listTitle.setTypeface(mFont);
		listTitle.setText(headerTitle);
		listWeight.setTypeface(mFont);
		listWeight.setText(weight);
		listGrade.setTypeface(mFont);
		DecimalFormat format = new DecimalFormat("0.00##");
		listGrade.setText(format.format(grade));

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void turnOptionOff(View toolbar) {
		toolbar.setFocusable(false);
		toolbar.setFocusableInTouchMode(false);
		toolbar.setVisibility(View.GONE);
	}

	public void turnOptionOn(View toolbar) {
		toolbar.setFocusable(true);
		toolbar.setFocusableInTouchMode(true);
		toolbar.setVisibility(View.VISIBLE);
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
	
	// Make a custom toast
		public void makeToast(String toastText) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View toastView = inflater.inflate(R.layout.toast_layout, null);
			TextView text = (TextView) toastView.findViewById(R.id.toast_text);
			text.setText(toastText);
			Toast toast = new Toast(mContext);
			toast.setView(toastView);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
}
