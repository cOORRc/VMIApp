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
import com.KioskApp.model.DataAlertSet;
import com.KioskApp.model.DataIMCSet;

import java.util.ArrayList;

public class AlertPartSetAdapter extends RecyclerView.Adapter<AlertPartSetAdapter.partSetAdapterViewHolder>{
	private Context mContext;
	private static ArrayList<DataAlertSet> dataAlertSets;
	private DataIMCSet currentPartSet;
	public String[] mColors = {"#ffffff","#E1EFEFEF"};

	public AlertPartSetAdapter(Context context, ArrayList<DataAlertSet> list){
		this.dataAlertSets = list;
		this.mContext = context;
	}

	@NonNull
	@Override
	public partSetAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_part_set, parent, false);
		return new partSetAdapterViewHolder(v);
	}

	@Override
	public void onBindViewHolder(@NonNull partSetAdapterViewHolder holder, int position) {
		DataAlertSet dataAlertSet = dataAlertSets.get(position);
		holder.itemView.setBackgroundColor(Color.parseColor(mColors[position % 2])); // 4 can be replaced by mColors.length
		holder.show_PartSet.setText(dataAlertSet.getTextPartSet());
		holder.show_PartUsage.setText(dataAlertSet.getTextPartUsage());
		holder.show_PartPacking.setText(dataAlertSet.getTextPartPacking());
		holder.show_PartQty.setText(dataAlertSet.getTextPartQty());
		holder.hid_PartFgCodeGDJ.setText(dataAlertSet.getTextFgCodeGDJ());
	}

	@Override
	public int getItemCount() {
		return dataAlertSets.size();
	}

	public static class partSetAdapterViewHolder extends RecyclerView.ViewHolder{
		public TextView show_PartSet, show_PartUsage, show_PartPacking, show_PartQty, hid_PartFgCodeGDJ;
		public partSetAdapterViewHolder(@NonNull View itemView) {
			super(itemView);
			show_PartSet = itemView.findViewById(R.id.alert_tv_part_com);
			show_PartUsage = itemView.findViewById(R.id.alert_tv_usage);
			show_PartPacking = itemView.findViewById(R.id.alert_tv_packing_snp);
			show_PartQty = itemView.findViewById(R.id.alert_tv_qty);
			hid_PartFgCodeGDJ = itemView.findViewById(R.id.set_tx_fg_code_gdj_usCom);
		}
	}
}
