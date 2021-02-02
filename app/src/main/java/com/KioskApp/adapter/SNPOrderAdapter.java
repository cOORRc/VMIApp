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
import com.KioskApp.model.DataSNPSet;

import java.util.ArrayList;

public class SNPOrderAdapter extends RecyclerView.Adapter<SNPOrderAdapter.snpAdapterViewHolder>{
	private snpAdapterViewHolder evh;
	private OnItemClickListener Listener;
	private Context mContext;
	private static ArrayList<DataSNPSet> arrayList;
	private DataSNPSet currentOrder;
	public String[] mColors = {"#ffffff","#E1EFEFEF"};

	public SNPOrderAdapter(Context context, ArrayList<DataSNPSet> orderList){
		this.mContext = context;
		this.arrayList = orderList;
	}

	@NonNull
	@Override
	public snpAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_list, parent, false);
		evh = new snpAdapterViewHolder(v, Listener);
		return evh;
	}

	@Override
	public void onBindViewHolder(@NonNull snpAdapterViewHolder holder, int position) {
		currentOrder = arrayList.get(position);
		holder.itemView.setBackgroundColor(Color.parseColor(mColors[position % 2])); // 4 can be replaced by mColors.length
		holder.show_code.setText(currentOrder.getTextPartNumber());
		holder.show_code_snp.setText(currentOrder.getTextPartSNP());
		holder.show_code_ship.setText(currentOrder.getTextPartShip());

	}

	@Override
	public int getItemCount() {
		return arrayList.size();
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		Listener = listener;
	}

	public void filterList(ArrayList<DataSNPSet> filteredList) {
		arrayList = filteredList;
		notifyDataSetChanged();
	}

    public interface OnItemClickListener {
		void onItemClick(int position, String str_partNumber, String str_snp, String str_ship);
	}

	public static class snpAdapterViewHolder extends RecyclerView.ViewHolder{
		public TextView show_code, show_code_snp, show_code_ship;
		public snpAdapterViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
			super(itemView);
			show_code = itemView.findViewById(R.id.text_menu);
			show_code_snp =itemView.findViewById(R.id.text_menu_snp);
			show_code_ship = itemView.findViewById(R.id.text_menu_ship);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listener != null) {
						int position = getAdapterPosition();
						String str_partNumber = show_code.getText().toString();
						String str_snp= show_code_snp.getText().toString();
						String str_ship = show_code_ship.getText().toString();
						System.out.println(str_snp+", "+str_ship);
						if (position != RecyclerView.NO_POSITION) {
							listener.onItemClick(position, str_partNumber, str_snp, str_ship);
						}
					}
				}
			});
		}
	}
}
