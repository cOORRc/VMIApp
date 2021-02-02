package com.KioskApp.model;

public class DataIMCSet {
	private String mImageMainResource;
	private String mTextMainResource;

	public DataIMCSet(String imageResource, String textResource) {
		mImageMainResource = imageResource;
		mTextMainResource = textResource;
	}
	public String getImageMainResource() {
		return mImageMainResource;
	}
	public void setImageMainResource(String imageResource) {
		this. mImageMainResource = imageResource;
	}
	public String getTextMainResource() {
		return mTextMainResource;
	}
	public void setTextMainResource(String textResource) {
		this.mTextMainResource = textResource;
	}
}
