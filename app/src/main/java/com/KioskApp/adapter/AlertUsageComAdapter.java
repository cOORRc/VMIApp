package com.KioskApp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.KioskApp.R;
import com.KioskApp.model.DataAlertUsageCom;

import java.util.ArrayList;

public class AlertUsageComAdapter extends RecyclerView.Adapter<AlertUsageComAdapter.ViewHolder> {
	private Context mContext;
	private static ArrayList<DataAlertUsageCom> alertUsageComArrayList;
	public static String[] mColors = {"#ffffff", "#E1EFEFEF"};

	public AlertUsageComAdapter(Context context, ArrayList<DataAlertUsageCom> list_usageCom) {
		this.alertUsageComArrayList = list_usageCom;
		this.mContext = context;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_usage_com, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		DataAlertUsageCom dataAlertUsageCom = alertUsageComArrayList.get(position);
		holder.itemView.setBackgroundColor(Color.parseColor(mColors[position % 2])); // 4 can be replaced by mColors.length
		holder.sh_us_com_PartCom.setText(dataAlertUsageCom.getUsCom_TextFGCompo());
		holder.sh_us_com_PartUsage.setText(dataAlertUsageCom.getUsCom_TextPartUsage());
		holder.sh_us_com_PartPacking.setText(dataAlertUsageCom.getUsCom_TextPartPacking());
		holder.sh_us_com_part_stock.setText(dataAlertUsageCom.getUsCom_TextPartStock());
		holder.sh_us_com_PartQty.setText(dataAlertUsageCom.getUsCom_TextPartQty());

		holder.hid_usCom_tx_fg_code_gdj.setText(dataAlertUsageCom.getUsCom_tx_fg_code_gdj());
		holder.hid_usCom_tx_fg_code_set_abt.setText(dataAlertUsageCom.getUsCom_tx_fg_code_set_abt());
		holder.sh_us_com_ed_picker.setText("0");
		holder.sh_us_com_add.setBackgroundResource(R.drawable.ic_add);
		holder.sh_us_com_minus.setBackgroundResource(R.drawable.ic_minus);


/*		if (holder.sh_us_com_PartUsage.getText() == "null" || holder.sh_us_com_PartUsage.getText() == "") {
			holder.sh_us_com_add.setEnabled(false);
			holder.sh_us_com_minus.setEnabled(false);
			holder.sh_us_com_ed_picker.setEnabled(false);
		}*/
	}

	@Override
	public int getItemCount() {
		return alertUsageComArrayList.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView sh_us_com_PartCom, sh_us_com_PartUsage, sh_us_com_PartPacking,
				sh_us_com_part_stock, sh_us_com_PartQty;
		public Button sh_us_com_minus, sh_us_com_add;
		public EditText sh_us_com_ed_picker;
		public TextView hid_usCom_tx_fg_code_gdj, hid_usCom_tx_fg_code_set_abt;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			sh_us_com_PartCom = itemView.findViewById(R.id.al_us_snp_com_tv_part);
			sh_us_com_PartUsage = itemView.findViewById(R.id.al_us_snp_com_tv_usage);
			sh_us_com_PartPacking = itemView.findViewById(R.id.al_us_snp_com_tv_packing_snp);
			sh_us_com_part_stock = itemView.findViewById(R.id.al_us_snp_com_tv_stock);
			sh_us_com_PartQty = itemView.findViewById(R.id.al_us_snp_com_tv_qty);
			sh_us_com_add = itemView.findViewById(R.id.al_us_snp_com_number_add);
			sh_us_com_minus = itemView.findViewById(R.id.al_us_snp_com_number_minus);
			sh_us_com_ed_picker = itemView.findViewById(R.id.al_us_snp_com_num_cus);
			hid_usCom_tx_fg_code_gdj = itemView.findViewById(R.id.tx_fg_code_gdj_usCom);
			hid_usCom_tx_fg_code_set_abt = itemView.findViewById(R.id.tx_fg_code_set_abt_usCom);
			Log.e("TAG", "minus : "+sh_us_com_PartPacking.getText().toString());
			sh_us_com_minus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						int num_packing = Integer.parseInt(sh_us_com_PartPacking.getText().toString().trim());
						int int_qty = Integer.parseInt(sh_us_com_PartQty.getText().toString().trim());
						String str_picker = sh_us_com_ed_picker.getText().toString().trim();
						String str_stock = sh_us_com_part_stock.getText().toString().trim();
						int int_stock = Integer.parseInt(str_stock);
						int position = getAdapterPosition();
						Log.e("TAG", "minus : "+num_packing+", "+int_qty);
						minusEvent(num_packing, int_qty, sh_us_com_ed_picker, str_picker, position);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			});
			sh_us_com_add.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						int num_packing = Integer.parseInt(sh_us_com_PartPacking.getText().toString().trim());
						int int_qty = Integer.parseInt(sh_us_com_PartQty.getText().toString().trim());
						String str_picker = sh_us_com_ed_picker.getText().toString().trim();
						String str_stock = sh_us_com_part_stock.getText().toString().trim();
						int int_stock = Integer.parseInt(str_stock);
						int position = getAdapterPosition();
						Log.e("TAG", "add : "+num_packing+", "+int_qty);
						addEvent(num_packing, str_picker, int_stock,position);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			});
		}

		private int data_integer = 0;
		private void minusEvent(int num_packing, int int_qty, EditText sh_us_com_ed_picker, String str_picker, int position) {
			int int_num_pic = Integer.parseInt(str_picker);
			data_integer = int_num_pic - 1;
			String picker = String.valueOf(data_integer);
			sh_us_com_ed_picker.setText(picker);
			int int_picker = Integer.parseInt(sh_us_com_ed_picker.getText().toString());
			if (int_picker >= 0) {
				int result_num_minus = int_qty - num_packing;
				sh_us_com_PartQty.setText(String.valueOf(result_num_minus));
				itemView.setBackgroundColor(Color.parseColor(mColors[position % 2]));
				sh_us_com_add.setEnabled(true);
			} else {
				sh_us_com_add.setEnabled(true);
				sh_us_com_ed_picker.getText().clear();
				sh_us_com_ed_picker.setText(String.valueOf(0));
				sh_us_com_PartQty.setText(String.valueOf(0));

			}
		}

		private void addEvent(int num_packing, String str_picker, int int_stock, int position) {
			int int_num_pic = Integer.parseInt(str_picker);
			data_integer = int_num_pic + 1;
			String picker = String.valueOf(data_integer);
			sh_us_com_ed_picker.setText(picker);
			int result_num_add = Integer.parseInt(picker);
			data_integer = num_packing * result_num_add;
			sh_us_com_PartQty.setText(String.valueOf(data_integer));

			Log.e("TAG", "addEvent : "+int_num_pic+", "+result_num_add +", "+data_integer + ", "+int_stock);
			if (int_stock > data_integer) {
				Log.e("TAG", "addEvent 2 : "+int_num_pic+", "+result_num_add +", "+data_integer + ", "+int_stock);
				sh_us_com_add.setEnabled(true);
				itemView.setBackgroundColor(Color.parseColor(mColors[position % 2]));
			}
			if (int_stock <= data_integer) {
				Log.e("TAG", "addEvent 2 : "+int_num_pic+", "+result_num_add +", "+data_integer + ", "+int_stock);
				sh_us_com_add.setEnabled(false);
				itemView.setBackgroundColor(0xFFFF8F6F);
			}
			else {
				sh_us_com_add.setEnabled(true);
				itemView.setBackgroundColor(Color.parseColor(mColors[position % 2]));
			}
		}
	}
}
