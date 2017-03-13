package com.maqiang.model;

public class Element {

	private String date;
	private double open;
	private double high;
	private double low;
	private double volume;
	private double close;
	
	private double predictClose;
	
	public Element(){}
	
	public Element(String date,double open,double high,double low,double volume,double close) {
		this.date=date;
		this.open=open;
		this.high=high;
		this.low=low;
		this.volume=volume;
		this.close=close;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}

	public double getPredictClose() {
		return predictClose;
	}

	public void setPredictClose(double predictClose) {
		this.predictClose = predictClose;
	}
	
	public String toString() {
		return date+"\t"+open+"\t"+high+"\t"+low+"\t"+volume+"\t"+close+"\t"+predictClose;
	}
	
}
