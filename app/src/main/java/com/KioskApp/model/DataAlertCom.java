package com.KioskApp.model;

public class DataAlertCom {
	private String TextPartCom;
	private String TextPartUsage;
	private String TextPartPacking;
	private String TextPartQty;
	private String TextPicker;

	private String EmerComTextFgCodeGDJ;
	public DataAlertCom(String part_com, String part_usage, String part_packing,
						String part_qty, String part_picker, String part_fg_code_gdj) {
		TextPartCom = part_com;
		TextPartUsage = part_usage;
		TextPartPacking = part_packing;
		TextPartQty = part_qty;
		TextPicker = part_picker;
		EmerComTextFgCodeGDJ = part_fg_code_gdj;

	}

	public String getTextPartCom() {
		return TextPartCom;
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

	public String getTextPicker() {
		return TextPicker;
	}

	public String getEmerComTextFgCodeGDJ() {
		return EmerComTextFgCodeGDJ;
	}
}
