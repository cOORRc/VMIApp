package com.KioskApp.model;

public class DataAlertUsageCom {
	private String usCom_tx_FGCompo;
	private String usCom_tx_PartUsage;
	private String usCom_tx_PartPacking;
	private String usCom_tx_PartStock;
	private String usCom_tx_PartQty;
	private String usCom_tx_PartPicker;
	private String usCom_tx_fg_code_gdj;
	private String usCom_tx_fg_code_set_abt;

	public DataAlertUsageCom(String part_fg, String part_usage, String part_packing, String part_stock,
							 String part_qty, String part_pic, String us_com_tx_fg_code_gdj, String us_com_tx_fg_code_set_abt) {
		usCom_tx_FGCompo = part_fg;
		usCom_tx_PartUsage = part_usage;
		usCom_tx_PartPacking = part_packing;
		usCom_tx_PartStock = part_stock;
		usCom_tx_PartQty = part_qty;
		usCom_tx_PartPicker = part_pic;

		usCom_tx_fg_code_gdj = us_com_tx_fg_code_gdj;
		usCom_tx_fg_code_set_abt = us_com_tx_fg_code_set_abt;

	}
	public String getUsCom_TextFGCompo() {
		return usCom_tx_FGCompo;
	}
	public String getUsCom_TextPartUsage() {
		return usCom_tx_PartUsage;
	}
	public String getUsCom_TextPartPacking() {
		return usCom_tx_PartPacking;
	}
	public String getUsCom_TextPartStock() {
		return usCom_tx_PartStock;
	}
	public String getUsCom_TextPartQty() {
		return usCom_tx_PartQty;
	}
	public String getUsCom_TextPartPicker(){
		return usCom_tx_PartPicker;
	}

	public String getUsCom_tx_fg_code_gdj() {
		return usCom_tx_fg_code_gdj;
	}
	public String getUsCom_tx_fg_code_set_abt() {
		return usCom_tx_fg_code_set_abt;
	}
}

