package edu.floridatech.android.Gradr.model;

import java.util.ArrayList;
import java.util.List;

public class Semester {
	private long mId;
	private String mName;
	private List<Classes> mClasses;
	private double mGrade;
	public Semester() {
	}

	public Semester(String name) {
		mName = name;
		mClasses = new ArrayList<Classes>();
	}
	
	public Semester(String name, List<Classes> classes) {
		mName = name;
		mClasses = classes;
	}
	
	public Semester(long id, String name, double grade) {
		mId = id;
		mName = name;
		mGrade = grade;
	}

	/**
	 * @return the mId
	 */
	public long getId() {
		return mId;
	}

	/**
	 * @param mId
	 *            the mId to set
	 */
	public void setId(long id) {
		mId = id;
	}

	/**
	 * @return the mName
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @param mName
	 *            the mName to set
	 */
	public void setName(String name) {
		mName = name;
	}
	
	public void setClasses(List<Classes> classes) {
		mClasses = classes;
	}
	
	public Classes getClass(int position) {
		return mClasses.get(position);
	}
	
	public void addClass(Classes classItem) {
		mClasses.add(classItem);
	}
	
	public List<Classes> getClasses() {
		return mClasses;
	}
	
	public String toString() {
		return mName;
	}

	public double getGrade() {
		return mGrade;
	}
	
	public void setGrade(double grade) {
		mGrade = grade;
	}
}
