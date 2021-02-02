package com.KioskApp.model;

public class DataSNPSet {

	private String TextPartNumber;
	private String TextShipType;
	private String TextSNP;

	public DataSNPSet(String part_number, String part_snp, String part_shiptype) {
		TextPartNumber = part_number;
		TextSNP = part_snp;
		TextShipType = part_shiptype;

	}
	public String getTextPartNumber() {
		return TextPartNumber;
	}
	public String getTextPartSNP() {
		return TextSNP;
	}
	public String getTextPartShip() {
		return TextShipType;
	}


}
