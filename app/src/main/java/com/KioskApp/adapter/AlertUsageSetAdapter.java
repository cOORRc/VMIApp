package com.KioskApp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KioskApp.R;
import com.KioskApp.model.DataAlertUsageSet;

import java.util.ArrayList;

public class AlertUsageSetAdapter extends RecyclerView.Adapter<AlertUsageSetAdapter.usageComAdapterViewHolder> {
	private Context mContext;
	private static ArrayList<DataAlertUsageSet> list_dataAlertUsageSets;
	public String[] mColors = {"#ffffff", "#E1EFEFEF"};

	public AlertUsageSetAdapter(Context context, ArrayList<DataAlertUsageSet> list) {
		this.list_dataAlertUsageSets = list;
		this.mContext = context;
	}

	@NonNull
	@Override
	public usageComAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_usage_set, parent, false);
		return new usageComAdapterViewHolder(v);
	}

	@Override
	public void onBindViewHolder(@NonNull usageComAdapterViewHolder holder, int position) {
		DataAlertUsageSet dataAlertUsageSet = list_dataAlertUsageSets.get(position);
		holder.itemView.setBackgroundColor(Color.parseColor(mColors[position % 2])); // 4 can be replaced by mColors.length
		holder.sh_usageSet_PartCom.setText(dataAlertUsageSet.getUsSetTextFGCompo());
		holder.sh_usageSet_PartUsage.setText(dataAlertUsageSet.getUsSetTextPartUsage());
		holder.sh_usageSet_PartPack.setText(dataAlertUsageSet.getUsSetTextPartPacking());
		holder.sh_usageSet_PartStock.setText(dataAlertUsageSet.getUsSetTextPartStock());
		holder.sh_usageSet_PartQty.setText(dataAlertUsageSet.getUsSetTextPartQty());

		holder.hid_usageSet_fg_code_gdj.setText(dataAlertUsageSet.getUsSetTextFGCodeGDJ());
		holder.hid_usageSet_fg_code_set_abt.setText(dataAlertUsageSet.getUsSetTextFGCodeSetABT());
	}

	@Override
	public int getItemCount() {
		return list_dataAlertUsageSets.size();
	}

	public static class usageComAdapterViewHolder extends RecyclerView.ViewHolder {
		public TextView sh_usageSet_PartCom, sh_usageSet_PartUsage, sh_usageSet_PartPack, sh_usageSet_PartStock, sh_usageSet_PartQty;
		public TextView hid_usageSet_fg_code_gdj, hid_usageSet_fg_code_set_abt;
		public usageComAdapterViewHolder(@NonNull View itemView) {
			super(itemView);
			sh_usageSet_PartCom = itemView.findViewById(R.id.al_us_snp_tv_part_com);
			sh_usageSet_PartUsage = itemView.findViewById(R.id.al_us_snp_tv_usage);
			sh_usageSet_PartPack = itemView.findViewById(R.id.al_us_snp_tv_packing_snp);
			sh_usageSet_PartStock = itemView.findViewById(R.id.al_us_snp_tv_vmi_stock);
			sh_usageSet_PartQty = itemView.findViewById(R.id.al_us_snp_tv_qty);

			hid_usageSet_fg_code_gdj = itemView.findViewById(R.id.tx_fg_code_gdj);
			hid_usageSet_fg_code_set_abt = itemView.findViewById(R.id.tx_fg_code_set_abt);
		}
	}
}
