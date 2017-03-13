package com.maqiang.preprocess;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import Jama.Matrix;

import com.maqiang.matrix.Driver;
import com.maqiang.matrix.MatrixMapper;
import com.maqiang.matrix.MatrixReducer;
import com.maqiang.model.Element;
import com.maqiang.model.Stock;

public class PreProcess {

	public static boolean fileToMatrix(Stock stock,Configuration conf){
		boolean result=true;
		String path="hdfs://master:9000/user/stock/data/"+stock.getCode()+","+stock.getName();
		try {
			FileSystem fs=FileSystem.get((URI.create(path)),conf);
			InputStream is=fs.open(new Path(path));
			List<String> strList=IOUtils.readLines(is);
			strList.remove(0);
			for(String str:strList) {
				String [] values=str.split(",");
				Element elem=new Element();
				elem.setDate(values[0]);
				elem.setOpen(Double.valueOf(values[1]));
				elem.setHigh(Double.valueOf(values[2]));
				elem.setLow(Double.valueOf(values[3]));
				elem.setVolume(Double.valueOf(values[4]));
				elem.setClose(Double.valueOf(values[5]));
				
				stock.getDataList().add(elem);
			}

			is.close();
			for(int i=stock.getDataList().size()-1;i>stock.getDataList().size()*0.2;i--) {
				stock.getAnalyzeList().add(stock.getDataList().get(i));
			}
			for(int i=0;i<=stock.getDataList().size()*0.2;i++) {
				stock.getPredictList().add(stock.getDataList().get(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
			result=false;
		}
		return result;
	}
	
	 //矩阵转置
	public static double [][] matrixTranspose(double [][] x) {
		Matrix m=new Matrix(x);
		return m.transpose().getArray();
	}
	
	//矩阵求逆
	public static double [][] matrixInverse(double [][] x) {
		Matrix m=new Matrix(x);
		return m.inverse().getArray();
	}
	
	//将矩阵转化成预输入文件形式
	public static boolean matrixPreInput(double [][]x,String file,Configuration conf) {
		boolean result=true;
		String source;
		try {
			source="hdfs://master:9000/user/stock/input/"+file;
			FileSystem fs=FileSystem.get(URI.create(source),conf);
			OutputStream os=fs.create(new Path(source));
			for(int i=0;i<x.length;i++) {
				for(int j=0;j<x[i].length;j++) {
					IOUtils.write(String.valueOf(i+1)+"\t"+String.valueOf(j+1)+"\t"+x[i][j]+"\n",os);
				}
			}
			os.flush();
			os.close();
		}catch(Exception e) {
			e.printStackTrace();
			result=false;
		}
		return result;
	}
	
	public static boolean matrixPreInput(double []y,String file,Configuration conf) {
		boolean result=true;
		String source;
		try {
			source="hdfs://master:9000/user/stock/input/"+file;
			FileSystem fs=FileSystem.get(URI.create(source),conf);
			OutputStream os=fs.create(new Path(source));
			for(int i=0;i<y.length;i++) {
				IOUtils.write(String.valueOf(i+1)+"\t"+"1\t"+y[i]+"\n", os);
			}
			os.flush();
			os.close();
		}catch(Exception e) {
			e.printStackTrace();
			result=false;
		}
		return result;
	}
	
	//reduce输出转化成矩阵
	public static double [][] reduceToMatrix(Configuration conf) {
		double [][]matrix=null;
		String path="hdfs://master:9000/user/stock/output/part-r-00000";
		try {
			FileSystem fs=FileSystem.get(URI.create(path),conf);
			if(!fs.exists(new Path(path)))
				return null;
			InputStream is=fs.open(new Path(path));
			List<String> strList=IOUtils.readLines(is);
//			String lastValue=strList.get(strList.size()-1);
//			String [] values=lastValue.split("\t");
			int maxV1=0;
			int maxV2=0;
			for(String str:strList) {
				String [] v=str.split("\t");
				int v1=Integer.valueOf(v[0]);
				int v2=Integer.valueOf(v[1]);
				if(v1>maxV1)
					maxV1=v1;
				if(v2>maxV2)
					maxV2=v2;
			}
			matrix=new double[maxV1][maxV2];
			for(String str:strList) {
				String []values=str.split("\t");
				matrix[Integer.valueOf(values[0])-1][Integer.valueOf(values[1])-1]=Double.valueOf(values[2]);
			}
			is.close();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return matrix;
	}
	
	public static void main(String [] args) throws IOException,ClassNotFoundException,
			InterruptedException{
		Stock stock=new Stock("000155","*ST川化");
		Configuration conf=new Configuration();
		fileToMatrix(stock,conf);
		for(Element elem:stock.getDataList()) {
			System.out.println(elem);
		}

		double [][] x=stock.getX();
		double []y=stock.getY();
	
//		matrixPreInput(x,"input_a",conf);
		double [][] t=matrixInverse(x);
		
//		matrixPreInput(t,"input_b",conf);

//		conf.setInt("m", Integer.valueOf(x.length));
//		conf.setInt("n", Integer.valueOf(t.length));
//		conf.setInt("l", Integer.valueOf(x.length));
		
//		Job job=new Job(conf,"matrix_multi");
//		
//		job.setJarByClass(PreProcess.class);
//		job.setMapperClass(MatrixMapper.class);
//		job.setReducerClass(MatrixReducer.class);
//		job.setOutputKeyClass(Text.class);
//		job.setOutputValueClass(Text.class);
//		
//		FileInputFormat.addInputPath(job, new Path("hdfs://master:9000/user/stock/input"));
//		FileOutputFormat.setOutputPath(job,new Path("hdfs://master:9000/user/stock/output"));
//		
//		System.exit(job.waitForCompletion(true)?0:1);
		
		String path="hdfs://master:9000/user/stock/output/part-r-00000";
		FileSystem fs=FileSystem.get(URI.create(path),conf);
		InputStream is=fs.open(new Path(path));
		IOUtils.copy(is, System.out);
		
		double [][]m=reduceToMatrix(conf);
		printArray(m);
	}
	
	public static void printArray(double [][]x) {
		for(int i=0;i<x.length;i++) {
			for(int j=0;j<x[i].length;j++) {
				System.out.print(x[i][j]+"  ");
			}
			System.out.println();
		}
	}
}
