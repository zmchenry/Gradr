package edu.floridatech.android.Gradr.model;

import java.util.ArrayList;
import java.util.List;

public class Classes {
	private long mId;
	private String mName;
	private String mSemester;
	private List<Category> mCategories;
	private double mGrade;

	public Classes() {

	}

	public Classes(String name, String semesterName, List<Category> categories) {
		mName = name;
		mSemester = semesterName;
		mCategories = categories;
	}

	public Classes(String name, String semesterName) {
		mName = name;
		mSemester = semesterName;
		mCategories = new ArrayList<Category>();
	}

	public Classes(long id, String name, String semesterName) {
		mId = id;
		mName = name;
		mSemester = semesterName;
		mCategories = new ArrayList<Category>();
	}

	public Classes(long classID, String semesterName, String name, double grade) {
		mId = classID;
		mName = name;
		mSemester = semesterName;
		mCategories = new ArrayList<Category>();
		mGrade = grade;
	}

	public Classes(long id, String name, String semesterName,
			List<Category> categories) {
		mId = id;
		mName = name;
		mSemester = semesterName;
		mCategories = categories;
	}

	/**
	 * @return the id for the class
	 */
	public long getId() {
		return mId;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		mId = id;
	}

	/**
	 * @return the name of the class
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @param name
	 *            the name to set for the class
	 */
	public void setName(String name) {
		mName = name;
	}

	/**
	 * @param semester
	 *            the name of the semester for the class
	 */
	public void setSemesterName(String semester) {
		mSemester = semester;
	}

	/**
	 * @return the name of the semester for the class
	 */
	public String getSemesterName() {
		return mSemester;
	}

	public List<Category> getCategoryList() {
		return mCategories;
	}

	public void setCategoryList(List<Category> list) {
		mCategories = list;
	}

	public Category getCategory(int position) {
		return mCategories.get(position);
	}

	public void addCategory(Category category) {
		mCategories.add(category);
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
