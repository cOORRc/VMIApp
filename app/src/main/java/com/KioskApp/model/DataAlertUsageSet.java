package com.KioskApp.model;

public class DataAlertUsageSet {
	private String usSetTextFGCompo;
	private String usSetTextPartUsage;
	private String usSetTextPartPacking;
	private String usSetTextPartStock;
	private String usSetTextPartQty;
	private String usSetTextFGCodeGDJ;
	private String usSetTextFGCodeSetABT;
	public DataAlertUsageSet(String part_fg, String part_usage, String part_packing,
							 String part_stock, String part_qty,String fg_code_gdj, String fg_code_set_abt) {
		usSetTextFGCompo = part_fg;
		usSetTextPartUsage = part_usage;
		usSetTextPartPacking = part_packing;
		usSetTextPartStock = part_stock;
		usSetTextPartQty = part_qty;

		usSetTextFGCodeGDJ = fg_code_gdj;
		usSetTextFGCodeSetABT = fg_code_set_abt;
	}


	public String getUsSetTextFGCompo() {
		return usSetTextFGCompo;
	}
	public String getUsSetTextPartUsage() {
		return usSetTextPartUsage;
	}
	public String getUsSetTextPartPacking() {
		return usSetTextPartPacking;
	}
	public String getUsSetTextPartStock(){
		return usSetTextPartStock;
	}
	public String getUsSetTextPartQty() {
		return usSetTextPartQty;
	}

	public String getUsSetTextFGCodeGDJ() {
		return usSetTextFGCodeGDJ;
	}

	public String getUsSetTextFGCodeSetABT() {
		return usSetTextFGCodeSetABT;
	}
}

