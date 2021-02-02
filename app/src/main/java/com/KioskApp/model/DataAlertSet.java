package com.KioskApp.model;

public class DataAlertSet {
	private String TextPartSet;
	private String TextPartUsage;
	private String TextPartPacking;
	private String TextPartQty;

	private String TextFgCodeGDJ;
	public DataAlertSet(String part_set, String part_usage, String part_packing, String part_qty, String part_fg_code_gdj) {
		TextPartSet = part_set;
		TextPartUsage = part_usage;
		TextPartPacking = part_packing;
		TextPartQty = part_qty;

		TextFgCodeGDJ = part_fg_code_gdj;
	}
	public String getTextPartSet() {
		return TextPartSet;
	}
	public String getTextPartUsage() {
		return TextPartUsage;
	}
	public String getTextPartPacking() {
		return TextPartPacking;
	}
	public String getTextPartQty() {
		return TextPartQty;
	}
	public String getTextFgCodeGDJ() {
		return TextFgCodeGDJ;
	}
}
