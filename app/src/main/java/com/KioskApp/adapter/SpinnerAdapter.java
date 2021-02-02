package com.KioskApp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.KioskApp.R;
import com.KioskApp.model.DataSpin;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<DataSpin> {

	public SpinnerAdapter(@NonNull Context context, ArrayList<DataSpin> spinList) {
		super(context, 0,spinList);
	}
	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		return initView(position, convertView, parent);
	}
	@Override
	public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		return initView(position, convertView, parent);
	}
	private View initView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.layout_spinner_item, parent, false
			);
		}
		TextView textViewName = convertView.findViewById(R.id.text_spin);
		textViewName.setHint("aa");
		DataSpin currentItem = getItem(position);
		if (currentItem != null) {
			textViewName.setText(currentItem.getSpinner());
		}
		return convertView;
	}
}

