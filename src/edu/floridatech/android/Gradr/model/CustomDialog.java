package edu.floridatech.android.Gradr.model;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import edu.floridatech.android.Gradr.R;
import edu.floridatech.android.Gradr.db.DbHelper;

public class CustomDialog extends Dialog implements
		android.view.View.OnClickListener {
	private Button accept;
	private Button cancel;
	private Button delete;
	private String commitText;
	private DbHelper db;
	private ArrayList<Semester> list;
	private ArrayAdapter<Semester> adapter;
	private int position;

	public CustomDialog(Context context, DbHelper helper, ArrayList<Semester> data, ArrayAdapter<Semester> dataAdapter, int pos) {
		super(context);
		db = helper;
		list = data;
		adapter = dataAdapter;
		position = pos;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_dialog);
		accept = (Button) findViewById(R.id.accept);
		cancel = (Button) findViewById(R.id.cancel);
		delete = (Button) findViewById(R.id.delete);

		accept.setOnClickListener(this);
		cancel.setOnClickListener(this);
		delete.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.accept:
			EditText edit = (EditText) findViewById(R.id.dialog_edit_text);
			Semester semester = list.get(position);
			String oldName = semester.getName();
			semester.setName(edit.getText().toString());
			db.updateSemester(semester, oldName);
			list.set(position, semester);
			adapter.notifyDataSetChanged();
			break;
		case R.id.delete:
			db.deleteSemester(list.get(position).getId());
			list.remove(position);
			adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
		dismiss();
	}

	public String getCommit() {
		return commitText;
	}
}
