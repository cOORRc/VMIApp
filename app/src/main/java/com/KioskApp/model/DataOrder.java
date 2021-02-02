package com.KioskApp.model;

public class DataOrder {
	private String mTextFG_CodeGdj;
	private String mTextSKU_CodeABT;
	private String mTextQTY_total;
	private String mTextQC_Status;

	public DataOrder(String qc_status, String pre_fg_code_gdj_item, String pre_sku_code_abt_item, String qty_total_item) {
		mTextQC_Status = qc_status;
		mTextFG_CodeGdj = pre_fg_code_gdj_item;
		mTextSKU_CodeABT = pre_sku_code_abt_item;
		mTextQTY_total = qty_total_item;
	}

	public String getmTextQC_Status() {
		return mTextQC_Status;
	}
	public String getmTextFG_CodeGdj(){
		return mTextFG_CodeGdj;
	}
	public String getmTextSKU_CodeABT() {
		return mTextSKU_CodeABT;
	}
	public String getmTextQTY_total() {
		return mTextQTY_total;
	}


}
