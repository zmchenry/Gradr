package edu.floridatech.android.Gradr.model;

import java.util.ArrayList;
import java.util.List;

public class Category {
	private long mId;
	private String mClassName;
	private String mCategory;
	private String mWeight;
	private double mGrade;
	private List<Assignment> mItems;

	public Category() {
		mItems = new ArrayList<Assignment>();
	}

	public Category(String className, String category, String weight) {
		mClassName = className;
		mCategory = category;
		mItems = new ArrayList<Assignment>();
		mWeight = weight;
	}

	public Category(String className, String category,
			ArrayList<Assignment> items, String weight) {
		mClassName = className;
		mCategory = category;
		mItems = items;
		mWeight = weight;
		calculateGrade();
	}

	public Category(long classID, String classSemester, String className,
			double classGrade) {
		mId = classID;
	}

	public void setId(long id) {
		mId = id;
	}

	public long getId() {
		return mId;
	}

	public void setClassName(String className) {
		mClassName = className;
	}

	public String getClassName() {
		return mClassName;
	}

	public String getCategory() {
		return mCategory;
	}

	public List<Assignment> getItems() {
		return mItems;
	}

	public void setCategory(String category) {
		mCategory = category;
	}

	public void setItems(List<Assignment> items) {
		mItems = items;
		calculateGrade();
	}

	public void addItem(Assignment item) {
		mItems.add(item);
		calculateGrade();
	}
	
	public void setItem(int position, Assignment item) {
		mItems.set(position, item);
		calculateGrade();
	}
	
	public void delete(int position) {
		mItems.remove(position);
		calculateGrade();
	}

	public void removeItem(String item) {
		mItems.remove(item);
	}

	public Assignment getChild(int position) {
		return mItems.get(position);
	}

	public int getChildCount() {
		return mItems.size();
	}

	public String toString() {
		return mCategory;
	}

	public String getWeight() {
		return mWeight;
	}

	public void setWeight(String weight) {
		mWeight = weight;
	}

	public double getGrade() {
		calculateGrade();
		return mGrade;
	}

	public void setGrade(double grade) {
		mGrade = grade;
	}

	public void calculateGrade() {
		double total = 0;
		int count = 0;
		for (int i = 0; i < mItems.size(); i++) {
			String grade = mItems.get(i).getGradeRecieved();
			if (grade != null && !grade.equals("--")) {
				total += Double.parseDouble(mItems.get(i).getGradeRecieved());
				count++;
			}
		}
		if (count == 0) {
			mGrade = 100;
		} else {
			mGrade = (total / count);
		}
	}

}
