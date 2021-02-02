package com.KioskApp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KioskApp.R;
import com.KioskApp.model.DataOrder;

import java.util.ArrayList;

public class AllOrderListAdapter extends RecyclerView.Adapter<AllOrderListAdapter.allOrderListAdapterViewHolder> {
	private AllOrderListAdapter.allOrderListAdapterViewHolder evh;
	private Context mContext;
	private static ArrayList<DataOrder> arrayList;
	private DataOrder currentOrder;

	public AllOrderListAdapter(Context context, ArrayList<DataOrder> orderList){
		this.mContext = context;
		this.arrayList = orderList;
	}

	@NonNull
	@Override
	public allOrderListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_order, parent, false);
		evh = new AllOrderListAdapter.allOrderListAdapterViewHolder(v);
		return evh;
	}

	@Override
	public void onBindViewHolder(@NonNull final allOrderListAdapterViewHolder holder, @SuppressLint("RecyclerView") final int position) {
		currentOrder = arrayList.get(position);
		String pos = String.valueOf(position+1);
		holder.show_no.setText(pos);
		holder.show_qc_status.setText(currentOrder.getmTextQC_Status());
		holder.show_fg_code_gdj.setText(currentOrder.getmTextFG_CodeGdj());
		holder.show_sku_code_abt.setText(currentOrder.getmTextSKU_CodeABT());
		holder.show_qty_total.setText(currentOrder.getmTextQTY_total());
	}

	@Override
	public int getItemCount() {
		return arrayList.size();
	}

	public static class allOrderListAdapterViewHolder extends RecyclerView.ViewHolder{
		public TextView show_no, show_qc_status, show_fg_code_gdj, show_sku_code_abt, show_qty_total;
		public allOrderListAdapterViewHolder(@NonNull View itemView) {
			super(itemView);
			show_no = itemView.findViewById(R.id.order_la_tv_no);
			show_qc_status = itemView.findViewById(R.id.order_la_tv_qc_status);
			show_fg_code_gdj = itemView.findViewById(R.id.order_la_tv_fg_gdj);
			show_sku_code_abt = itemView.findViewById(R.id.order_la_tv_sku_abt);
			show_qty_total = itemView.findViewById(R.id.order_la_tv_qty_total);
		}
	}
}
