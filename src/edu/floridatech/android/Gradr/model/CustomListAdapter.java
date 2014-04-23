package edu.floridatech.android.Gradr.model;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.floridatech.android.Gradr.R;

public class CustomListAdapter<T> extends ArrayAdapter<T> {
	private Context mContext;
	private View mConvertView;
	private Typeface font;
	public CustomListAdapter(Context context, ArrayList<T> data) {
		super(context, R.layout.custom_list_item, data);
		mContext = context;
		font = Typeface.createFromAsset(context.getAssets(), "Gotham-Light.ttf");
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.custom_list_item, null);
			mConvertView = convertView;
		}

		TextView text = ((TextView) convertView.findViewById(R.id.custom_title));
		text.setTypeface(font);
		text.setText(getItem(position).toString());

		return convertView;
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
	public View getConvertView() {
		
		if(mConvertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mConvertView = inflater.inflate(R.layout.custom_list_item, null);
		}
		return mConvertView;
	}
}
