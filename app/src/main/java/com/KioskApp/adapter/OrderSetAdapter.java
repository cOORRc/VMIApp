package com.KioskApp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.KioskApp.R;
import com.KioskApp.model.DataIMCSet;

import java.util.ArrayList;

public class OrderSetAdapter extends RecyclerView.Adapter<OrderSetAdapter.orderAdapterViewHolder>{
	private orderAdapterViewHolder evh;
	private OnItemClickListener Listener;
	private Context mContext;
	private static ArrayList<DataIMCSet> arrayList;
	private DataIMCSet currentOrder;

	public OrderSetAdapter(Context context, ArrayList<DataIMCSet> orderList){
		this.mContext = context;
		this.arrayList = orderList;
	}

	@NonNull
	@Override
	public orderAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_imc, parent, false);
		evh = new orderAdapterViewHolder(v, Listener);
//		evh = new MemuAdapterViewHolder(v, mListener);
		return evh;
	}

	@Override
	public void onBindViewHolder(@NonNull orderAdapterViewHolder holder, int position) {
		currentOrder = arrayList.get(position);
		Glide.with(mContext)
				.load(currentOrder.getImageMainResource())
				.centerInside()
				.placeholder(R.drawable.ic_add_photo)
				.into(holder.showImg_box);
		holder.show_code.setText(currentOrder.getTextMainResource());
	}

	@Override
	public int getItemCount() {
		return arrayList.size();
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		Listener = listener;
	}
	public interface OnItemClickListener {
		void onItemClick(int position, String tx_snp);
	}

	public static class orderAdapterViewHolder extends RecyclerView.ViewHolder{
		public TextView show_code;
		public ImageView showImg_box;
		public orderAdapterViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
			super(itemView);
			showImg_box = itemView.findViewById(R.id.image_menu);
			show_code = itemView.findViewById(R.id.text_menu);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listener != null) {
						int position = getAdapterPosition();
						String tx_snp = show_code.getText().toString();
						if (position != RecyclerView.NO_POSITION) {
							listener.onItemClick(position, tx_snp);
						}
					}
				}
			});
		}
	}

}
