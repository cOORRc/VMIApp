package com.KioskApp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.KioskApp.R;
import com.KioskApp.model.DataAlertCom;
import com.KioskApp.model.DataIMCComponent;

import java.util.ArrayList;

public class AlertPartComAdapter extends RecyclerView.Adapter<AlertPartComAdapter.partComAdapterViewHolder> {
	private Context mContext;
	private static ArrayList<DataAlertCom> dataAlertComs;
	private DataIMCComponent currentPartCom;
	public String[] mColors = {"#ffffff", "#E1EFEFEF"};

	public AlertPartComAdapter(Context context, ArrayList<DataAlertCom> list) {
		this.dataAlertComs = list;
		this.mContext = context;
	}

	@NonNull
	@Override
	public partComAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_part_com, parent, false);
		return new partComAdapterViewHolder(v);
	}

	@Override
	public void onBindViewHolder(@NonNull partComAdapterViewHolder holder, int position) {
		DataAlertCom dataAlertCom = dataAlertComs.get(position);
		holder.itemView.setBackgroundColor(Color.parseColor(mColors[position % 2])); // 4 can be replaced by mColors.length
		holder.show_PartCom.setText(dataAlertCom.getTextPartCom());
		holder.show_PartUsage.setText(dataAlertCom.getTextPartUsage());
		holder.show_PartPacking.setText(dataAlertCom.getTextPartPacking());
		holder.show_PartQty.setText(dataAlertCom.getTextPartQty());
		holder.hidCom_PartFgCodeGDJ.setText(dataAlertCom.getEmerComTextFgCodeGDJ());
	}

	@Override
	public int getItemCount() {
		return dataAlertComs.size();
	}

	public static class partComAdapterViewHolder extends RecyclerView.ViewHolder {
		private TextView hidCom_PartFgCodeGDJ;
		public TextView show_PartCom, show_PartUsage, show_PartPacking, show_PartQty;
		public Button show_minus, show_add;
		public EditText show_ed_picker;

		public partComAdapterViewHolder(@NonNull View itemView) {
			super(itemView);
			show_PartCom = itemView.findViewById(R.id.alert_com_tv_part_com);
			show_PartUsage = itemView.findViewById(R.id.alert_com_tv_usage);
			show_PartPacking = itemView.findViewById(R.id.alert_com_tv_packing_snp);
			show_PartQty = itemView.findViewById(R.id.alert_com_tv_qty);
			show_add = itemView.findViewById(R.id.alert_com_number_add);
			show_minus = itemView.findViewById(R.id.alert_com_number_minus);
			show_ed_picker = itemView.findViewById(R.id.alert_com_number_customer);
			hidCom_PartFgCodeGDJ = itemView.findViewById(R.id.com_tx_fg_code_gdj_usCom);

			show_minus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						int num_packing = Integer.parseInt(show_PartPacking.getText().toString().trim());
						int int_qty = Integer.parseInt(show_PartQty.getText().toString().trim());
						String str_picker = show_ed_picker.getText().toString().trim();
						minusEvent(num_packing, int_qty, show_ed_picker, str_picker);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			});
			show_add.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						int num_packing = Integer.parseInt(show_PartPacking.getText().toString().trim());
						int int_qty = Integer.parseInt(show_PartQty.getText().toString().trim());
						String str_picker = show_ed_picker.getText().toString().trim();
						addEvent(num_packing, str_picker, show_ed_picker);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			});
		}

		private int data_integer = 0;

		private void minusEvent(int num_packing, int int_qty, EditText show_ed_picker, String str_picker) {
			int int_num_pic = Integer.parseInt(str_picker);
			data_integer = int_num_pic - 1;
			String picker = String.valueOf(data_integer);
			show_ed_picker.setText(picker);
			int int_picker = Integer.parseInt(show_ed_picker.getText().toString());
			if (int_picker >= 1 && int_picker != 0) {
				int result_num_minus = int_qty - num_packing;
				show_PartQty.setText(String.valueOf(result_num_minus));
			} else {
				show_ed_picker.getText().clear();
				show_ed_picker.setText(String.valueOf(0));
				show_PartQty.setText(String.valueOf(0));
			}
		}

		private void addEvent(int num_packing, String str_picker, EditText show_ed_picker) {
			int int_num_pic = Integer.parseInt(str_picker);
			data_integer = int_num_pic + 1;
			String picker = String.valueOf(data_integer);
			show_ed_picker.setText(picker);
			int result_num_add = Integer.parseInt(picker);
			data_integer = num_packing * result_num_add;
			show_PartQty.setText(String.valueOf(data_integer));
		}
	}
}
