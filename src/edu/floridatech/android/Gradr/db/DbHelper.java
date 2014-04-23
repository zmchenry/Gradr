package edu.floridatech.android.Gradr.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.floridatech.android.Gradr.model.Assignment;
import edu.floridatech.android.Gradr.model.Category;
import edu.floridatech.android.Gradr.model.Classes;
import edu.floridatech.android.Gradr.model.Semester;

public class DbHelper extends SQLiteOpenHelper {
	private static final String LOG = "DatabaseHelper";

	private static final String DB_NAME = "Gradebook.db";
	private static final int SCHEMA_VERSION = 1;

	private static final String TABLE_SEMESTER = "semesters";
	private static final String KEY_SEMESTER_NAME = "semester_name";

	private static final String TABLE_CLASSES = "classes";
	private static final String KEY_CLASS_NAME = "class_name";

	private static final String TABLE_ASSIGNMENTS = "assignments";

	private static final String KEY_ASSIGNMENT_NAME = "assignment_name";
	private static final String KEY_GRADE_RECIEVED = "grade_recieved";

	private static final String TABLE_CATEGORIES = "categories";
	private static final String KEY_CATEGORY_NAME = "category_name";
	private static final String KEY_CATEGORY_WEIGHT = "category_weight";

	private static final String KEY_ID = "_id";

	private static final String SQL_CREATE_SEMESTER_TABLE = "CREATE TABLE "
			+ TABLE_SEMESTER + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_SEMESTER_NAME
			+ " TEXT NOT NULL, " + KEY_GRADE_RECIEVED + " TEXT)";;

	private static final String SQL_CREATE_CLASSES_TABLE = "CREATE TABLE "
			+ TABLE_CLASSES + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_SEMESTER_NAME
			+ " TEXT NOT NULL, " + KEY_CLASS_NAME + " TEXT NOT NULL, "
			+ KEY_GRADE_RECIEVED + " TEXT)";

	private static final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE "
			+ TABLE_CATEGORIES + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CLASS_NAME
			+ " TEXT NOT NULL, " + KEY_CATEGORY_NAME + " TEXT NOT NULL, "
			+ KEY_CATEGORY_WEIGHT + " TEXT, " + KEY_GRADE_RECIEVED + " TEXT)";

	private static final String SQL_CREATE_ASSIGNMENTS_TABLE = "CREATE TABLE "
			+ TABLE_ASSIGNMENTS + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CLASS_NAME
			+ " TEXT NOT NULL, " + KEY_CATEGORY_NAME + " TEXT NOT NULL, "
			+ KEY_ASSIGNMENT_NAME + " TEXT NOT NULL, " + KEY_GRADE_RECIEVED
			+ " TEXT)";

	private static final String SQL_DROP_SEMESTER_TABLE = "DROP TABLE IF EXISTS "
			+ TABLE_SEMESTER;
	private static final String SQL_DROP_CLASSES_TABLE = "DROP TABLE IF EXISTS "
			+ TABLE_CLASSES;
	private static final String SQL_DROP_ASSIGNMENTS_TABLE = "DROP TABLE IF EXISTS "
			+ TABLE_ASSIGNMENTS;

	private static final String SQL_DROP_CATEGORY_TABLE = "DROP TABLE IF EXISTS"
			+ TABLE_CATEGORIES;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, SCHEMA_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_SEMESTER_TABLE);
		db.execSQL(SQL_CREATE_CLASSES_TABLE);
		db.execSQL(SQL_CREATE_CATEGORY_TABLE);
		db.execSQL(SQL_CREATE_ASSIGNMENTS_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DROP_SEMESTER_TABLE);
		db.execSQL(SQL_DROP_CLASSES_TABLE);
		db.execSQL(SQL_DROP_CATEGORY_TABLE);
		db.execSQL(SQL_DROP_ASSIGNMENTS_TABLE);
		onCreate(db);
	}

	/* BEGIN SEMSTER DATABASE SETUP */
	public long createSemester(Semester semester) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_SEMESTER_NAME, semester.getName());
		values.put(KEY_GRADE_RECIEVED, semester.getGrade());
		long id = db.insert(TABLE_SEMESTER, null, values);
		db.close();
		return id;
	}

	public Semester getSemester(long id) {
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_SEMESTER + " WHERE "
				+ KEY_ID + " = " + id;
		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Semester semester = new Semester();
		semester.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		semester.setName(c.getString(c.getColumnIndex(KEY_SEMESTER_NAME)));
		semester.setGrade(c.getDouble(c.getColumnIndex(KEY_GRADE_RECIEVED)));
		db.close();

		return semester;
	}

	public List<Semester> getSemesterList() {
		List<Semester> semesters = new ArrayList<Semester>();
		String selectQuery = "SELECT * FROM " + TABLE_SEMESTER;

		// Log.e(LOG, selectQuery);
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				Semester semester = new Semester();
				semester.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				semester.setName(c.getString(c
						.getColumnIndex(KEY_SEMESTER_NAME)));
				semester.setGrade(c.getDouble(c
						.getColumnIndex(KEY_GRADE_RECIEVED)));
				semesters.add(semester);
			} while (c.moveToNext());
		}
		return semesters;
	}

	public int updateSemester(Semester semester, String oldSemesterName) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, semester.getId());
		values.put(KEY_SEMESTER_NAME, semester.getName());
		values.put(KEY_GRADE_RECIEVED, semester.getGrade());
		int ret = db.update(TABLE_SEMESTER, values, KEY_ID + " = ?",
				new String[] { String.valueOf(semester.getId()) });
		if (ret > 0) {
			updateSemesterInClasses(semester.getName(), oldSemesterName);
		}
		return ret;

	}

	public void deleteSemester(long id) {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.e(LOG, TABLE_SEMESTER + ": " + KEY_ID + " - " + id);
		db.delete(TABLE_SEMESTER, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}

	/* END SEMSTER DATABASE SETUP */

	// /* BEGIN CLASSES DATABASE SETUP */
	public long createClass(Classes classObject) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_SEMESTER_NAME, classObject.getSemesterName());
		values.put(KEY_CLASS_NAME, classObject.getName());
		values.put(KEY_GRADE_RECIEVED, Double.toString(classObject.getGrade()));
		long id = db.insert(TABLE_CLASSES, null, values);
		db.close();
		return id;
	}

	public void updateSemesterInClasses(String newSemesterName,
			String oldSemesterName) {

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_SEMESTER_NAME, newSemesterName);
		db.update(TABLE_CLASSES, values, KEY_SEMESTER_NAME + " = ?",
				new String[] { oldSemesterName });
	}

	public Classes getClass(long id, String semesterName) {
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_CLASSES + " WHERE "
				+ KEY_ID + " = " + id + " AND " + KEY_SEMESTER_NAME + " = '"
				+ semesterName + "'";
		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Classes curClass = new Classes();
		curClass.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		curClass.setSemesterName(c.getString(c
				.getColumnIndex(KEY_SEMESTER_NAME)));
		curClass.setName(c.getString(c.getColumnIndex(KEY_CLASS_NAME)));
		curClass.setGrade(c.getDouble(c.getColumnIndex(KEY_GRADE_RECIEVED)));
		db.close();
		return curClass;
	}

	public List<Classes> getClassList(String semesterName) {
		List<Classes> classes = new ArrayList<Classes>();
		String selectQuery = "SELECT * FROM " + TABLE_CLASSES;

		// Log.e(LOG, selectQuery);
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				Classes classObj = new Classes();
				classObj.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				classObj.setSemesterName(c.getString(c
						.getColumnIndex(KEY_SEMESTER_NAME)));
				classObj.setName(c.getString(c.getColumnIndex(KEY_CLASS_NAME)));
				classObj.setGrade(c.getDouble(c
						.getColumnIndex(KEY_GRADE_RECIEVED)));
				if (classObj.getSemesterName().equals(semesterName)) {
					classes.add(classObj);
				}
			} while (c.moveToNext());
		}
		db.close();
		return classes;
	}

	public int updateClass(Classes classObj, String oldClassName) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, classObj.getId());
		values.put(KEY_SEMESTER_NAME, classObj.getSemesterName());
		values.put(KEY_CLASS_NAME, classObj.getName());
		values.put(KEY_GRADE_RECIEVED, Double.toString(classObj.getGrade()));
		values.put(KEY_GRADE_RECIEVED, classObj.getGrade());

		int ret = db.update(
				TABLE_CLASSES,
				values,
				KEY_ID + " = ? AND " + KEY_SEMESTER_NAME + " = ?",
				new String[] { String.valueOf(classObj.getId()),
						classObj.getSemesterName() });

		if (ret > 0) {
			updateCategoryInClass(classObj.getName(), oldClassName);
		}
		return ret;
	}

	public void deleteClass(long id, String semesterName) {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.e(LOG, TABLE_CLASSES + ": " + KEY_ID + " - " + id + " and "
				+ KEY_SEMESTER_NAME + " - " + semesterName);
		db.delete(TABLE_CLASSES, KEY_ID + " = ? AND " + KEY_SEMESTER_NAME
				+ " = ?", new String[] { String.valueOf(id), semesterName });
		db.close();
	}

	/* END CLASSES DATABASE SETUP */

	/* BEGIN CATEGORY DATABASE SETUP */

	public long createCategory(Category category) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CLASS_NAME, category.getClassName());
		values.put(KEY_CATEGORY_NAME, category.getCategory());
		values.put(KEY_CATEGORY_WEIGHT, category.getWeight());
		values.put(KEY_GRADE_RECIEVED, category.getGrade());
		long id = db.insert(TABLE_CATEGORIES, null, values);
		db.close();
		return id;
	}

	public Category getCategory(long id, String className) {
		String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE "
				+ KEY_ID + " = " + id + " AND " + KEY_CLASS_NAME + " = '"
				+ className + "'";
		Log.e(LOG, selectQuery);

		// Log.e(LOG, selectQuery);
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		Category category = new Category();
		category.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		category.setClassName(c.getString(c.getColumnIndex(KEY_CLASS_NAME)));
		category.setCategory(c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)));
		category.setItems(getAssignments(category.getClassName(),
				category.getCategory()));
		category.setWeight(c.getString(c.getColumnIndex(KEY_CATEGORY_WEIGHT)));
		category.setGrade(c.getDouble(c.getColumnIndex(KEY_GRADE_RECIEVED)));
		db.close();
		return category;
	}

	public ArrayList<Category> getCategoryList(String className) {
		String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE "
				+ KEY_CLASS_NAME + " = '" + className + "'";
		Log.e(LOG, selectQuery);

		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		ArrayList<Category> categories = new ArrayList<Category>();
		if (c.moveToFirst()) {
			do {
				Category category = new Category();
				category.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				category.setClassName(c.getString(c
						.getColumnIndex(KEY_CLASS_NAME)));
				category.setCategory(c.getString(c
						.getColumnIndex(KEY_CATEGORY_NAME)));
				category.setWeight(c.getString(c
						.getColumnIndex(KEY_CATEGORY_WEIGHT)));
				category.setGrade(c.getDouble(c
						.getColumnIndex(KEY_GRADE_RECIEVED)));
				categories.add(category);
			} while (c.moveToNext());
		}
		db.close();
		return categories;
	}

	public int updateCategory(Category category, String oldCategoryName) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, category.getId());
		values.put(KEY_CLASS_NAME, category.getClassName());
		values.put(KEY_CATEGORY_NAME, category.getCategory());
		values.put(KEY_CATEGORY_WEIGHT, category.getWeight());
		values.put(KEY_GRADE_RECIEVED, category.getGrade());
		int ret = db.update(
				TABLE_CATEGORIES,
				values,
				KEY_ID + " = ? AND " + KEY_CLASS_NAME + " = ?",
				new String[] { String.valueOf(category.getId()),
						category.getClassName() });
		if (ret > 0) {
			updateAssignmentInCategory(category.getClassName(),
					category.getCategory(), oldCategoryName);
		}
		return ret;
	}

	public void updateCategoryInClass(String newClassName, String oldClassName) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_CLASS_NAME, newClassName);
		db.update(TABLE_CATEGORIES, values, KEY_CLASS_NAME + " = ?",
				new String[] { oldClassName });
		updateClassInAssignments(newClassName, oldClassName);
	}

	public void updateAssignmentInCategory(String className,
			String newCategoryName, String oldCategoryName) {
		SQLiteDatabase db = getWritableDatabase();
		Log.e(LOG, "Update Assignments in " + TABLE_ASSIGNMENTS + " where "
				+ KEY_CLASS_NAME + " = " + className + " AND "
				+ KEY_CATEGORY_NAME + " = " + oldCategoryName);
		ContentValues values = new ContentValues();
		values.put(KEY_CATEGORY_NAME, newCategoryName);
		db.update(TABLE_ASSIGNMENTS, values, KEY_CLASS_NAME + " = ? AND "
				+ KEY_CATEGORY_NAME + " = ?", new String[] { className,
				oldCategoryName });
	}

	public void deleteCategory(Category category) {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.e(LOG, TABLE_CATEGORIES + ": " + KEY_ID + " - " + category.getId()
				+ " and " + KEY_CLASS_NAME + " - " + category.getClassName()
				+ " and " + KEY_CATEGORY_NAME + " - " + category.getCategory());
		db.delete(TABLE_CATEGORIES, KEY_ID + " = ? AND " + KEY_CLASS_NAME
				+ " = ? AND " + KEY_CATEGORY_NAME + " = ?", new String[] {
				String.valueOf(category.getId()), category.getClassName(),
				category.getCategory() });
		db.delete(TABLE_ASSIGNMENTS, KEY_CATEGORY_NAME + " = ?",
				new String[] { category.getCategory() });
		db.close();
	}

	/* END CATEGORY DATABASE SETUP */

	/* BEGIN ASSIGNMENTS DATABASE SETUP */

	public long createAssignment(Assignment assignment) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CLASS_NAME, assignment.getClassName());
		values.put(KEY_CATEGORY_NAME, assignment.getCategoryName());
		values.put(KEY_ASSIGNMENT_NAME, assignment.getAssignmentName());
		values.put(KEY_GRADE_RECIEVED, assignment.getGradeRecieved());
		long id = db.insert(TABLE_ASSIGNMENTS, null, values);
		db.close();
		return id;
	}

	public int updateAssignment(Assignment assignment) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, assignment.getId());
		values.put(KEY_CLASS_NAME, assignment.getClassName());
		values.put(KEY_CATEGORY_NAME, assignment.getCategoryName());
		values.put(KEY_ASSIGNMENT_NAME, assignment.getAssignmentName());
		values.put(KEY_GRADE_RECIEVED, assignment.getGradeRecieved());
		return db
				.update(TABLE_ASSIGNMENTS,
						values,
						KEY_ID + " = ? AND " + KEY_CLASS_NAME + " = ? AND "
								+ KEY_CATEGORY_NAME + " = ?",
						new String[] { String.valueOf(assignment.getId()),
								assignment.getClassName(),
								assignment.getCategoryName() });
	}

	public List<Assignment> getAssignments(String className, String categoryName) {
		String selectQuery = "SELECT * FROM " + TABLE_ASSIGNMENTS + " WHERE "
				+ KEY_CLASS_NAME + " = '" + className + "' AND "
				+ KEY_CATEGORY_NAME + " = '" + categoryName + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		List<Assignment> assignments = new ArrayList<Assignment>();
		if (c.moveToFirst()) {
			do {
				Assignment assignment = new Assignment();
				assignment.setId(Long.parseLong(c.getString(c
						.getColumnIndex(KEY_ID))));
				assignment.setClassName(c.getString(c
						.getColumnIndex(KEY_CLASS_NAME)));
				assignment.setCategoryName(c.getString(c
						.getColumnIndex(KEY_CATEGORY_NAME)));
				assignment.setAssignmentName(c.getString(c
						.getColumnIndex(KEY_ASSIGNMENT_NAME)));
				String gradeRecieved = c.getString(c
						.getColumnIndex(KEY_GRADE_RECIEVED));
				assignment.setGradeRecieved(gradeRecieved);
				assignments.add(assignment);
			} while (c.moveToNext());
		}
		db.close();
		return assignments;
	}

	public void deleteAssignment(Assignment assignment) {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.e(LOG,
				TABLE_ASSIGNMENTS + ": " + KEY_ID + " - " + assignment.getId()
						+ " and " + KEY_CLASS_NAME + " - "
						+ assignment.getClassName() + " and "
						+ KEY_CATEGORY_NAME + " - "
						+ assignment.getCategoryName() + " and "
						+ KEY_ASSIGNMENT_NAME + " - "
						+ assignment.getAssignmentName());
		db.delete(
				TABLE_ASSIGNMENTS,
				KEY_ID + " = ? AND " + KEY_CLASS_NAME + " = ? AND "
						+ KEY_CATEGORY_NAME + " = ? AND " + KEY_ASSIGNMENT_NAME
						+ " = ?",
				new String[] { String.valueOf(assignment.getId()),
						assignment.getClassName(),
						assignment.getCategoryName(),
						assignment.getAssignmentName() });
		closeDatabase();
	}

	public void updateClassInAssignments(String newClass, String oldClass) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_CLASS_NAME, newClass);
		db.update(TABLE_ASSIGNMENTS, values, KEY_CLASS_NAME + " = ?",
				new String[] { oldClass });
	}

	/* END ASSIGNMENTS DATABASE SETUP */

	public void closeDatabase() {
		SQLiteDatabase db = getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

}
