package edu.floridatech.android.Gradr;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import edu.floridatech.android.Gradr.model.Classes;

public class MainActivity extends FragmentActivity implements
		MainFragment.OnItemSelectedListener,
		SemesterFragment.OnSemesterSelectedListener,
		ClassFragment.OnClassSelectedListener {
	private Fragment leftFragment;
	private Fragment rightFragment;
	private boolean mDualPane;
	private FragmentManager manager;
	private String TAG = "Main Fragment -> ";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		View right = findViewById(R.id.fragment_right);
		mDualPane = right != null;
		if (mDualPane) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		if (mDualPane) {
			rightFragment = new SemesterFragment();
			transaction.replace(R.id.fragment_right, rightFragment);
		}
		leftFragment = new MainFragment();
		transaction.replace(R.id.fragment_left, leftFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	/* Main Fragment Input */
	@Override
	public void onTaskSelected(String buttonName) {
		Fragment previous = manager.findFragmentById(R.id.fragment_right);
		if (buttonName.equals("Gradebook")) {
			if (mDualPane
					&& !rightFragment.getClass().toString()
							.equals("SemesterFragment")) {
				Log.e(TAG, TAG + " Dual-Pane Semster");
				rightFragment = new SemesterFragment();
			}
			leftFragment = new SemesterFragment();
			Log.e(TAG, TAG + "Semster");
		} else if (buttonName.equals("Planner")) {
			if (mDualPane
					&& !rightFragment.getClass().toString()
							.equals("PlannerFragment")) {
				Log.e(TAG, TAG + "Dual Pane - Planner");
				// rightFragment = new PlannerFragment();
			}
			leftFragment = new MainFragment();
		} else {
			Log.e(TAG, "Invalid Input");
		}
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.fragment_left, leftFragment);
		if (rightFragment != null && !rightFragment.equals(previous)) {
			transaction.replace(R.id.fragment_right, rightFragment);
		}
		transaction.addToBackStack(null);
		transaction.commit();
	}

	/* Semester Selected */
	@Override
	public void onSemesterSelected(long id, String semesterName) {
		Log.e(TAG, "Semester item selected: " + semesterName);
		Fragment classFragment = ClassFragment.newInstance(id, semesterName);
		Fragment semesterFragment = new SemesterFragment();
		if (mDualPane) {
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(R.id.fragment_right, classFragment);
			transaction.replace(R.id.fragment_left, semesterFragment);
			transaction.addToBackStack(null);
			transaction.commit();
			leftFragment = semesterFragment;
			rightFragment = classFragment;
		} else {
			leftFragment = classFragment;
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(R.id.fragment_left, classFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	/* Class Selected */
	@Override
	public void onClassSelected(long semesterId, Classes classObj) {
		Log.e(TAG, "Class item selected: " + classObj.getName());
		Fragment assignmentFragment = AssignmentFragment.newInstance(
				classObj.getId(), classObj.getSemesterName(),
				classObj.getName(), classObj.getGrade());

		Fragment classFragment = ClassFragment.newInstance(semesterId,
				classObj.getSemesterName());

		if (mDualPane) {
			FragmentTransaction transaction = manager.beginTransaction();
			leftFragment = rightFragment;
			rightFragment = assignmentFragment;
			transaction.replace(R.id.fragment_left, classFragment);
			transaction.replace(R.id.fragment_right, assignmentFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		} else {
			leftFragment = assignmentFragment;
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(R.id.fragment_left, assignmentFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStop() {
		super.onStop();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
