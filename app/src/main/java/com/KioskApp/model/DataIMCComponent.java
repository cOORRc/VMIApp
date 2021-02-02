package com.KioskApp.model;

public class DataIMCComponent {
	private String TextPartNumModel;
	private String TextPartQTYModel;
	private String TextShipTypeModel;

	public DataIMCComponent(String part_number, String part_qty, String part_shiptype) {
		TextPartNumModel = part_number;
		TextPartQTYModel = part_qty;
		TextShipTypeModel = part_shiptype;

	}
	public String getTextPartNumberCom() {
		return TextPartNumModel;
	}
	public String getTextPartQTYModel() {
		return TextPartNumModel;
	}
	public String getTextPartShipCom() {
		return TextShipTypeModel;
	}


}