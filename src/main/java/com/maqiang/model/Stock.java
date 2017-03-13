package com.maqiang.model;

import java.util.ArrayList;
import java.util.List;

public class Stock {

	private List<Element> dataList;
	private List<Element> analyzeList;
	private List<Element> predictList;
	
	private double [] args;		//predict arguments vertex
	
	private double [][] x;
	private double [] y;
	
	private String code;
	private String name;
	
	
	public Stock(String code,String name) {
		this.code=code;
		this.name=name;
		this.dataList=new ArrayList<Element>();
		this.analyzeList=new ArrayList<Element>();
		this.predictList=new ArrayList<Element>();
	}

	public List<Element> getDataList() {
		return dataList;
	}

	public void setDataList(List<Element> dataList) {
		this.dataList = dataList;
	}

	public double[] getArgs() {
		return args;
	}

	public void setArgs(double[] args) {
		this.args = args;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double[][] getX() {
		if(dataList==null) 
			return null;
		x=new double[dataList.size()][5];
		for(int i=0;i<dataList.size();i++) {
			x[i][0]=1;
			x[i][1]=dataList.get(i).getOpen();
			x[i][2]=dataList.get(i).getHigh();
			x[i][3]=dataList.get(i).getLow();
			x[i][4]=dataList.get(i).getVolume();
		}
		return x;
	}

	public void setX(double[][] x) {
		this.x = x;
	}

	public double[] getY() {
		if(dataList==null)
			return null;
		y=new double[dataList.size()];
		for(int i=0;i<dataList.size();i++) {
			y[i]=dataList.get(i).getClose();
		}
		return y;
	}

	public void setY(double[] y) {
		this.y = y;
	}
	
	public double [][] getAnalyzeX() {
		x=new double[analyzeList.size()][5];
		for(int i=0;i<analyzeList.size();i++) {
			x[i][0]=1;
			x[i][1]=analyzeList.get(i).getOpen();
			x[i][2]=analyzeList.get(i).getHigh();
			x[i][3]=analyzeList.get(i).getLow();
			x[i][4]=analyzeList.get(i).getVolume();
		}
		return x;
	}
	public double []getAnalyzeY() {
		y=new double[analyzeList.size()];
		for(int i=0;i<analyzeList.size();i++) {
			y[i]=analyzeList.get(i).getClose();
		}
		return y;
	}
	
	public List<Element> getAnalyzeList(){
		return analyzeList;
	}
	
	public List<Element> getPredictList(){
		return predictList;
	}
	
	public String toString() {
		return code+" "+name;
	}
}
