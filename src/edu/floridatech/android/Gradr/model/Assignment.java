package edu.floridatech.android.Gradr.model;

public class Assignment {
	private long mId;
	private String mClassName;
	private String mCategoryName;
	private String mAssignmentName;
	private String mGradeRecieved;
	
	public Assignment(){};
	public Assignment(String name) {
		mAssignmentName = name;
	}
	public Assignment(String className, String categoryName, String name, String grade) {
		mClassName = className;
		mCategoryName = categoryName;
		mAssignmentName = name;
		mGradeRecieved = grade;
	}
	public Assignment(String className, String categoryName, String name) {
		mClassName = className;
		mCategoryName = categoryName;
		mAssignmentName = name;
	}
	public Assignment(String name, String gradeRecieved) {
		mAssignmentName = name;
		mGradeRecieved = gradeRecieved;
	}
	
	public void setId(long id) {
		mId = id;
	}
	
	public long getId() {
		return mId;
	}
	
	public void setCategoryName(String categoryName) {
		mCategoryName = categoryName;
	}
	
	public String getCategoryName() {
		return mCategoryName;
	}
	
	public void setAssignmentName(String assignmentName) {
		mAssignmentName = assignmentName;
	}
	
	public String getAssignmentName(){
		return mAssignmentName;
	}
	
	public void setGradeRecieved(String grade) {
		mGradeRecieved = grade;
	}
	
	public String getGradeRecieved() {
		return mGradeRecieved;
	}
	
	public String getClassName() {
		return mClassName;
	}
	
	public void setClassName(String className) {
		mClassName = className;
	}
}
